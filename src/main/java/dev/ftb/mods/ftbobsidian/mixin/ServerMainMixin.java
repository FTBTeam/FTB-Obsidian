package dev.ftb.mods.ftbobsidian.mixin;

import dev.ftb.mods.ftbobsidian.defaults.DefaultsSync;
import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class ServerMainMixin {
    @Inject(method = "main", at = @At("HEAD"))
    private static void ftbobsidian$main(String[] args, CallbackInfo ci) {
        DefaultsSync.run();
    }
}
