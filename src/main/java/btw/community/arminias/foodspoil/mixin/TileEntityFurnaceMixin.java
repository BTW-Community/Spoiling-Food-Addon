package btw.community.arminias.foodspoil.mixin;


import btw.community.arminias.foodspoil.FoodType;
import btw.community.arminias.foodspoil.Utils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {TileEntityFurnace.class} )
public abstract class TileEntityFurnaceMixin extends TileEntity {

    @Shadow
    protected ItemStack[] furnaceItemStacks;


    @Inject(method = "getStackInSlot", at=@At("HEAD"))
    private void addFoodDecay(int iSlot, CallbackInfoReturnable<ItemStack> cir) {
        if (this.worldObj != null && !this.worldObj.isRemote && this.furnaceItemStacks != null) {
            ItemStack item = this.furnaceItemStacks[iSlot];
            if (Utils.isBeyondSpoilDate(item, this.worldObj.getTotalWorldTime())) {
                this.furnaceItemStacks[iSlot] = FoodType.doItemDecayFast(item);
                this.onInventoryChanged();
            }
        }
    }

    @Inject(method = "smeltItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemStack;copy()Lnet/minecraft/src/ItemStack;", shift = At.Shift.BY, by = 2))
    private void inheritFoodDecay(CallbackInfo ci) {
        ItemStack oldItem = this.furnaceItemStacks[0];
        ItemStack newItem = this.furnaceItemStacks[2];
        if (oldItem != null && newItem != null) {
            Utils.inheritFoodDecay(oldItem, newItem, this.worldObj.getTotalWorldTime());
        }
    }
}
