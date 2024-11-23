package btw.community.arminias.foodspoil.mixin.animal;

import btw.community.arminias.foodspoil.AgingEntityExtension;
import btw.community.arminias.foodspoil.FoodSpoilAddon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderMooshroom.class)
public abstract class RenderMooshroomMixin extends RenderLiving {
    private static final ResourceLocation REDCOW_OLD = new ResourceLocation("foodspoilmod:textures/entity/mob/redcow_old.png");
    @Shadow @Final private static ResourceLocation mooshroomTextures;


    public RenderMooshroomMixin(ModelBase par1ModelBase, float par2) {
        super(par1ModelBase, par2);
    }

    @Environment(EnvType.CLIENT)
    @Inject(method = "getMooshroomTextures", at = @At("RETURN"), cancellable = true)
    private void getTexture(EntityMooshroom par1EntityMooshroom, CallbackInfoReturnable<ResourceLocation> cir) {
        if (par1EntityMooshroom instanceof AgingEntityExtension entityAging && entityAging.foodspoilmod$isOld()) {
            ResourceLocation texture = cir.getReturnValue();
            if (texture.equals(mooshroomTextures)) {
                cir.setReturnValue(REDCOW_OLD);
            }
        }
    }
}
