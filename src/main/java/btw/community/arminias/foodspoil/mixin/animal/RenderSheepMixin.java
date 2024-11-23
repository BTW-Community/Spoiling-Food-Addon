package btw.community.arminias.foodspoil.mixin.animal;

import btw.community.arminias.foodspoil.AgingEntityExtension;
import btw.community.arminias.foodspoil.FoodSpoilAddon;
import com.prupe.mcpatcher.mob.MobRandomizer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import net.minecraft.src.Entity;
import net.minecraft.src.EntitySheep;
import net.minecraft.src.RenderSheep;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(RenderSheep.class)
public abstract class RenderSheepMixin {
    private static final ResourceLocation SHEEP_FUR_OLD = new ResourceLocation("foodspoilmod:textures/entity/mob/sheep_fur_old.png");
    private static final ResourceLocation SHEEP_FUR_SHEARED_OLD = new ResourceLocation("foodspoilmod:textures/entity/mob/fcSheepFurSheared_old.png");

    @Environment(EnvType.CLIENT)
    @Redirect(method = "setWoolColorAndRender", at = @At(value = "INVOKE", target = "Lcom/prupe/mcpatcher/mob/MobRandomizer;randomTexture(Lnet/minecraft/src/Entity;Lnet/minecraft/src/ResourceLocation;)Lnet/minecraft/src/ResourceLocation;", ordinal = 1))
    private ResourceLocation modifyTexture(Entity entity, ResourceLocation texture) {
        EntitySheep sheep = (EntitySheep) entity;
        if (sheep != null && entity instanceof AgingEntityExtension entityAging && entityAging.foodspoilmod$isOld()) {
            return SHEEP_FUR_SHEARED_OLD;
        }
        return MobRandomizer.randomTexture(sheep, texture);
    }

    @Environment(EnvType.CLIENT)
    @Redirect(method = "setWoolColorAndRender", at = @At(value = "INVOKE", target = "Lcom/prupe/mcpatcher/mob/MobRandomizer;randomTexture(Lnet/minecraft/src/Entity;Lnet/minecraft/src/ResourceLocation;)Lnet/minecraft/src/ResourceLocation;", ordinal = 0))
    private ResourceLocation modifyTexture2(Entity entity, ResourceLocation texture) {
        EntitySheep sheep = (EntitySheep) entity;
        if (sheep != null && entity instanceof AgingEntityExtension entityAging && entityAging.foodspoilmod$isOld()) {
            return SHEEP_FUR_OLD;
        }
        return MobRandomizer.randomTexture((Entity)sheep, texture);
    }
}
