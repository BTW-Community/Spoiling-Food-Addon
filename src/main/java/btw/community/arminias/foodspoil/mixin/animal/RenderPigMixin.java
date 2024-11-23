package btw.community.arminias.foodspoil.mixin.animal;

import btw.community.arminias.foodspoil.AgingEntityExtension;
import btw.community.arminias.foodspoil.FoodSpoilAddon;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderPig.class)
public abstract class RenderPigMixin {
    @Shadow @Final private static ResourceLocation pigTextures;
    private static final ResourceLocation PIG_OLD = new ResourceLocation("foodspoilmod:textures/entity/mob/pig_old.png");

    @Environment(EnvType.CLIENT)
    @Inject(method = "getEntityTexture", at = @At("RETURN"), cancellable = true)
    private void getTexture(Entity par1Entity, CallbackInfoReturnable<ResourceLocation> cir) {
        if (par1Entity instanceof AgingEntityExtension entityAging && entityAging.foodspoilmod$isOld()) {
            ResourceLocation texture = cir.getReturnValue();
            if (texture.equals(pigTextures)) {
                cir.setReturnValue(PIG_OLD);
            }
        }
    }
}
