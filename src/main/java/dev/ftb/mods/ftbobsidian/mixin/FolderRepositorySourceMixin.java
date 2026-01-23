package dev.ftb.mods.ftbobsidian.mixin;

import dev.ftb.mods.ftbobsidian.config.StartupConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Debug(export = true)
@Mixin(Pack.class)
public class FolderRepositorySourceMixin {
    @Redirect(
            method = "readMetaAndCreate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/packs/repository/Pack;create(Ljava/lang/String;Lnet/minecraft/network/chat/Component;ZLnet/minecraft/server/packs/repository/Pack$ResourcesSupplier;Lnet/minecraft/server/packs/repository/Pack$Info;Lnet/minecraft/server/packs/PackType;Lnet/minecraft/server/packs/repository/Pack$Position;ZLnet/minecraft/server/packs/repository/PackSource;)Lnet/minecraft/server/packs/repository/Pack;"
            )
    )
    private static Pack modifyRequiredFlag(String id, Component title, boolean required, Pack.ResourcesSupplier resources, Pack.Info info, PackType packType, Pack.Position defaultPosition, boolean fixedPosition, PackSource packSource) {
        boolean modifiedRequired = required;
        if (!required && packType == PackType.CLIENT_RESOURCES && StartupConfig.FORCE_LOADED_RESOURCE_PACKS.get().contains(id)) {
            modifiedRequired = true;
        }

        return Pack.create(id, title, modifiedRequired, resources, info, packType, defaultPosition, fixedPosition, packSource);
    }
}
