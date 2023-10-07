package btw.community.arminias.foodspoil.mixin.animal;

import btw.entity.mob.ChickenEntity;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntityEgg.class)
public abstract class EntityEggMixin extends EntityThrowable {
    public EntityEggMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "onImpact", at = @At(value = "INVOKE", target = "Lbtw/entity/mob/ChickenEntity;setLocationAndAngles(DDDFF)V"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT, require = 0)
    private void spawnThrownEntity(MovingObjectPosition par1MovingObjectPosition, CallbackInfo ci, byte var2, int var3, ChickenEntity var4) {
        var4.getDataWatcher().updateObject(FoodSpoilMod.ANIMAL_AGE_WATCHER_ID, -var4.getTicksForChildToGrow());
    }

    @Inject(method = "onImpact", at = @At(value = "INVOKE", target = "Lbtw/entity/mob/ChickenEntity;setLocationAndAngles(DDDFF)V"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT, require = 0)
    private void spawnThrownEntity2(MovingObjectPosition par1MovingObjectPosition, CallbackInfo ci, int var2, int var3, ChickenEntity var4) {
        var4.getDataWatcher().updateObject(FoodSpoilMod.ANIMAL_AGE_WATCHER_ID, -var4.getTicksForChildToGrow());
    }
}
