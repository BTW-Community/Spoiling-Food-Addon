package btw.community.arminias.foodspoil.mixin.additions;

import net.minecraft.src.EntityPotion;
import net.minecraft.src.ItemPotion;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(EntityPotion.class)
public class EntityPotionMixin {
    @Shadow private ItemStack potionDamage;

    @Redirect(method = {"onImpact"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemPotion;getEffects(Lnet/minecraft/src/ItemStack;)Ljava/util/List;"))
    public List getEffectsRedirect(ItemPotion itemPotion, net.minecraft.src.ItemStack itemStack) {
        if (potionDamage == null || !(potionDamage.getItem() instanceof ItemPotion itemPotionReal)) {
            return itemPotion.getEffects(itemStack);
        }
        return itemPotionReal.getEffects(itemStack);
    }
}
