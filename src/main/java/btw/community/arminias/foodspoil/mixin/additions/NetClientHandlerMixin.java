package btw.community.arminias.foodspoil.mixin.additions;

import btw.community.arminias.foodspoil.PotionEffectExtension;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NetClientHandler.class)
public class NetClientHandlerMixin {
    @Redirect(method = "handleEntityEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLiving;addPotionEffect(Lnet/minecraft/src/PotionEffect;)V"))
    private void addPotionEffect(EntityLiving entityLiving, PotionEffect potionEffect) {
        EntityLivingAccessor entityLivingAccessor = (EntityLivingAccessor) entityLiving;
        if (entityLiving.isPotionApplicable(potionEffect))
        {
            if (entityLivingAccessor.getActivePotionsMap().containsKey(potionEffect.getPotionID()))
            {
                ((PotionEffectExtension)entityLivingAccessor.getActivePotionsMap().get(potionEffect.getPotionID())).originalCombine(potionEffect);
                entityLivingAccessor.callOnChangedPotionEffect((PotionEffect)entityLivingAccessor.getActivePotionsMap().get(Integer.valueOf(potionEffect.getPotionID())));
            }
            else
            {
                entityLivingAccessor.getActivePotionsMap().put(potionEffect.getPotionID(), potionEffect);
                entityLivingAccessor.callOnNewPotionEffect(potionEffect);
            }
        }
    }
}
