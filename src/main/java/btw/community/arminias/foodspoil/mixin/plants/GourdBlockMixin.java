package btw.community.arminias.foodspoil.mixin.plants;

import api.block.blocks.FallingBlock;
import btw.block.blocks.GourdBlock;
import btw.community.arminias.foodspoil.FoodType;
import btw.community.arminias.metadata.extension.WorldExtension;
import api.item.util.ItemUtils;
import btw.community.arminias.foodspoil.FoodSpoilAddon;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(GourdBlock.class)
public abstract class GourdBlockMixin extends FallingBlock {

    protected GourdBlockMixin(int iBlockID, Material material) {
        super(iBlockID, material);
    }

    @Inject(method = "updateTick", at = @At(value = "INVOKE", target = "Lbtw/block/blocks/GourdBlock;validateConnectionState(Lnet/minecraft/src/World;III)V"), cancellable = true)
    private void updateTick(World world, int i, int j, int k, Random rand, CallbackInfo ci) {
        // Get extra metadata using extension
        int extra = WorldExtension.cast(world).getBlockExtraMetadata(i, j, k);
        //System.out.println(extra);
        // Don't update if the plant is generated and not planted
        if (extra == 0) {
            return;
        }/* else if (extra == -1) {
            // Generate a new spoil time
            setSpoilAtTime(world, i, j, k, world.getTotalWorldTime() + FoodSpoilAddon.getPlantSpoilAge() * 24000L);
        }*/
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

    @Override
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLiving, ItemStack par6ItemStack) {
        super.onBlockPlacedBy(par1World, par2, par3, par4, par5EntityLiving, par6ItemStack);
        // Get spoil time from item tag compound
        if (par6ItemStack.stackTagCompound != null) {
            long spoilDate = par6ItemStack.stackTagCompound.getLong("spoilDate");
            setSpoilAtTime(par1World, par2, par3, par4, spoilDate);
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

    // TODO Maybe adapt this to use the spoil time of the gourd that gets exploded, dunno, would require changing stuff somewhere else as well
    /*@Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityItem;<init>(Lnet/minecraft/src/World;DDDLnet/minecraft/src/ItemStack;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void explode(World world, double posX, double posY, double posZ, CallbackInfo ci, Item itemToDrop, int iTempCount, ItemStack itemStack) {
        // Get extra metadata using extension
        int extra = WorldExtension.cast(world).getBlockExtraMetadata((int) posX, (int) posY, (int) posZ);
        // Set item spoil time as fraction of max spoil time using extra metadata
        if (itemStack.stackTagCompound == null) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        itemStack.stackTagCompound.setLong("spoilDate", getSpoilAtTime(extra));
    }*/

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
