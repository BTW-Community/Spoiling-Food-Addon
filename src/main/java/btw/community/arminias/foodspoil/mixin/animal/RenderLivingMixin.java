package btw.community.arminias.foodspoil.mixin.animal;

import btw.community.arminias.foodspoil.FoodSpoilMod;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderLiving.class)
public abstract class RenderLivingMixin {

    @Shadow protected abstract float interpolateRotation(float par1, float par2, float par3);

    @Shadow protected abstract float getDeathMaxRotation(EntityLiving par1EntityLiving);

    @Inject(method = "getColorMultiplier", at = @At("HEAD"), cancellable = true)
    private void getColorMultiplier(EntityLiving par1EntityLiving, float par2, float par3, CallbackInfoReturnable<Integer> cir) {
        if (par1EntityLiving instanceof EntityAnimal) {
            int age = par1EntityLiving.getDataWatcher().getWatchableObjectInt(FoodSpoilMod.ANIMAL_AGE_WATCHER_ID);
            if (age > FoodSpoilMod.ANIMAL_OLD_AGE) {
                if (((EntityAnimal) par1EntityLiving).isStarving()) {
                    cir.setReturnValue(0x77777777);
                }
                else if (((EntityAnimal) par1EntityLiving).isFamished()) {
                    cir.setReturnValue(0x44444444);
                }
            }
        }
    }
    /*@Inject(method = "doRenderLiving", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/RenderLiving;rotateCorpse(Lnet/minecraft/src/EntityLiving;FFF)V"))
    private void doSleepingRotation(EntityLiving par1EntityLiving, double par22, double par44, double par66, float par88, float par99, CallbackInfo ci) {
        if (par1EntityLiving.deathTime == 0 && par1EntityLiving.isPotionActive(FoodSpoilAddon.sleeping) && !(par1EntityLiving instanceof EntityPlayer)) {
            System.out.println("Sleeping");
            float par3 = this.interpolateRotation(par1EntityLiving.prevRenderYawOffset, par1EntityLiving.renderYawOffset, par99);
            GL11.glTranslatef(0.0F, 0.5F, 0.0F);
            GL11.glRotatef(180.0F - par3, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(this.getDeathMaxRotation(par1EntityLiving), 0.0F, 0.0F, 1.0F);
        }
    }*/
}
