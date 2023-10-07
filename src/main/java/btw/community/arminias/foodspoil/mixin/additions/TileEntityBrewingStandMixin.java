package btw.community.arminias.foodspoil.mixin.additions;

import btw.community.arminias.foodspoil.FoodSpoilAddon;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntityBrewingStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityBrewingStand.class)
public class TileEntityBrewingStandMixin {
    @Redirect(method = "canBrew", at = @At(value = "FIELD", target = "Lnet/minecraft/src/ItemStack;itemID:I", ordinal = 1))
    private int canBrew(ItemStack itemStack) {
        return itemStack.itemID == FoodSpoilAddon.sleepingPotion.itemID ? Item.potion.itemID : itemStack.itemID;
    }

    @Redirect(method = "brewPotions", at = @At(value = "FIELD", target = "Lnet/minecraft/src/ItemStack;itemID:I", ordinal = 0))
    private int brewPotions(ItemStack itemStack) {
        return itemStack.itemID == FoodSpoilAddon.sleepingPotion.itemID ? Item.potion.itemID : itemStack.itemID;
    }

    @Inject(method = "getPotionResult", at = @At("RETURN"), cancellable = true)
    private void getSleepingSplashPotion(int par1, ItemStack par2ItemStack, CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() == 0 && par2ItemStack.itemID == Item.gunpowder.itemID && par1 == FoodSpoilAddon.sleeping.getId()) {
            cir.setReturnValue(0x4000 | FoodSpoilAddon.sleeping.getId());
        }
    }
}
