package btw.community.arminias.foodspoil.mixin.plants;

import btw.block.blocks.GourdBlock;
import btw.community.arminias.foodspoil.FoodSpoilAddon;
import btw.community.arminias.foodspoil.FoodType;
import btw.community.arminias.metadata.extension.WorldExtension;
import btw.item.items.PlaceAsBlockItem;
import btw.world.util.BlockPos;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlaceAsBlockItem.class)
public abstract class PlaceAsBlockItemMixin {
    @Redirect(method = "onItemUsedByBlockDispenser", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Block;onPostBlockPlaced(Lnet/minecraft/src/World;IIII)V"))
    private void onItemUsedByBlockDispenser(Block instance, World world, int i, int j, int k, int meta, ItemStack stack, World world2, int i2, int j2, int k2, int iFacing) {
        // Check if the item is instance of GourdBlock
    //if (Block.blocksList[this.getBlockID()] instanceof GourdBlock) {
        if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("spoilDate")) {
            long spoilDate = stack.stackTagCompound.getLong("spoilDate");
            // Set extra metadata using extension
            setSpoilAtTime(world, i, j, k, spoilDate);
        }
        instance.onPostBlockPlaced(world, i, j, k, meta);
        //}
    }

    @Redirect(method = "onItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Block;onPostBlockPlaced(Lnet/minecraft/src/World;IIII)V"))
    private void onItemUsed(Block instance, World par1World, int i, int j, int k, int meta, ItemStack itemStack, EntityPlayer player, World world, int i2, int j2, int k2, int iFacing, float fClickX, float fClickY, float fClickZ) {
        // Check if the item is instance of GourdBlock
        //if (Block.blocksList[this.getBlockID()] instanceof GourdBlock) {
        if (itemStack.stackTagCompound != null && itemStack.stackTagCompound.hasKey("spoilDate")) {
            long spoilDate = itemStack.stackTagCompound.getLong("spoilDate");
            // Set extra metadata using extension
            setSpoilAtTime(world, i, j, k, spoilDate);
        }
        instance.onPostBlockPlaced(world, i, j, k, meta);
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
