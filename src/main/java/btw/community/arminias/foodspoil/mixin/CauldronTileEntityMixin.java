package btw.community.arminias.foodspoil.mixin;

import api.inventory.InventoryUtils;
import api.item.util.ItemUtils;
import btw.block.tileentity.CauldronTileEntity;
import btw.block.tileentity.CookingVesselTileEntity;
import btw.community.arminias.foodspoil.Utils;
import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CauldronTileEntity.class)
public abstract class CauldronTileEntityMixin extends CookingVesselTileEntity {
    /*@Inject(method = "attemptToCookFood", at = @At(value = "INVOKE", target = "Lbtw/block/tileentity/CauldronTileEntity;decrStackSize(II)Lnet/minecraft/src/ItemStack;"), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private void inheritFoodDecay(CallbackInfoReturnable<Boolean> cir, int i, ItemStack tempStack, ItemStack cookedItem) {
        if (cookedItem != null) {
            Utils.inheritFoodDecay(contents[i], cookedItem, this.worldObj.getTotalWorldTime());
        }
    }*/

    @Shadow(remap = false)
    public abstract int getUncookedItemInventoryIndex();

    /**
     * @author Arminias
     */
    @Overwrite(remap = false)
    private boolean attemptToCookFood() {
        int iUncookedFoodIndex = getUncookedItemInventoryIndex();

        if ( iUncookedFoodIndex >= 0 )
        {
            ItemStack tempStack = FurnaceRecipes.smelting().getSmeltingResult(
                    contents[iUncookedFoodIndex].getItem().itemID);

            // we have to copy the furnace recipe stack so we don't end up with a pointer to the
            // actual smelting result ItemStack in our inventory
            ItemStack cookedStack = tempStack.copy();
            Utils.inheritFoodDecay(contents[iUncookedFoodIndex], cookedStack, this.worldObj.getTotalWorldTime());
            decrStackSize( iUncookedFoodIndex, 1 );

            if ( !InventoryUtils.addItemStackToInventory(this, cookedStack) )
            {
                ItemUtils.ejectStackWithRandomOffset(worldObj, xCoord, yCoord + 1, zCoord, cookedStack);
            }

            return true;
        }

        return false;
    }
}
