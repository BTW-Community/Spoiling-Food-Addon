package btw.community.arminias.foodspoil.mixin;


import btw.block.tileentity.OvenTileEntity;
import btw.block.tileentity.WickerBasketTileEntity;
import btw.community.arminias.foodspoil.FoodType;
import btw.community.arminias.foodspoil.Utils;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {WickerBasketTileEntity.class, OvenTileEntity.class} )
public class SingleInventoryMixin extends TileEntity {

    @Shadow(aliases = {"storageStack", "cookStack"}, remap = false)
    private ItemStack perClassInventory;

    @Inject(method = "updateEntity", at=@At("RETURN"))
    private void addFoodDecay(CallbackInfo ci) {
        if (this.worldObj != null && this.perClassInventory != null) {
            if (Utils.isBeyondSpoilDate(this.perClassInventory, this.worldObj.getTotalWorldTime())) {
                perClassInventory = FoodType.doItemDecayFast(perClassInventory);
                this.onInventoryChanged();
            }
        }
    }
}
