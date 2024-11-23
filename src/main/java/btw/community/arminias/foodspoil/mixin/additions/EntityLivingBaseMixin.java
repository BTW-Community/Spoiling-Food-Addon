package btw.community.arminias.foodspoil.mixin.additions;

import btw.community.arminias.foodspoil.FoodSpoilAddon;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.HashMap;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin extends Entity {
    @Final
    @Shadow
    private HashMap activePotionsMap;

    public EntityLivingBaseMixin(World par1World) {
        super(par1World);
    }

    @Shadow protected abstract boolean isAIEnabled();

    @Shadow protected double newRotationPitch;

    @Shadow public abstract boolean isPotionActive(Potion par1Potion);

    @Shadow public abstract void removePotionEffect(int par1);

    @Shadow public abstract PotionEffect getActivePotionEffect(Potion par1Potion);

    @Inject(method = "isMovementBlocked", at = @At(value = "RETURN"), cancellable = true)
    private void isMovementBlockedInject(CallbackInfoReturnable<Boolean> cir) {
        if (this.isPotionActive(FoodSpoilAddon.sleeping)) {
            cir.setReturnValue(true);
        }
    }

    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLivingBase;isAIEnabled()Z"))
    private boolean isAIEnabledRedirect(EntityLivingBase entityLivingBase) {
        return this.isAIEnabled() && !this.isPotionActive(FoodSpoilAddon.sleeping);
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "HEAD"))
    private void entityLivingOnLivingUpdateInject(CallbackInfo ci) {
        /*if (!((Object) this instanceof EntityPlayer) && this.activePotionsMap.containsKey(FoodSpoilAddon.sleeping.id)) {
            this.newRotationPitch = 0.35F;
            this.newPosX = this.posX;
            this.newPosY = this.posY;
            this.newPosZ = this.posZ;
            this.newPosRotationIncrements += 3;
            /*if (!this.worldObj.isRemote) {
                for (Object object : ((WorldServer) this.worldObj).playerEntities) {
                    System.out.println("Sleeping");
                    EntityPlayerMP player = (EntityPlayerMP) object;
                    player.playerNetServerHandler.sendPacketToPlayer(new Packet32EntityLook(this.entityId, (byte) ((this.rotationYaw * 360F) / 256F), (byte) 65));
                }
            }*/
        //this.rotationYaw = 89.77F;
        /*} else if (this.newRotationPitch == 0.35F) {
            this.newRotationPitch = 0F;
            this.newPosRotationIncrements += 3;
            //this.rotationYaw = 0F;
        }*/
        if (worldObj.isRemote) {
            if (!((Object) this instanceof EntityPlayer) && dataWatcher.getWatchableObjectInt(7) == PotionHelper.calcPotionLiquidColor(Collections.singletonList(new PotionEffect(FoodSpoilAddon.sleeping.getId(), 1, 0)))) {
                this.newRotationPitch = 80F;
                //this.rotationPitch = 90F;
            }
        }
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void attackEntityFrom(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir) {
        if (this.isPotionActive(FoodSpoilAddon.sleeping) && this.getActivePotionEffect(FoodSpoilAddon.sleeping).getAmplifier() < 1) {
            this.removePotionEffect(FoodSpoilAddon.sleeping.id);
        }
    }
}
