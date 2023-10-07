package btw.community.arminias.foodspoil.mixin;


import btw.community.arminias.foodspoil.FoodType;
import btw.community.arminias.foodspoil.Utils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {TileEntityChest.class, TileEntityHopper.class, TileEntityBrewingStand.class, TileEntityDispenser.class } )
public abstract class InventoryMixin extends TileEntity {

    @Shadow(aliases = {"chestContents", "hopperItemStacks", "brewingItemStacks", "dispenserContents", "field_5660", "field_525", "field_513", "field_528"}, remap = false)
    private ItemStack[] perClassInventory;


    @Inject(method = "getStackInSlot", at=@At("HEAD"))
    private void addFoodDecay(int iSlot, CallbackInfoReturnable<ItemStack> cir) {
        if (this.worldObj != null && !this.worldObj.isRemote && this.perClassInventory != null) {
            ItemStack item = this.perClassInventory[iSlot];
            if (Utils.isBeyondSpoilDate(item, this.worldObj.getTotalWorldTime())) {
                this.perClassInventory[iSlot] = FoodType.doItemDecayFast(item);
                this.onInventoryChanged();
            }
        }
    }
}
