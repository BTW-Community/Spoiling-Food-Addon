package btw.community.arminias.foodspoil.mixin;

import btw.block.tileentity.WickerBasketTileEntity;
import btw.community.arminias.foodspoil.FoodSpoilAddon;
import btw.community.arminias.foodspoil.FoodType;
import btw.community.arminias.foodspoil.Utils;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(WickerBasketTileEntity.class)
public abstract class WickerBasketTileEntityMixin extends TileEntity {

    @Shadow(remap = false)
    public abstract ItemStack getSlotStack(int iSlot);


    @Shadow(aliases = {"storageStacks", "contents"}, remap = false)
    private ItemStack[] inventoryContents;

    @Unique
    private long lastTick = 0;

    @Inject(method = {"getStackInSlot", "getSlotStack"}, at = @At("HEAD"), remap = false)
    private void getStackInSlot(int iSlot, CallbackInfoReturnable<ItemStack> cir) {
        if (this.worldObj != null && !this.worldObj.isRemote && this.inventoryContents != null) {
            ItemStack item = this.inventoryContents[iSlot];
            if (Utils.isBeyondSpoilDate(item, this.worldObj.getTotalWorldTime())) {
                this.inventoryContents[iSlot] = FoodType.doItemDecayFast(item);
                this.onInventoryChanged();
                // Extra in this class (WickerBasketTileEntity)
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            }
        }
    }

    @Inject(method = "updateEntity", at = @At("HEAD"))
    private void updateEntity(CallbackInfo ci) {
        if (!worldObj.isRemote) {
            long tick = this.worldObj.getTotalWorldTime();
            if (tick % 10 == 0 && lastTick != 0) {
                for (int i = 0; i < this.inventoryContents.length; i++) {
                    ItemStack item = inventoryContents[i];
                    NBTTagCompound tag;
                    long spoilDate;
                    if (item != null && (tag = item.getTagCompound()) != null && tag.hasKey("spoilDate") &&
                            (spoilDate = tag.getLong("spoilDate")) > 0) {
                        // Check if on ice or snow
                        Block block = Block.blocksList[this.worldObj.getBlockId(this.xCoord, this.yCoord - 1, this.zCoord)];
                        if (Utils.isCoolingBlock(block)) {
                            spoilDate = (long) (spoilDate + (tick - lastTick) * FoodSpoilAddon.getWorldIcePreservationFactor());
                            tag.setLong("spoilDate", spoilDate);
                        }
                    }

                    // Calls the spoil code mixin
                    this.getSlotStack(i);
                }
                this.lastTick = tick;
            } else if (lastTick == 0) {
                lastTick = tick;
            }
        }
    }
}
