package btw.community.arminias.foodspoil.mixin.plants;

import btw.block.blocks.GourdBlock;
import btw.community.arminias.foodspoil.FoodType;
import btw.community.arminias.metadata.extension.WorldExtension;
import btw.item.items.PlaceAsBlockItem;
import btw.world.util.BlockPos;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlaceAsBlockItem.class)
public abstract class PlaceAsBlockItemMixin {
    @Shadow(aliases = {"getBlockID", "method_3464"}, remap = false)
    public abstract int getBlockID();

    @Inject(method = "onItemUsedByBlockDispenser", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Block;onPostBlockPlaced(Lnet/minecraft/src/World;IIII)V"))
    private void onItemUsedByBlockDispenser(ItemStack stack, World world, int i, int j, int k, int iFacing, CallbackInfoReturnable<Boolean> cir) {
        // Check if the item is instance of GourdBlock
        if (Block.blocksList[this.getBlockID()] instanceof GourdBlock) {
            if (stack.stackTagCompound != null) {
                long spoilDate = stack.stackTagCompound.getLong("spoilDate");
                // Calculate extra metadata from spoil time
                float fraction = 1 - ((float)(spoilDate - world.getTotalWorldTime()) / (float) FoodType.getDecayTimeFast(stack.itemID));
                int extra = ((int)(1 + fraction * FoodSpoilMod.PLANT_SPOIL_AGE) << 1) | 1;
                // Set extra metadata using extension
                BlockPos targetPos = new BlockPos(i, j, k, iFacing);
                WorldExtension.cast(world).setBlockExtraMetadata(targetPos.x, targetPos.y, targetPos.z, extra);
            }
        }
    }
}
