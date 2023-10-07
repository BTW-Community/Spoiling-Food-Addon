package btw.community.arminias.foodspoil.mixin.animal;

import btw.entity.mob.CowEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import net.minecraft.src.EntityCow;
import net.minecraft.src.EntityMooshroom;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityMooshroom.class)
public abstract class EntityMooshroomMixin extends EntityCow {

    public EntityMooshroomMixin(World world) {
        super(world);
    }

    @Environment(EnvType.CLIENT)
    @Inject(method = "getTexture", at = @At("RETURN"), cancellable = true)
    private void getTexture(CallbackInfoReturnable<String> cir) {
        if (this.getDataWatcher().getWatchableObjectInt(FoodSpoilMod.ANIMAL_AGE_WATCHER_ID) > FoodSpoilMod.ANIMAL_OLD_AGE) {
            if (texture.equals("/mob/redcow.png")) {
                cir.setReturnValue("/mob/redcow_old.png");
            }
        }
    }
}
