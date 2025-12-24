package btw.community.arminias.foodspoil.mixin.plants;

import api.block.blocks.CropsBlock;
import btw.block.blocks.PlantsBlock;
import btw.community.arminias.metadata.extension.WorldExtension;
import api.item.util.ItemUtils;
import btw.community.arminias.foodspoil.FoodSpoilAddon;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(CropsBlock.class)
public abstract class CropsBlockMixin extends PlantsBlock {
    @Shadow protected abstract boolean isFullyGrown(World world, int i, int j, int k);

    protected CropsBlockMixin(int iBlockID, Material material) {
        super(iBlockID, material);
    }

    @Inject(method = "updateTick", at = @At(value = "INVOKE", target = "Lapi/block/blocks/CropsBlock;isFullyGrown(Lnet/minecraft/src/World;III)Z"), cancellable = true)
    private void updateTick(World world, int i, int j, int k, Random rand, CallbackInfo ci) {
        if (isFullyGrown(world, i, j, k)) {
            // Get extra metadata using extension
            int extra = WorldExtension.cast(world).getBlockExtraMetadata(i, j, k);
            //System.out.println(extra);
            // Don't update if the plant is generated and not planted
            if (extra == 0) {
                return;
            } else if (extra == -1) {
                // Generate a new spoil time
                setSpoilAtTime(world, i, j, k, world.getTotalWorldTime() + FoodSpoilAddon.getPlantSpoilAge() * 24000L * (getIsFertilizedForPlantGrowth(world, i, j - 1, k) ? 2 : 1));
            }
            //if ((extra >> 1) > FoodSpoilMod.PLANT_SPOIL_AGE) {
            else if (getSpoilAtTime(extra) < world.getTotalWorldTime()) {
                // Kill the plant
                world.setBlockToAir(i, j, k);
                ItemUtils.dropStackAsIfBlockHarvested(world, i, j, k, new ItemStack(FoodSpoilAddon.spoiledCrop));
                ci.cancel();
            } /*else {
                updateHasUpdatedToday(world, i, j, k, extra);
            }*/
        }
    }

    @Unique
    private long getSpoilAtTime(int extra) {
        return (long) extra << FoodSpoilMod.SPOIL_TIME_QUANTIZATION_SHIFT;
    }

    @Unique
    private void setSpoilAtTime(World world, int i, int j, int k, long spoilAtTime) {
        WorldExtension.cast(world).setBlockExtraMetadata(i, j, k, (int) (spoilAtTime >>> FoodSpoilMod.SPOIL_TIME_QUANTIZATION_SHIFT));
    }

    // TODO Remove
    /*@Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick) {
        System.out.println(WorldExtension.cast(world).getBlockExtraMetadata(i, j, k));
        return super.onBlockActivated(world, i, j, k, player, iFacing, fXClick, fYClick, fZClick);
    }*/
}
