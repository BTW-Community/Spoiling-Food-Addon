package btw.community.arminias.foodspoil.mixin.additions;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.ItemPotion;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Potion;
import net.minecraft.src.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static net.minecraft.src.PotionHelper.getPotionEffects;

@Mixin(ItemPotion.class)
public abstract class ItemPotionMixin {
    @Shadow
    public HashMap effectCache;

    @Shadow public abstract List getEffects(ItemStack par1ItemStack);

    @Redirect(method = "getEffects(I)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/PotionHelper;getPotionEffects(IZ)Ljava/util/List;"))
    public List getPotionEffectsRedirect(int par0, boolean par1) {
        return getEffectCustom(par0, par1);
    }

    @Redirect(method = "getEffects(Lnet/minecraft/src/ItemStack;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/PotionHelper;getPotionEffects(IZ)Ljava/util/List;"))
    public List getPotionEffectsRedirect2(int par0, boolean par1) {
        return getEffectCustom(par0, par1);
    }

    @Redirect(method = {"addInformation", "getItemDisplayName"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemPotion;getEffects(Lnet/minecraft/src/ItemStack;)Ljava/util/List;"))
    public List getEffectsRedirect(ItemPotion itemPotion, net.minecraft.src.ItemStack itemStack) {
        return this.getEffects(itemStack);
    }

    @Unique
    private List getEffectCustom(int par0, boolean par1) {
        List original = getPotionEffects(par0, par1);
        if (original == null || original.isEmpty()) {
            int k = par0 & (Potion.potionTypes.length - 1);
            if (Potion.potionTypes[k] != null) {
                if (effectCache.containsKey(par0)) {
                    return (List) effectCache.get(par0);
                }
                List c = new ArrayList(Collections.singletonList(new PotionEffect(Potion.potionTypes[k].getId(), (int) (1200 * (1 + isExtended(par0) * 5/3F)), isUpgraded(par0))));
                effectCache.put(par0, c);
                return c;
            } else {
                return original;
            }
        } else {
            return original;
        }
    }

    @Unique
    private static int getPotionId(int par1) {
        return par1 & 0b00011111;
    }

    @Unique
    private static boolean isSplash(int par1) {
        return (par1 & 0x4000) != 0;
    }

    @Unique
    private static int isExtended(int par1) {
        return (par1 & 0b01000000) >> 6;
    }

    @Unique
    private static int isUpgraded(int par1) {
        return (par1 & 0b00100000) >> 5;
    }
}
