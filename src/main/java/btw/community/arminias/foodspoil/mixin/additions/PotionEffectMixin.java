package btw.community.arminias.foodspoil.mixin.additions;

import btw.community.arminias.foodspoil.FoodSpoilAddon;
import btw.community.arminias.foodspoil.PotionEffectExtension;
import net.minecraft.src.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionEffect.class)
public abstract class PotionEffectMixin implements PotionEffectExtension {

    @Shadow public abstract int getAmplifier();

    @Shadow public abstract int getDuration();

    @Shadow private int amplifier;
    @Shadow private int duration;
    @Shadow private int potionID;
    @Shadow private boolean isAmbient;
    private int amplifierB;
    private int durationB;

    @Override
    public void originalCombine(PotionEffect potionEffect) {
        if (this.potionID != potionEffect.getPotionID())
        {
            System.err.println("This method should only be called for matching effects!");
        }

        if (potionEffect.getAmplifier() > this.amplifier)
        {
            this.amplifier = potionEffect.getAmplifier();
            this.duration = potionEffect.getDuration();
        }
        else if (potionEffect.getAmplifier() == this.amplifier && this.duration < potionEffect.getDuration())
        {
            this.duration = potionEffect.getDuration();
        }
        else if (!potionEffect.getIsAmbient() && this.isAmbient)
        {
            this.isAmbient = potionEffect.getIsAmbient();
        }
    }

    @Inject(at = @At("HEAD"), method = "combine(Lnet/minecraft/src/PotionEffect;)V", cancellable = false)
    public void combine(PotionEffect other, CallbackInfo ci) {
        if (this.potionID != other.getPotionID() || this.potionID != FoodSpoilAddon.sleeping.id) {
            return;
        }
        amplifierB = this.getAmplifier();
        durationB = this.getDuration();
    }

    @Inject(at = @At("RETURN"), method = "combine(Lnet/minecraft/src/PotionEffect;)V", cancellable = false)
    public void combine2(PotionEffect other, CallbackInfo ci) {
        if (this.potionID != other.getPotionID() || this.potionID != FoodSpoilAddon.sleeping.id) {
            return;
        }
        if (this.getAmplifier() == amplifierB) {
            int newDuration = Math.min(durationB + other.getDuration(), 10000); // 32766 max value here
            if (newDuration == 10000) {
                this.amplifier = amplifierB + 1;
                this.duration = 18000;
            }
            else {
                this.duration = newDuration;
            }
        }
    }
}
