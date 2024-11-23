package btw.community.arminias.foodspoil.mixin.additions;

import btw.community.arminias.foodspoil.FoodSpoilAddon;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLivingBase {
    @Shadow public abstract void wakeUpPlayer(boolean par1, boolean par2, boolean par3);

    @Shadow public ChunkCoordinates playerLocation;

    @Shadow protected boolean sleeping;

    @Shadow private int sleepTimer;

    @Shadow public abstract EnumStatus sleepInBedAt(int par1, int par2, int par3);

    @Shadow protected abstract void func_71013_b(int par1);

    public EntityPlayerMixin(World par1World) {
        super(par1World);
    }

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayer;wakeUpPlayer(ZZZ)V"))
    private void wakeUpPlayer(EntityPlayer entityPlayer, boolean par1, boolean par2, boolean par3) {
        if (!par1 && par2 && par3) {
            if (!this.isPotionActive(FoodSpoilAddon.sleeping)) {
                this.wakeUpPlayer(par1, par2, par3);
            }
        } else if (par1 && par2 && !par3) {
            if (!this.isPotionActive(FoodSpoilAddon.sleeping) || this.getActivePotionEffect(FoodSpoilAddon.sleeping).getAmplifier() >= 1) {
                this.wakeUpPlayer(par1, par2, par3);
            }
        }
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void onUpdate(CallbackInfo ci) {
        PotionEffect sleeping;
        if ((sleeping = this.getActivePotionEffect(FoodSpoilAddon.sleeping)) != null && sleeping.getAmplifier() >= 1) {
            if (this.playerLocation == null) {
                this.playerLocation = new ChunkCoordinates(MathHelper.floor_double(this.posX + 0.5D), MathHelper.floor_double(this.posY + 0.5D), MathHelper.floor_double(this.posZ + 0.5D));
            }
            if (!this.sleeping) {
                this.sleepInBedAt(this.playerLocation.posX, this.playerLocation.posY, this.playerLocation.posZ);
                int par1 = this.playerLocation.posX;
                int par2 = this.playerLocation.posY;
                int par3 = this.playerLocation.posZ;
                this.setSize(0.2F, 0.2F);
                this.yOffset = 0.2F;

                if (this.worldObj.blockExists(par1, par2, par3))
                {
                    int var9 = this.worldObj.getBlockMetadata(par1, par2, par3);
                    int var5 = BlockBed.getDirection(var9);
                    float var10 = 0.5F;
                    float var7 = 0.5F;

                    switch (var5)
                    {
                        case 0:
                            var7 = 0.9F;
                            break;

                        case 1:
                            var10 = 0.1F;
                            break;

                        case 2:
                            var7 = 0.1F;
                            break;

                        case 3:
                            var10 = 0.9F;
                    }

                    this.func_71013_b(var5);
                    this.setPosition((double)((float)par1 + var10), (double)((float)par2 + 0.9375F), (double)((float)par3 + var7));
                }
                else
                {
                    this.setPosition((double)((float)par1 + 0.5F), (double)((float)par2 + 0.9375F), (double)((float)par3 + 0.5F));
                }

                this.sleeping = true;
                this.sleepTimer = 0;
                this.playerLocation = new ChunkCoordinates(par1, par2, par3);
                this.motionX = this.motionZ = this.motionY = 0.0D;

                if (!this.worldObj.isRemote)
                {
                    this.worldObj.updateAllPlayersSleepingFlag();
                }
            }
        }
    }

    @Inject(method = {"isPlayerSleeping", "isPlayerFullyAsleep"}, at = @At("RETURN"), cancellable = true)
    private void isPlayerSleeping(CallbackInfoReturnable<Boolean> cir) {
        PotionEffect sleeping;
        if ((sleeping = this.getActivePotionEffect(FoodSpoilAddon.sleeping)) != null && sleeping.getAmplifier() >= 1) {
            cir.setReturnValue(true);
            //System.out.println(sleeping.getDuration());
        }
    }

    @Redirect(method = "sleepInBedAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;isDaytime()Z", by = 1))
    private boolean isDaytime(World world) {
        return world.isDaytime() && !this.isPotionActive(FoodSpoilAddon.sleeping);
    }

    @Inject(method = "isInBed", at = @At("HEAD"), cancellable = true)
    private void isInBed(CallbackInfoReturnable<Boolean> cir) {
        PotionEffect sleeping;
        if ((sleeping = this.getActivePotionEffect(FoodSpoilAddon.sleeping)) != null && sleeping.getAmplifier() >= 1) {
            cir.setReturnValue(true);
        }
    }
}
