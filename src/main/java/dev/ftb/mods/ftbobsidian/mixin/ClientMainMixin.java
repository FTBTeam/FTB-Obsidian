package dev.ftb.mods.ftbobsidian.mixin;

import dev.ftb.mods.ftbobsidian.defaults.DefaultsSync;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(Main.class)
public class ClientMainMixin {
    @Inject(method = "main", at = @At("HEAD"))
    private static void ftbobsidian$main(String[] args, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        DefaultsSync.run();
    }
}
