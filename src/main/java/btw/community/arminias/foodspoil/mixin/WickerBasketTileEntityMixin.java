package btw.community.arminias.foodspoil.mixin;

import btw.block.tileentity.WickerBasketTileEntity;
import btw.community.arminias.foodspoil.Utils;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(WickerBasketTileEntity.class)
public abstract class WickerBasketTileEntityMixin extends TileEntity {
    @Shadow public abstract ItemStack getStorageStack();

    private long lastTick = 0;


    @Inject(method = "updateEntity", at = @At("HEAD"))
    private void updateEntity(CallbackInfo ci) {
        if (!worldObj.isRemote) {
            long tick = this.worldObj.getTotalWorldTime();
            ItemStack item = this.getStorageStack();
            NBTTagCompound tag;
            long spoilDate;
            if (item != null && tick % 10 == 0 && (tag = item.getTagCompound()) != null &&
                    (spoilDate = tag.getLong("spoilDate")) > 0 && lastTick != 0) {
                // Check if on ice or snow
                Block block = Block.blocksList[this.worldObj.getBlockId(this.xCoord, this.yCoord - 1, this.zCoord)];
                if (Utils.isCoolingBlock(block)) {
                    spoilDate = (long) (spoilDate + (tick - lastTick) * FoodSpoilMod.WORLD_ICE_PRESERVATION_FACTOR);
                    tag.setLong("spoilDate", spoilDate);
                }
                this.lastTick = tick;
            } else if (lastTick == 0 || tick % 10 == 0) {
                lastTick = tick;
            }
        }
    }
}
