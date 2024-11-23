package btw.community.arminias.foodspoil.mixin;

import btw.community.arminias.foodspoil.FoodSpoilAddon;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;startServer()Z", shift = At.Shift.AFTER))
    private void onServerInit(CallbackInfo ci) {
        FoodSpoilAddon.getInstance().parseLocalConfig();
    }
}
