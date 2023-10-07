package btw.community.arminias.foodspoil.mixin.animal;


import btw.community.arminias.foodspoil.FoodSpoilMod;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntityAnimal.class)
public abstract class EntityAnimalMixin extends EntityAgeable {

    public EntityAnimalMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "entityInit", at = @At("RETURN"))
    private void entityInit(CallbackInfo ci) {
        dataWatcher.addObject(FoodSpoilMod.ANIMAL_AGE_WATCHER_ID, Integer.valueOf(-1));
    }

    @Inject(method = "onLivingUpdate", at = @At("RETURN"))
    private void onLivingUpdate(CallbackInfo ci) {
        if (worldObj.getTotalWorldTime() % 40 == 0 && worldObj.provider.dimensionId != 1) {
            int age = this.dataWatcher.getWatchableObjectInt(FoodSpoilMod.ANIMAL_AGE_WATCHER_ID);
            age = age == -1 ? age : (age + 40 == -1 ? 0 : age + 40);
            dataWatcher.updateObject(FoodSpoilMod.ANIMAL_AGE_WATCHER_ID, Integer.valueOf(age));
            if (age > FoodSpoilMod.ANIMAL_OLD_AGE) {
                getNavigator().setCanSwim(false);
            }
            if (age > FoodSpoilMod.ANIMAL_DEATH_AGE) {
                attackEntityFrom(DamageSource.generic, 1);
            }
        }
    }

    @Inject(method = "readEntityFromNBT", at = @At("RETURN"))
    private void readEntityFromNBT(NBTTagCompound tag, CallbackInfo ci) {
        if (tag.hasKey("spoilingFoodAddonAge"))
        {
            dataWatcher.updateObject(FoodSpoilMod.ANIMAL_AGE_WATCHER_ID, Integer.valueOf(tag.getInteger("spoilingFoodAddonAge")));
        }
    }

    @Inject(method = "writeEntityToNBT", at = @At("RETURN"))
    private void writeEntityToNBT(NBTTagCompound tag, CallbackInfo ci) {
        tag.setInteger("spoilingFoodAddonAge", dataWatcher.getWatchableObjectInt(FoodSpoilMod.ANIMAL_AGE_WATCHER_ID));
    }

    @Inject(method = "jump", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAnimal;isChild()Z"), cancellable = true)
    private void jump(CallbackInfo ci) {
        if (dataWatcher.getWatchableObjectInt(FoodSpoilMod.ANIMAL_AGE_WATCHER_ID) > FoodSpoilMod.ANIMAL_OLD_AGE) {
            // jump half height if old
            motionY = 0.21D;
            isAirBorne = true;
            ci.cancel();
        }
    }

    @Inject(method = "isReadyToEatBreedingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAnimal;getGrowingAge()I"), cancellable = true)
    private void isReadyToEatBreedingItem(CallbackInfoReturnable<Boolean> cir) {
        if (dataWatcher.getWatchableObjectInt(FoodSpoilMod.ANIMAL_AGE_WATCHER_ID) > FoodSpoilMod.ANIMAL_OLD_AGE) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getHungerSpeedModifier", at = @At(value = "RETURN"), cancellable = true)
    private void getSpeedModifier(CallbackInfoReturnable<Float> cir) {
        if (dataWatcher.getWatchableObjectInt(FoodSpoilMod.ANIMAL_AGE_WATCHER_ID) > FoodSpoilMod.ANIMAL_OLD_AGE) {
            cir.setReturnValue(0.66F);
        }
    }

    @Inject(method = "giveBirthAtTargetLocation", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAgeable;setGrowingAge(I)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void giveBirthAtTargetLocation(EntityAnimal targetMate, double dChildX, double dChildY, double dChildZ, CallbackInfo ci, int nestSize, int nestTempCount, EntityAgeable childEntity) {
        if (!((Object) this instanceof EntityOcelot)) {
            childEntity.getDataWatcher().updateObject(FoodSpoilMod.ANIMAL_AGE_WATCHER_ID, Integer.valueOf(-getTicksForChildToGrow()));
        }
    }

    @Inject(method = "giveBirthAtTargetLocation", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;spawnEntityInWorld(Lnet/minecraft/src/Entity;)Z"))
    private void giveBirthAtTargetLocation(EntityAnimal targetMate, double dChildX, double dChildY, double dChildZ, CallbackInfo ci) {
        if (!((Object) this instanceof EntityOcelot)) {
            if (dataWatcher.getWatchableObjectInt(FoodSpoilMod.ANIMAL_AGE_WATCHER_ID) < 0) {
                dataWatcher.updateObject(FoodSpoilMod.ANIMAL_AGE_WATCHER_ID, Integer.valueOf(0));
            }
            if (targetMate.getDataWatcher().getWatchableObjectInt(FoodSpoilMod.ANIMAL_AGE_WATCHER_ID) < 0) {
                targetMate.getDataWatcher().updateObject(FoodSpoilMod.ANIMAL_AGE_WATCHER_ID, Integer.valueOf(0));
            }
        }
    }

}
