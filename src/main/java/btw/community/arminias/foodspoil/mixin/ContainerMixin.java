package btw.community.arminias.foodspoil.mixin;

import btw.community.arminias.foodspoil.Utils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(Container.class)
public abstract class ContainerMixin {
    @Shadow public List inventorySlots;

    @Redirect(method = "slotClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemStack;areItemStackTagsEqual(Lnet/minecraft/src/ItemStack;Lnet/minecraft/src/ItemStack;)Z", ordinal = 0))
    private boolean mergeDecayTimers(ItemStack stack1, ItemStack stack2,int par1, int par2, int par3, EntityPlayer par4EntityPlayer) {
        if ((!par4EntityPlayer.worldObj.isRemote || true) && stack1.stackTagCompound != null && stack2.stackTagCompound != null && stack1.stackTagCompound.hasKey("spoilDate") && stack2.stackTagCompound.hasKey("spoilDate")) {
            int var21 = par2 == 0 ? stack2.stackSize : 1;
            Slot var16 = (Slot)this.inventorySlots.get(par1);
            if (var21 > var16.getSlotStackLimit() - stack1.stackSize)
            {
                var21 = var16.getSlotStackLimit() - stack1.stackSize;
            }

            if (var21 > stack2.getMaxStackSize() - stack1.stackSize)
            {
                var21 = stack2.getMaxStackSize() - stack1.stackSize;
            }

            Utils.mergeDecayNBTsPartialRelaxed(stack1, stack2, var21);

            return true;
        }
        return ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    @Redirect(method = "attemptToMergeWithSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemStack;areItemStackTagsEqual(Lnet/minecraft/src/ItemStack;Lnet/minecraft/src/ItemStack;)Z", ordinal = 0))
    private boolean mergeDecayTimers(ItemStack stackSource, ItemStack tempDestStack, ItemStack stackSourceX, int iTempSlot) {
        if (stackSource.stackTagCompound != null && tempDestStack.stackTagCompound != null && stackSource.stackTagCompound.hasKey("spoilDate") && tempDestStack.stackTagCompound.hasKey("spoilDate")) {
            Slot tempDestSlot = (Slot) inventorySlots.get(iTempSlot);
            int iDestStackSize = tempDestStack.stackSize + stackSource.stackSize;
            int iMaxStackSize = stackSource.getMaxStackSize();

            if (tempDestSlot.getSlotStackLimit() < iMaxStackSize)
            {
                iMaxStackSize = tempDestSlot.getSlotStackLimit();
            }

            if (tempDestStack.stackSize < iMaxStackSize)
            {
                if (iDestStackSize <= iMaxStackSize)
                {
                    if (!Utils.mergeDecayNBTsStricter(stackSource, tempDestStack, Utils.getTotalWorldTime())) return false;
                }
                else
                {
                    if (!Utils.mergeDecayNBTsPartialStricter(tempDestStack, stackSource, (iMaxStackSize - tempDestStack.stackSize), Utils.getTotalWorldTime())) return false;
                }
            }

            return true;
        }
        return ItemStack.areItemStackTagsEqual(stackSource, tempDestStack);
    }

    
}
