package btw.community.arminias.foodspoil.mixin.animal;

import btw.community.arminias.foodspoil.AgingEntityExtension;
import btw.community.arminias.foodspoil.FoodSpoilAddon;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Entity;
import net.minecraft.src.RenderCow;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderCow.class)
public abstract class RenderCowMixin {
    @Shadow @Final private static ResourceLocation cowTextures;
    private static final ResourceLocation COW_OLD = new ResourceLocation("foodspoilmod:textures/entity/mob/cow_old.png");

    @Environment(EnvType.CLIENT)
    @Inject(method = "getEntityTexture", at = @At("RETURN"), cancellable = true)
    private void getTexture(Entity par1Entity, CallbackInfoReturnable<ResourceLocation> cir) {
        if (par1Entity instanceof AgingEntityExtension entityAging && entityAging.foodspoilmod$isOld()) {
            ResourceLocation texture = cir.getReturnValue();
            if (texture.equals(cowTextures)) {
                cir.setReturnValue(COW_OLD);
            }
        }
    }
}
