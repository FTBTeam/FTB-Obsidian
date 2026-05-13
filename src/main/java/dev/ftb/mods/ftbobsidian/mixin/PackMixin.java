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

import java.util.List;

@Mixin(Pack.class)
public class PackMixin {
    @Mutable @Shadow @Final private boolean required;

    @Mutable @Shadow @Final private Pack.Info info;

    @Inject(
            method = "<init>(Ljava/lang/String;ZLnet/minecraft/server/packs/repository/Pack$ResourcesSupplier;Lnet/minecraft/network/chat/Component;Lnet/minecraft/server/packs/repository/Pack$Info;Lnet/minecraft/server/packs/repository/Pack$Position;ZLnet/minecraft/server/packs/repository/PackSource;Ljava/util/List;)V",
            at = @At("RETURN")
    )
    private void ftbobsidian$modifyPack(String id, boolean required, Pack.ResourcesSupplier resources, Component title, Pack.Info info, Pack.Position position, boolean fixedPosition, PackSource packSource, List<Pack> children, CallbackInfo ci) {
        if (!info.compatibility().isCompatible()) {
            if (StartupConfig.FORCED_COMPATIBLE_RESOURCE_PACKS.get().contains(id)) {
                // Force compatibility for this pack
                this.info = new Pack.Info(info.description(), PackCompatibility.COMPATIBLE, info.requestedFeatures(), info.overlays(), info.isHidden());
            }
        }

        // Force load packs
        if (StartupConfig.FORCE_LOADED_RESOURCE_PACKS.get().contains(id)) {
            this.required = true;
        }
    }
}
