package btw.community.arminias.foodspoil.mixin.animal;

import btw.community.arminias.foodspoil.AgingEntityExtension;
import btw.community.arminias.foodspoil.FoodSpoilAddon;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Entity;
import net.minecraft.src.RenderChicken;
import net.minecraft.src.RenderCow;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderChicken.class)
public abstract class RenderChickenMixin {
    @Shadow @Final private static ResourceLocation chickenTextures;
    private static final ResourceLocation CHICKEN_OLD = new ResourceLocation("foodspoilmod:textures/entity/mob/chicken_old.png");

    @Environment(EnvType.CLIENT)
    @Inject(method = "getEntityTexture", at = @At("RETURN"), cancellable = true)
    private void getTexture(Entity par1Entity, CallbackInfoReturnable<ResourceLocation> cir) {
        if (par1Entity instanceof AgingEntityExtension entityAging && entityAging.foodspoilmod$isOld()) {
            ResourceLocation texture = cir.getReturnValue();
            if (texture.equals(chickenTextures)) {
                cir.setReturnValue(CHICKEN_OLD);
            }
        }
    }
}
