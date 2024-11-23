package btw.community.arminias.foodspoil.mixin.plants;

import btw.community.arminias.foodspoil.FoodSpoilMod;
import btw.community.arminias.metadata.extension.WorldExtension;
import btw.item.items.CocoaBeanItem;
import btw.item.items.MushroomItem;
import btw.item.items.PlaceAsBlockItem;
import btw.world.util.BlockPos;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MushroomItem.class)
public abstract class MushroomItemMixin {

    @Inject(method = "onItemUsedByBlockDispenser", at = @At(value = "RETURN"))
    private void onItemUsedByBlockDispenser(ItemStack stack, World world, int i, int j, int k, int iFacing, CallbackInfoReturnable<Boolean> cir) {
        // Check if the item is instance of GourdBlock
        //if (Block.blocksList[this.getBlockID()] instanceof GourdBlock) {
        if (cir.getReturnValue()) {
            if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("spoilDate")) {
                long spoilDate = stack.stackTagCompound.getLong("spoilDate");
                // Set extra metadata using extension
                BlockPos targetPos = new BlockPos(i, j, k, iFacing);
                setSpoilAtTime(world, targetPos.x, targetPos.y, targetPos.z, spoilDate);
            }
        }
        //}
    }

    @Inject(method = "onItemUse", at = @At(value = "RETURN"))
    private void onItemUsed(ItemStack itemStack, EntityPlayer player, World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, CallbackInfoReturnable<Boolean> cir) {
        // Check if the item is instance of GourdBlock
        //if (Block.blocksList[this.getBlockID()] instanceof GourdBlock) {
        if (cir.getReturnValue()) {
            if (itemStack.stackTagCompound != null && itemStack.stackTagCompound.hasKey("spoilDate")) {
                long spoilDate = itemStack.stackTagCompound.getLong("spoilDate");
                // Set extra metadata using extension
                setSpoilAtTime(world, i, j + 1, k, spoilDate);
            }
        }
        //}
    }

    @Unique
    private long getSpoilAtTime(int extra) {
        return (long) extra << FoodSpoilMod.SPOIL_TIME_QUANTIZATION_SHIFT;
    }

    @Unique
    private void setSpoilAtTime(World world, int i, int j, int k, long spoilAtTime) {
        WorldExtension.cast(world).setBlockExtraMetadata(i, j, k, (int) (spoilAtTime >>> FoodSpoilMod.SPOIL_TIME_QUANTIZATION_SHIFT));
    }
}
