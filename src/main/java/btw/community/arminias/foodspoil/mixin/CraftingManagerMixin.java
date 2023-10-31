package btw.community.arminias.foodspoil.mixin;

import btw.community.arminias.foodspoil.Utils;
import net.minecraft.src.CraftingManager;
import net.minecraft.src.InventoryCrafting;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CraftingManager.class)
public class CraftingManagerMixin {
    @Inject(method = "findMatchingRecipe", at = @At("RETURN"))
    private void inheritFoodDecay(InventoryCrafting inventory, World world, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack result = cir.getReturnValue();
        if (result == null) return;
        float p = 0F;
        int count = 0;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack item = inventory.getStackInSlot(i);
            if (item != null) {
                float tempP = Utils.getPercentageSpoilTimeLeft(item, world.getTotalWorldTime());
                if (tempP != -1.0F) {
                    p += tempP;
                    count++;
                }
            }
        }
        if (count > 0) {
            p /= count;
            Utils.setSpoilTime(result, p, world.getTotalWorldTime());
        }
    }
}
