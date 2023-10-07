package btw.community.arminias.foodspoil.mixin.animal;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import net.minecraft.src.EntitySheep;
import net.minecraft.src.RenderSheep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(RenderSheep.class)
public abstract class RenderSheepMixin {

    @Environment(EnvType.CLIENT)
    @ModifyVariable(method = "setWoolColorAndRender", at = @At(value = "INVOKE", target = "Lcom/prupe/mcpatcher/mal/resource/FakeResourceLocation;wrap(Ljava/lang/String;)Lcom/prupe/mcpatcher/mal/resource/FakeResourceLocation;", ordinal = 1), ordinal = 0)
    private String modifyTexture(String texture, EntitySheep sheep, int par2, float par3) {
        if (sheep != null && sheep.getDataWatcher().getWatchableObjectInt(FoodSpoilMod.ANIMAL_AGE_WATCHER_ID) > FoodSpoilMod.ANIMAL_OLD_AGE) {
            return "/mob/fcSheepFurSheared_old.png";
        }
        return texture;
    }

    @Environment(EnvType.CLIENT)
    @ModifyConstant(method = "setWoolColorAndRender", constant = @Constant(stringValue = "/mob/sheep_fur.png"))
    private String modifyTexture2(String texture, EntitySheep sheep, int par2, float par3) {
        if (sheep != null && sheep.getDataWatcher().getWatchableObjectInt(FoodSpoilMod.ANIMAL_AGE_WATCHER_ID) > FoodSpoilMod.ANIMAL_OLD_AGE) {
            return "/mob/sheep_fur_old.png";
        }
        return texture;
    }
}
