package btw.community.arminias.foodspoil.mixin;

import com.typesafe.config.Config;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(api.config.AddonConfig.class)
public interface AddonConfigAccessor {
    @Accessor
    Config getCurrentConfig();
}
