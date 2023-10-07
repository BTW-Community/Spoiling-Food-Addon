package btw.community.arminias.foodspoil.mixin.animal;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityAnimal;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLiving.class)
public abstract class EntityLivingMixin extends Entity {

    public EntityLivingMixin(World par1World) {
        super(par1World);
    }

    @Environment(EnvType.CLIENT)
    @Inject(method = "getTexture", at = @At("RETURN"), cancellable = true)
    private void getTexture(CallbackInfoReturnable<String> cir) {
        if (((Object) this) instanceof EntityAnimal && this.getDataWatcher().getWatchableObjectInt(FoodSpoilMod.ANIMAL_AGE_WATCHER_ID) > FoodSpoilMod.ANIMAL_OLD_AGE) {
            switch (cir.getReturnValue()) {
                case "/mob/cow.png":
                    cir.setReturnValue("/mob/cow_old.png");
                    break;
                case "/mob/pig.png":
                    cir.setReturnValue("/mob/pig_old.png");
                    break;
                case "/mob/sheep.png":
                    cir.setReturnValue("/mob/sheep_old.png");
                    break;
                case "/btwmodtex/fcSheep.png":
                    cir.setReturnValue("/mob/fcSheep_old.png");
                    break;
                case "/mob/chicken.png":
                    cir.setReturnValue("/mob/chicken_old.png");
                    break;
            }
        }
    }
}
