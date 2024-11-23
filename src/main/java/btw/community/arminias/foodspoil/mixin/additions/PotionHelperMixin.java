package btw.community.arminias.foodspoil.mixin.additions;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Potion;
import net.minecraft.src.PotionEffect;
import net.minecraft.src.PotionHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static net.minecraft.src.PotionHelper.getPotionEffects;

@Mixin(PotionHelper.class)
public abstract class PotionHelperMixin {
    @Shadow @Final public static String redstoneEffect;
    @Unique
    private static final HashMap<Integer, List> redirectCache = new HashMap<>();

    @Environment(EnvType.CLIENT)
    @Redirect(method = "func_77915_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/PotionHelper;getPotionEffects(IZ)Ljava/util/List;"))
    private static List getPotionEffectsRedirect(int par0, boolean par1) {
        List original = getPotionEffects(par0, par1);
        if (original == null || original.isEmpty()) {
            int k = par0 & (Potion.potionTypes.length - 1);
            if (Potion.potionTypes[k] != null) {
                if (redirectCache.containsKey(k)) {
                    return redirectCache.get(k);
                }
                List c = Collections.singletonList(new PotionEffect(Potion.potionTypes[k].getId(), 1, 0));
                redirectCache.put(k, c);
                return c;
            } else {
                return original;
            }
        } else {
            return original;
        }
    }
}
