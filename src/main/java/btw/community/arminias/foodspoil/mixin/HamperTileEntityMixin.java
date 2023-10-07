package btw.community.arminias.foodspoil.mixin;

import btw.block.tileentity.HamperTileEntity;
import btw.community.arminias.foodspoil.Utils;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(HamperTileEntity.class)
public abstract class HamperTileEntityMixin extends TileEntity {

    @Shadow(aliases = {"method_2381"})
    public abstract ItemStack getStackInSlot(int iSlot);

    @Shadow public abstract int getSizeInventory();

    private long lastTick = 0;


    @Inject(method = "updateEntity", at = @At("HEAD"))
    private void updateEntity(CallbackInfo ci) {
        if (!worldObj.isRemote) {
            long tick = this.worldObj.getTotalWorldTime();
            if (tick % 10 == 0 && lastTick != 0) {
                for (int i = 0; i < this.getSizeInventory(); i++) {
                    ItemStack item = this.getStackInSlot(i);
                    NBTTagCompound tag;
                    long spoilDate;
                    if (item != null && (tag = item.getTagCompound()) != null &&
                            (spoilDate = tag.getLong("spoilDate")) > 0) {
                        // Check if on ice or snow
                        Block block = Block.blocksList[this.worldObj.getBlockId(this.xCoord, this.yCoord - 1, this.zCoord)];
                        if (Utils.isCoolingBlock(block)) {
                            spoilDate = (long) (spoilDate + (tick - lastTick) * FoodSpoilMod.WORLD_ICE_PRESERVATION_FACTOR);
                            tag.setLong("spoilDate", spoilDate);
                        }
                    }
                }
                this.lastTick = tick;
            } else if (lastTick == 0) {
                lastTick = tick;
            }
        }
    }
}
