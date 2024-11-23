package btw.community.arminias.foodspoil.mixin;

import btw.community.arminias.foodspoil.BooleanFloatPair;
import btw.community.arminias.foodspoil.FoodSpoilAddon;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import btw.community.arminias.foodspoil.Utils;
import btw.crafting.manager.BulkCraftingManager;
import btw.crafting.recipe.types.BulkRecipe;
import btw.inventory.util.InventoryUtils;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(BulkCraftingManager.class)
public abstract class BulkCraftingManagerMixin {

    @Inject(method = "consumeIngredientsAndReturnResult", at = @At(value = "INVOKE", target = "Lbtw/crafting/recipe/types/BulkRecipe;consumeInventoryIngredients(Lnet/minecraft/src/IInventory;)Z"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void inheritFoodDecay(IInventory inventory, CallbackInfoReturnable<List<ItemStack>> cir, int j, BulkRecipe tempRecipe) {
        if (inventory != null && inventory.getSizeInventory() > 0) {
            float p = getPercentageSpoiled(tempRecipe, inventory);
            if (p < 0) {
                return;
            }
            p = Math.min(p + FoodSpoilAddon.getCookingSpoilingBonus(), 1.0F);
            tempRecipe.consumeInventoryIngredients(inventory);
            List<ItemStack> output = tempRecipe.getCraftingOutputList();
            for (int i = 0; i < output.size(); i++) {
                ItemStack itemStack = output.get(i).copy();
                Utils.setSpoilTime(itemStack, p, Utils.getTotalWorldTime());
                output.set(i, itemStack);
            }
            cir.setReturnValue(output);
        }
    }

    @Unique
    public float getPercentageSpoiled(BulkRecipe recipe, IInventory inventory) {
        float ret = 0F;
        int count = 0;
        List<ItemStack> recipeInputStacks = ((BulkRecipeAccessor) recipe).getRecipeInputStacks();
        if (recipeInputStacks != null && !recipeInputStacks.isEmpty()) {
            for (ItemStack tempStack : recipeInputStacks) {
                if (tempStack != null) {
                    BooleanFloatPair pair = getSpoilPercentageFromItems(inventory, tempStack.getItem().itemID, tempStack.getItemDamage(), tempStack.stackSize, ((BulkRecipeAccessor) recipe).isMetadataExclusive());
                    if (!pair.bool) {
                        return -1F;
                    }
                    if (pair.value != -1.0F) {
                        ret += pair.value;
                        count++;
                    }
                }
            }
        }

        return ret / count;
    }

    @Unique
    public BooleanFloatPair getSpoilPercentageFromItems(IInventory inventory, int iShiftedItemIndex, int iItemDamage, // m_iIgnoreMetadata to disregard
                                                        int iItemCount, boolean bMetaDataExclusive) {
        float ret = 0F;
        int count = 0;
        for (int iSlot = 0; iSlot < inventory.getSizeInventory(); iSlot++) {
            ItemStack tempItemStack = inventory.getStackInSlot(iSlot);

            if (tempItemStack != null) {
                Item tempItem = tempItemStack.getItem();

                if (tempItem.itemID == iShiftedItemIndex) {
                    if (iItemDamage == InventoryUtils.IGNORE_METADATA || ((!bMetaDataExclusive) && tempItemStack.getItemDamage() == iItemDamage) || (bMetaDataExclusive && tempItemStack.getItemDamage() != iItemDamage)) {
                        if (tempItemStack.stackSize >= iItemCount) {
                            //InventoryUtils.decreaseStackSize(inventory, iSlot, iItemCount);
                            float p = Utils.getPercentageSpoilTimeLeft(tempItemStack, Utils.getTotalWorldTime()) * iItemCount;
                            if (p != -1.0F) {
                                ret += p;
                                count += iItemCount;
                            } else {
                                return new BooleanFloatPair(true, -1F);
                            }
                            return new BooleanFloatPair(true, ret / count);
                        } else {
                            //iItemCount -= tempItemStack.stackSize;
                            float p = Utils.getPercentageSpoilTimeLeft(tempItemStack, Utils.getTotalWorldTime()) * tempItemStack.stackSize;
                            if (p != -1.0F) {
                                ret += p;
                                count += tempItemStack.stackSize;
                            }
                            //inventory.setInventorySlotContents(iSlot, null);
                        }
                    }
                }
            }
        }

        return new BooleanFloatPair(false, ret);
    }
}
