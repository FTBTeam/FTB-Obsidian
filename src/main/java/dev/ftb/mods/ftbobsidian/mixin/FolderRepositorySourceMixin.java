package dev.ftb.mods.ftbobsidian.mixin;

import dev.ftb.mods.ftbobsidian.config.StartupConfig;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FolderRepositorySource.class)
public class FolderRepositorySourceMixin {
    @Shadow
    @Final
    private PackType packType;

    @Unique
    private static final PackSelectionConfig FORCE_LOADED_CONFIG = new PackSelectionConfig(true, Pack.Position.TOP, false);

    @Unique
    private static final PackSelectionConfig FORCE_LOADED_CONFIG_TOP = new PackSelectionConfig(true, Pack.Position.TOP, true);

    @ModifyArg(
            method = "lambda$loadPacks$0",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/Pack;readMetaAndCreate(Lnet/minecraft/server/packs/PackLocationInfo;Lnet/minecraft/server/packs/repository/Pack$ResourcesSupplier;Lnet/minecraft/server/packs/PackType;Lnet/minecraft/server/packs/PackSelectionConfig;)Lnet/minecraft/server/packs/repository/Pack;"),
            index = 3
    )
    private PackSelectionConfig modifyPackSelectionConfig(PackSelectionConfig selectionConfig, @Local(name = "locationInfo") PackLocationInfo locationInfo) {
        // We don't care about server data packs.
        if (this.packType == PackType.SERVER_DATA) {
            return selectionConfig;
        }

        String fileName = locationInfo.id();
        var result = StartupConfig.isResourcePackForceLoaded(fileName);
        if (result == StartupConfig.Result.FORCE_TOP) {
            return FORCE_LOADED_CONFIG_TOP;
        } else if (result == StartupConfig.Result.FORCE) {
            return FORCE_LOADED_CONFIG;
        }

        // Otherwise fall back to the default
        return selectionConfig;
    }
}
