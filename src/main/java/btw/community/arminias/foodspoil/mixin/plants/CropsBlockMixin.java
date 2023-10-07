package btw.community.arminias.foodspoil.mixin.plants;

import btw.block.blocks.CropsBlock;
import btw.block.blocks.PlantsBlock;
import btw.community.arminias.metadata.extension.WorldExtension;
import btw.item.util.ItemUtils;
import btw.community.arminias.foodspoil.FoodSpoilAddon;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    @Inject(method = "updateTick", at = @At(value = "INVOKE", target = "Lbtw/block/blocks/CropsBlock;isFullyGrown(Lnet/minecraft/src/World;III)Z"), cancellable = true)
    private void updateTick(World world, int i, int j, int k, Random rand, CallbackInfo ci) {
        if (isFullyGrown(world, i, j, k)) {
            // Get extra metadata using extension
            int extra = WorldExtension.cast(world).getBlockExtraMetadata(i, j, k);
            //System.out.println(extra);
            // Don't update if the plant is generated and not planted
            if (extra == 0) {
                return;
            }
            if ((extra >> 1) > FoodSpoilMod.PLANT_SPOIL_AGE) {
                // Kill the plant
                world.setBlockToAir(i, j, k);
                ItemUtils.dropStackAsIfBlockHarvested(world, i, j, k, new ItemStack(FoodSpoilAddon.spoiledCrop));
                ci.cancel();
            } else {
                updateHasUpdatedToday(world, i, j, k, extra);
            }
        }
    }

    private void updateHasUpdatedToday(World world, int i, int j, int k, int extra) {
        int timeOfDay = (int)(world.worldInfo.getWorldTime() % 24000L);
        boolean hasUpdatedToday = (extra & 1) == 1;
        if (hasUpdatedToday && timeOfDay > 14000 && timeOfDay < 22000) {
            WorldExtension.cast(world).setBlockExtraMetadata(i, j, k, extra & 0xFFFFFFFE);
        }
        else if (!hasUpdatedToday && (timeOfDay > 22000 || timeOfDay < 14000)) {
            WorldExtension.cast(world).setBlockExtraMetadata(i, j, k, (extra + 2) | 1);
        }
    }

    // TODO Remove
    /*@Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick) {
        System.out.println(WorldExtension.cast(world).getBlockExtraMetadata(i, j, k));
        return super.onBlockActivated(world, i, j, k, player, iFacing, fXClick, fYClick, fZClick);
    }*/
}
