package btw.community.arminias.foodspoil.mixin.animal;


import btw.community.arminias.foodspoil.AgingEntityExtension;
import btw.community.arminias.foodspoil.FoodSpoilAddon;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.UUID;

@Mixin(EntityAnimal.class)
public abstract class EntityAnimalMixin extends EntityAgeable implements AgingEntityExtension {
    @Unique
    private static final long UNSET_SPAWNED_AT_VALUE = Long.MAX_VALUE >> 1;
    @Shadow @Final
    protected static AttributeModifier starvingSpeedModifier;
    @Unique
    private static final UUID OLD_AGE_SPEED_MODIFIER_UUID = UUID.fromString("f5b2e470-7a6d-492c-9592-0267c7bdae8f");
    @Unique
    private static final AttributeModifier oldAgeSpeedModifier = new AttributeModifier(OLD_AGE_SPEED_MODIFIER_UUID, "Old age speed penalty", -0.33, 2).setSaved(false);

    public EntityAnimalMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "entityInit", at = @At("RETURN"))
    private void entityInit(CallbackInfo ci) {
        dataWatcher.addObject(FoodSpoilMod.ANIMAL_SPAWNED_AT_WATCHER_ID, Integer.valueOf(-1));
    }

    @Inject(method = "onLivingUpdate", at = @At("RETURN"))
    private void onLivingUpdate(CallbackInfo ci) {
        if (worldObj.getTotalWorldTime() % 40 == 0 && worldObj.provider.dimensionId != 1) {
            long spawnedAt = foodspoilmod$getSpawnedAt();
            if (spawnedAt == UNSET_SPAWNED_AT_VALUE) {
                return;
            }
            if (spawnedAt + FoodSpoilAddon.getAnimalOldAge() < worldObj.getTotalWorldTime()) {
                getNavigator().setCanSwim(false);
            }
            if (spawnedAt + FoodSpoilAddon.getAnimalDeathAge() < worldObj.getTotalWorldTime()) {
                attackEntityFrom(DamageSource.generic, 1);
            }
        }
    }

    public long foodspoilmod$getSpawnedAt() {
        int value = this.dataWatcher.getWatchableObjectInt(FoodSpoilMod.ANIMAL_SPAWNED_AT_WATCHER_ID);
        if (value == -1) {
            return UNSET_SPAWNED_AT_VALUE;
        }
        return ((long) value) << FoodSpoilMod.SPAWNED_AT_QUANTIZATION_SHIFT;
    }

    public void foodspoilmod$setSpawnedAt(long spawnedAt) {
        this.dataWatcher.updateObject(FoodSpoilMod.ANIMAL_SPAWNED_AT_WATCHER_ID, Integer.valueOf((int) (spawnedAt >> FoodSpoilMod.SPAWNED_AT_QUANTIZATION_SHIFT)));
    }

    public boolean foodspoilmod$isOld() {
        return foodspoilmod$getSpawnedAt() + FoodSpoilAddon.getAnimalOldAge() < worldObj.getTotalWorldTime();
    }

    public boolean foodspoilmod$isDying() {
        return foodspoilmod$getSpawnedAt() + FoodSpoilAddon.getAnimalDeathAge() < worldObj.getTotalWorldTime();
    }

    @Inject(method = "readEntityFromNBT", at = @At("RETURN"))
    private void readEntityFromNBT(NBTTagCompound tag, CallbackInfo ci) {
        if (tag.hasKey("spoilingFoodAddonSpawnedAt"))
        {
            dataWatcher.updateObject(FoodSpoilMod.ANIMAL_SPAWNED_AT_WATCHER_ID, Integer.valueOf(tag.getInteger("spoilingFoodAddonSpawnedAt")));
        }
    }

    @Inject(method = "writeEntityToNBT", at = @At("RETURN"))
    private void writeEntityToNBT(NBTTagCompound tag, CallbackInfo ci) {
        tag.setInteger("spoilingFoodAddonSpawnedAt", dataWatcher.getWatchableObjectInt(FoodSpoilMod.ANIMAL_SPAWNED_AT_WATCHER_ID));
    }

    @Inject(method = "jump", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAnimal;isChild()Z"), cancellable = true)
    private void jump(CallbackInfo ci) {
        if (foodspoilmod$isOld()) {
            // jump half height if old
            motionY = 0.21D;
            isAirBorne = true;
            ci.cancel();
        }
    }

    @Inject(method = "isReadyToEatBreedingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAnimal;getGrowingAge()I"), cancellable = true)
    private void isReadyToEatBreedingItem(CallbackInfoReturnable<Boolean> cir) {
        if (foodspoilmod$isOld()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getHungerSpeedModifier", at = @At(value = "RETURN"), cancellable = true)
    private void getSpeedModifier(CallbackInfoReturnable<AttributeModifier> cir) {
        if (cir.getReturnValue() != starvingSpeedModifier && foodspoilmod$isOld()) {
            cir.setReturnValue(oldAgeSpeedModifier);
        }
    }

    @Inject(method = "giveBirthAtTargetLocation", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAgeable;setGrowingAge(I)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void giveBirthAtTargetLocation(EntityAnimal targetMate, double dChildX, double dChildY, double dChildZ, CallbackInfo ci, int nestSize, int nestTempCount, EntityAgeable childEntity) {
        if (!((Object) this instanceof EntityOcelot)) {
            if (childEntity instanceof AgingEntityExtension) {
                ((AgingEntityExtension) childEntity).foodspoilmod$setSpawnedAt(worldObj.getTotalWorldTime() + getTicksForChildToGrow());
            }
        }
    }

    @Inject(method = "giveBirthAtTargetLocation", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;spawnEntityInWorld(Lnet/minecraft/src/Entity;)Z"))
    private void giveBirthAtTargetLocation(EntityAnimal targetMate, double dChildX, double dChildY, double dChildZ, CallbackInfo ci) {
        if (!((Object) this instanceof EntityOcelot)) {
            if (dataWatcher.getWatchableObjectInt(FoodSpoilMod.ANIMAL_SPAWNED_AT_WATCHER_ID) == -1) {
                foodspoilmod$setSpawnedAt(worldObj.getTotalWorldTime());
            }
            if (targetMate.getDataWatcher().getWatchableObjectInt(FoodSpoilMod.ANIMAL_SPAWNED_AT_WATCHER_ID) == -1) {
                ((AgingEntityExtension) targetMate).foodspoilmod$setSpawnedAt(worldObj.getTotalWorldTime());
            }
        }
    }

    /*@Inject(method = "entityAnimalInteract", at=@At("HEAD"), cancellable=true)
    private void interact(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        player.sendChatToPlayer(ChatMessageComponent.createFromText("Spawned at: " + foodspoilmod$getSpawnedAt()));
        player.sendChatToPlayer(ChatMessageComponent.createFromText("Old at: " + (foodspoilmod$getSpawnedAt() + FoodSpoilAddon.getAnimalOldAge())));
    }*/

}
