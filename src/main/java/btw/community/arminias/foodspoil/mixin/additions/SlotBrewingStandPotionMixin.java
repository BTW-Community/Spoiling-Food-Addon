package btw.community.arminias.foodspoil.mixin.additions;

import btw.community.arminias.foodspoil.FoodSpoilAddon;
import net.minecraft.src.ItemStack;
import net.minecraft.src.SlotBrewingStandPotion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SlotBrewingStandPotion.class)
public class SlotBrewingStandPotionMixin {
    @Inject(method = "canHoldPotion", at = @At("RETURN"), cancellable = true)
    private static void canHoldSleepingPotion(ItemStack par0ItemStack, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && par0ItemStack != null && par0ItemStack.itemID == FoodSpoilAddon.sleepingPotion.itemID) {
            cir.setReturnValue(true);
        }
    }
}
