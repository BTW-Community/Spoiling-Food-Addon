package btw.community.arminias.foodspoil.mixin.animal;

import btw.community.arminias.foodspoil.FoodSpoilMod;
import net.minecraft.src.EntityAnimal;
import net.minecraft.src.PathFinder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PathFinder.class)
public class PathFinderMixin {

    @Redirect(method = "getVerticalOffset", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAnimal;getGrowingAge()I"))
    private int getVerticalOffset(EntityAnimal entityAnimal) {
        if (entityAnimal.getDataWatcher().getWatchableObjectInt(FoodSpoilMod.ANIMAL_AGE_WATCHER_ID) > FoodSpoilMod.ANIMAL_OLD_AGE) {
            return -1;
        } else {
            return entityAnimal.getGrowingAge();
        }
    }
}
