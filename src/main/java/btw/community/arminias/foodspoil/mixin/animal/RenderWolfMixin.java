package btw.community.arminias.foodspoil.mixin.animal;

import btw.community.arminias.foodspoil.AgingEntityExtension;
import btw.community.arminias.foodspoil.FoodSpoilAddon;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.EntityWolf;
import net.minecraft.src.RenderWolf;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderWolf.class)
public abstract class RenderWolfMixin {
    @Shadow @Final protected static ResourceLocation tamedWolfTextures;
    @Shadow @Final protected static ResourceLocation wolfTextures;
    private static final ResourceLocation WOLF_TAME_OLD = new ResourceLocation("foodspoilmod:textures/entity/mob/wolf_tame_old.png");
    private static final ResourceLocation WOLF_OLD = new ResourceLocation("foodspoilmod:textures/entity/mob/wolf_old.png");

    @Environment(EnvType.CLIENT)
    @Inject(method = "func_110914_a", at = @At("RETURN"), cancellable = true)
    private void getTexture(EntityWolf wolf, CallbackInfoReturnable<ResourceLocation> cir) {
        if (wolf instanceof AgingEntityExtension entityAging && entityAging.foodspoilmod$isOld()) {
            if (cir.getReturnValue().equals(tamedWolfTextures)) {
                cir.setReturnValue(WOLF_TAME_OLD);
            } else if (cir.getReturnValue().equals(wolfTextures)) {
                cir.setReturnValue(WOLF_OLD);
            }
        }
    }
}
