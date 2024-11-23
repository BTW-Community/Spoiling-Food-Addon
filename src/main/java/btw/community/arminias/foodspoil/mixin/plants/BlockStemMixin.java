package btw.community.arminias.foodspoil.mixin.plants;

import btw.block.blocks.PlantsBlock;
import btw.community.arminias.foodspoil.FoodSpoilAddon;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import btw.community.arminias.metadata.extension.WorldExtension;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockStem.class)
public abstract class BlockStemMixin extends PlantsBlock {
    @Shadow protected Block fruitType;

    protected BlockStemMixin(int iBlockID, Material material) {
        super(iBlockID, material);
    }

    @Redirect(method = "checkForGrowth", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;setBlockWithNotify(IIII)Z"))
    private boolean checkForGrowth(World world, int i, int j, int k, int iBlockID) {
        setSpoilAtTime(world, i, j, k, world.getTotalWorldTime() + FoodSpoilAddon.getPlantSpoilAge() * 24000L * (getIsFertilizedForPlantGrowth(world, i, j - 1, k) ? 2 : 1));
        return world.setBlockWithNotify(i, j, k, this.fruitType.blockID);
    }

    @Redirect(method = "checkForGrowth", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;setBlockMetadataWithNotify(IIII)Z", ordinal = 1))
    private boolean checkForGrowth2(World world, int i, int j, int k, int meta) {
        setSpoilAtTime(world, i, j, k, world.getTotalWorldTime() + FoodSpoilAddon.getPlantSpoilAge() * 24000L * (getIsFertilizedForPlantGrowth(world, i, j - 1, k) ? 2 : 1));
        return world.setBlockMetadataWithNotify(i, j, k, meta);
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
