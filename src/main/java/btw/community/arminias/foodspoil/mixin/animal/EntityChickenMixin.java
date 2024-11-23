package btw.community.arminias.foodspoil.mixin.animal;

import btw.community.arminias.foodspoil.AgingEntityExtension;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import net.minecraft.src.EntityAnimal;
import net.minecraft.src.EntityChicken;
import net.minecraft.src.EntityOcelot;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityChicken.class)
public abstract class EntityChickenMixin extends EntityAnimal {
    public EntityChickenMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "updateHungerState", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityChicken;dropItem(II)Lnet/minecraft/src/EntityItem;"))
    private void giveBirthAtTargetLocation(CallbackInfo ci) {
        if (dataWatcher.getWatchableObjectInt(FoodSpoilMod.ANIMAL_SPAWNED_AT_WATCHER_ID) == -1) {
            ((AgingEntityExtension) this).foodspoilmod$setSpawnedAt(worldObj.getTotalWorldTime());
        }
    }
}
