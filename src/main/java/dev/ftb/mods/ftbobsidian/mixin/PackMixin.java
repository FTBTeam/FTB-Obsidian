package dev.ftb.mods.ftbobsidian.mixin;

import dev.ftb.mods.ftbobsidian.config.StartupConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Pack.class)
public class PackMixin {
    @Mutable
    @Shadow(aliases = {"f_10405_"})
    @Final
    private PackCompatibility compatibility;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void ftbobsidian$packConstructor(String id, boolean required, Pack.ResourcesSupplier resources, Component title, Pack.Info info, PackCompatibility compatibility, Pack.Position defaultPosition, boolean fixedPosition, PackSource packSource, CallbackInfo ci) {
        if (compatibility.isCompatible()) {
            return;
        }

        if (StartupConfig.FORCED_COMPATIBLE_RESOURCE_PACKS.get().contains(id)) {
            this.compatibility = PackCompatibility.COMPATIBLE;
        }
    }
}
