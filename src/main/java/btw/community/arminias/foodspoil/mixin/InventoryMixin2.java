package btw.community.arminias.foodspoil.mixin;


import btw.block.tileentity.CookingVesselTileEntity;
import btw.block.tileentity.HamperTileEntity;
import btw.block.tileentity.HopperTileEntity;
import btw.community.arminias.foodspoil.FoodType;
import btw.community.arminias.foodspoil.Utils;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {HopperTileEntity.class, CookingVesselTileEntity.class, HamperTileEntity.class} )
public abstract class InventoryMixin2 extends TileEntity implements IInventory {

    @Shadow(remap = false)
    private ItemStack[] contents;

    @Inject(method = {"getStackInSlot", "method_2381"}, at = @At("HEAD"), remap = false)
    private void getStackInSlot(int iSlot, CallbackInfoReturnable<ItemStack> cir) {
        if (this.worldObj != null && !this.worldObj.isRemote && this.contents != null) {
            ItemStack item = this.contents[iSlot];
            if (Utils.isBeyondSpoilDate(item, this.worldObj.getTotalWorldTime())) {
                this.contents[iSlot] = FoodType.doItemDecayFast(item);
                this.onInventoryChanged();
            }
        }
    }
}
