package btw.community.arminias.foodspoil.mixin.animal;

import btw.community.arminias.foodspoil.FoodSpoilMod;
import btw.entity.mob.WolfEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.EntityWolf;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WolfEntity.class)
public abstract class WolfEntityMixin extends EntityWolf {

    public WolfEntityMixin(World world) {
        super(world);
    }

    @Environment(EnvType.CLIENT)
    @Inject(method = "getTexture", at = @At("RETURN"), cancellable = true)
    private void getTexture(CallbackInfoReturnable<String> cir) {
        if (this.getDataWatcher().getWatchableObjectInt(FoodSpoilMod.ANIMAL_AGE_WATCHER_ID) > FoodSpoilMod.ANIMAL_OLD_AGE) {
            if (cir.getReturnValue().equals("/mob/wolf_tame.png")) {
                cir.setReturnValue("/mob/wolf_tame_old.png");
            } else if (cir.getReturnValue().equals("/mob/wolf.png")) {
                cir.setReturnValue("/mob/wolf_old.png");
            }
        }
    }
}
