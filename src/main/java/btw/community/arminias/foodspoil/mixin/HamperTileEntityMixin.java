package btw.community.arminias.foodspoil.mixin;

import btw.block.tileentity.HamperTileEntity;
import btw.community.arminias.foodspoil.FoodSpoilAddon;
import btw.community.arminias.foodspoil.Utils;
import btw.community.arminias.foodspoil.FoodSpoilMod;
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


@Mixin(HamperTileEntity.class)
public abstract class HamperTileEntityMixin extends TileEntity {

    @Shadow(aliases = {"method_2381", "getStackInSlot"})
    public abstract ItemStack getStackInSlot(int iSlot);

    @Shadow public abstract int getSizeInventory();

    @Shadow private ItemStack[] contents;
    @Unique
    private long lastTick = 0;


    @Inject(method = "updateEntity", at = @At("HEAD"))
    private void updateEntity(CallbackInfo ci) {
        if (!worldObj.isRemote) {
            long tick = this.worldObj.getTotalWorldTime();
            if (tick % 10 == 0 && lastTick != 0) {
                for (int i = 0; i < this.getSizeInventory(); i++) {
                    ItemStack item = contents[i];
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
                    this.getStackInSlot(i);
                }
                this.lastTick = tick;
            } else if (lastTick == 0) {
                lastTick = tick;
            }
        }
    }
}
