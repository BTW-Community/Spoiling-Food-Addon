package btw.community.arminias.foodspoil.mixin.additions;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import btw.community.arminias.foodspoil.FoodSpoilAddon;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(GuiSleepMP.class)
public class GuiSleepMPMixin extends GuiChat {

    @Inject(method = "initGui", at = @At("RETURN"))
    private void initGui(CallbackInfo ci) {
        PotionEffect sleeping;
        if ((sleeping = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(FoodSpoilAddon.sleeping)) != null
                && sleeping.getAmplifier() >= 1) {
            ((GuiButton) this.buttonList.get(this.buttonList.size() - 1)).enabled = false;
        }
    }

    @Inject(method = "wakeEntity", at = @At("HEAD"), cancellable = true)
    private void wakeEntity(CallbackInfo ci) {
        PotionEffect sleeping;
        if ((sleeping = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(FoodSpoilAddon.sleeping)) != null
                && sleeping.getAmplifier() >= 1) {
            ci.cancel();
        }
    }
}
