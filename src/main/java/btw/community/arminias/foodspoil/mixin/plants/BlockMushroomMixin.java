package btw.community.arminias.foodspoil.mixin.plants;

import btw.block.blocks.MushroomBlockBrown;
import btw.community.arminias.foodspoil.FoodSpoilAddon;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import btw.community.arminias.metadata.extension.WorldExtension;
import btw.item.util.ItemUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin({BlockMushroom.class, MushroomBlockBrown.class})
public abstract class BlockMushroomMixin extends BlockFlower {
    protected BlockMushroomMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }

    @Inject(method = "updateTick", at = @At(value = "HEAD"), cancellable = true)
    private void updateTick(World world, int i, int j, int k, Random rand, CallbackInfo ci) {
        if (world.getBlockId(i, j, k) == this.blockID) {
            // Get extra metadata using extension
            int extra = WorldExtension.cast(world).getBlockExtraMetadata(i, j, k);
            //System.out.println(extra);
            // Don't update if the plant is generated and not planted
            if (extra == 0) {
                return;
            } else if (extra == -1) {
                // Generate a new spoil time
                setSpoilAtTime(world, i, j, k, world.getTotalWorldTime() + FoodSpoilAddon.getPlantSpoilAge() * 24000L);
            }
            //if ((extra >> 1) > FoodSpoilMod.PLANT_SPOIL_AGE) {
            else if (getSpoilAtTime(extra) < world.getTotalWorldTime()) {
                // Kill the plant
                world.setBlockToAir(i, j, k);
                world.playAuxSFX(2000, i, j, k, 0);
                //ItemUtils.dropStackAsIfBlockHarvested(world, i, j, k, new ItemStack(FoodSpoilAddon.spoiledCrop));
                ci.cancel();
            } /*else {
                updateHasUpdatedToday(world, i, j, k, extra);
            }*/
        }
    }

    @Override
    public void dropBlockAsItem_do(World par1World, int par2, int par3, int par4, ItemStack par5ItemStack) {
        // Get extra metadata using extension
        int extra = WorldExtension.cast(par1World).getBlockExtraMetadata(par2, par3, par4);
        if (extra == 0) {
            super.dropBlockAsItem_do(par1World, par2, par3, par4, par5ItemStack);
            return;
        }
        // Set item spoil time as fraction of max spoil time using extra metadata
        if (par5ItemStack.stackTagCompound == null) {
            par5ItemStack.setTagCompound(new NBTTagCompound());
        }
        par5ItemStack.stackTagCompound.setLong("spoilDate", getSpoilAtTime(extra));
        par5ItemStack.stackTagCompound.setLong("creationDate", par1World.getTotalWorldTime());

        WorldExtension.cast(par1World).setBlockExtraMetadata(par2, par3, par4, 0);

        super.dropBlockAsItem_do(par1World, par2, par3, par4, par5ItemStack);
    }

    @Override
    public ItemStack getStackRetrievedByBlockDispenser(World world, int i, int j, int k) {
        ItemStack stack = super.getStackRetrievedByBlockDispenser(world, i, j, k);
        if (stack != null) {
            // Get extra metadata using extension
            int extra = WorldExtension.cast(world).getBlockExtraMetadata(i, j, k);
            if (extra == 0) {
                return stack;
            }
            // Set item spoil time as fraction of max spoil time using extra metadata
            if (stack.stackTagCompound == null) {
                stack.setTagCompound(new NBTTagCompound());
            }
            stack.stackTagCompound.setLong("spoilDate", getSpoilAtTime(extra));
            stack.stackTagCompound.setLong("creationDate", world.getTotalWorldTime());

            WorldExtension.cast(world).setBlockExtraMetadata(i, j, k, 0);
        }
        return stack;
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
        //System.out.println(WorldExtension.cast(world).getBlockExtraMetadata(i, j, k));
        player.sendChatToPlayer(ChatMessageComponent.createFromText("Metadata: " + WorldExtension.cast(world).getBlockExtraMetadata(i, j, k)));
        return super.onBlockActivated(world, i, j, k, player, iFacing, fXClick, fYClick, fZClick);
    }*/
}
