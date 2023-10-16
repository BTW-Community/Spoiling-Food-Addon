package btw.community.arminias.foodspoil.mixin;

import btw.community.arminias.foodspoil.Utils;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InventoryPlayer.class)
public abstract class InventoryPlayerMixin {
    @Shadow public abstract int getInventoryStackLimit();

    @Shadow public EntityPlayer player;

    @Redirect(method = "storeItemStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemStack;areItemStackTagsEqual(Lnet/minecraft/src/ItemStack;Lnet/minecraft/src/ItemStack;)Z"))
    private boolean areItemStackTagsEqual(ItemStack stack1, ItemStack stack2) {
        int var3 = stack2.stackSize;
        int var5 = var3;

        if (var3 > stack1.getMaxStackSize() - stack1.stackSize)
        {
            var5 = stack1.getMaxStackSize() - stack1.stackSize;
        }

        if (var5 > this.getInventoryStackLimit() - stack1.stackSize)
        {
            var5 = this.getInventoryStackLimit() - stack1.stackSize;
        }

        if (stack1.stackTagCompound != null && stack2.stackTagCompound != null && stack1.stackTagCompound.hasKey("spoilDate") && stack2.stackTagCompound.hasKey("spoilDate")) {
            if (Utils.mergeDecayNBTsPartialStricter(stack1, stack2, var5, this.player.worldObj.getTotalWorldTime())) {
                return true;
            }
        }
        return ItemStack.areItemStackTagsEqual(stack1, stack2);
    }
}
