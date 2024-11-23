package btw.community.arminias.foodspoil.mixin.additions;

import btw.community.arminias.foodspoil.PotionEffectExtension;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NetClientHandler.class)
public class NetClientHandlerMixin {
    @Redirect(method = "handleEntityEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLivingBase;addPotionEffect(Lnet/minecraft/src/PotionEffect;)V"))
    private void addPotionEffect(EntityLivingBase entityLivingBase, PotionEffect potionEffect) {
        EntityLivingBaseAccessor entityLivingBaseAccessor = (EntityLivingBaseAccessor) entityLivingBase;
        if (entityLivingBase.isPotionApplicable(potionEffect))
        {
            if (entityLivingBaseAccessor.getActivePotionsMap().containsKey(potionEffect.getPotionID()))
            {
                ((PotionEffectExtension)entityLivingBaseAccessor.getActivePotionsMap().get(potionEffect.getPotionID())).originalCombine(potionEffect);
                entityLivingBaseAccessor.callOnChangedPotionEffect((PotionEffect)entityLivingBaseAccessor.getActivePotionsMap().get(potionEffect.getPotionID()), true);
            }
            else
            {
                entityLivingBaseAccessor.getActivePotionsMap().put(potionEffect.getPotionID(), potionEffect);
                entityLivingBaseAccessor.callOnNewPotionEffect(potionEffect);
            }
        }
    }
}
