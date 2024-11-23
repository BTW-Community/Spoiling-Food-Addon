package btw.community.arminias.foodspoil.mixin.animal;

import btw.community.arminias.foodspoil.AgingEntityExtension;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntityEgg.class)
public abstract class EntityEggMixin extends EntityThrowable {
    public EntityEggMixin(World par1World) {
        super(par1World);
    }

    @Group(name = "spawnThrownEntity", min = 1, max = 1)
    @Inject(method = "onImpact", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityChicken;setLocationAndAngles(DDDFF)V"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT, require = 0)
    private void spawnThrownEntity(MovingObjectPosition par1MovingObjectPosition, CallbackInfo ci, byte var2, int var3, EntityChicken var4) {
        ((AgingEntityExtension) var4).foodspoilmod$setSpawnedAt(worldObj.getTotalWorldTime() + var4.getTicksForChildToGrow());
    }

    @Group(name = "spawnThrownEntity", min = 1, max = 1)
    @Inject(method = "onImpact", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityChicken;setLocationAndAngles(DDDFF)V"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT, require = 0)
    private void spawnThrownEntity2(MovingObjectPosition par1MovingObjectPosition, CallbackInfo ci, int var2, int var3, EntityChicken var4) {
        ((AgingEntityExtension) var4).foodspoilmod$setSpawnedAt(worldObj.getTotalWorldTime() + var4.getTicksForChildToGrow());
    }
}
