package btw.community.arminias.foodspoil.mixin.additions;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.HashMap;

@Mixin(EntityLiving.class)
public interface EntityLivingAccessor {
    @Accessor
    HashMap getActivePotionsMap();

    @Invoker
    void callOnChangedPotionEffect(PotionEffect par1PotionEffect);

    @Invoker
    void callOnNewPotionEffect(PotionEffect par1PotionEffect);
}
