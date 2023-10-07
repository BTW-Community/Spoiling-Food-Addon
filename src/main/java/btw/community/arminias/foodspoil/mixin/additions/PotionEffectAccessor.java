package btw.community.arminias.foodspoil.mixin.additions;

import net.minecraft.src.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PotionEffect.class)
public interface PotionEffectAccessor {
    @Accessor
    void setDuration(int duration);

    @Accessor
    void setAmplifier(int amplifier);
}
