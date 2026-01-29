package dev.ftb.mods.ftbobsidian.mixin;

import dev.ftb.mods.ftbobsidian.config.StartupConfig;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Pack.class)
public class PackMixin {
    @Inject(method = "readPackMetadata", at = @At("RETURN"), cancellable = true)
    private static void onReadPackMetadata(PackLocationInfo location, Pack.ResourcesSupplier resources, int version, CallbackInfoReturnable<Pack.Metadata> cir) {
        Pack.Metadata returnValue = cir.getReturnValue();
        if (returnValue == null || returnValue.compatibility().isCompatible()) {
            // Don't do anything if the pack is compatible or has no metadata
            return;
        }

        var id  = location.id();
        if (StartupConfig.FORCED_COMPATIBLE_RESOURCE_PACKS.get().contains(id)) {
            // Replace the return value with a compatible metadata
            var fixedMeta = new Pack.Metadata(returnValue.description(), PackCompatibility.COMPATIBLE, returnValue.requestedFeatures(), returnValue.overlays(), returnValue.isHidden());

            cir.setReturnValue(fixedMeta);
        }
    }
}
