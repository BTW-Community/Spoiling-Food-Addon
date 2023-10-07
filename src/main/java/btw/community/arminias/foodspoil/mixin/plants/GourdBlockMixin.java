package btw.community.arminias.foodspoil.mixin.plants;

import btw.block.blocks.FallingBlock;
import btw.block.blocks.GourdBlock;
import btw.community.arminias.foodspoil.FoodType;
import btw.community.arminias.metadata.extension.WorldExtension;
import btw.item.util.ItemUtils;
import btw.community.arminias.foodspoil.FoodSpoilAddon;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Override
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLiving par5EntityLiving, ItemStack par6ItemStack) {
        super.onBlockPlacedBy(par1World, par2, par3, par4, par5EntityLiving, par6ItemStack);
        // Get spoil time from item tag compound
        if (par6ItemStack.stackTagCompound != null) {
            long spoilDate = par6ItemStack.stackTagCompound.getLong("spoilDate");
            // Calculate extra metadata from spoil time
            float fraction = 1 - (float)(spoilDate - par1World.getTotalWorldTime()) / (float) FoodType.getDecayTimeFast(par6ItemStack.itemID);
            int extra = ((int)(1 + fraction * FoodSpoilMod.PLANT_SPOIL_AGE) << 1) | 1;
            // Set extra metadata using extension
            WorldExtension.cast(par1World).setBlockExtraMetadata(par2, par3, par4, extra);
        }

    }

    @Override
    public void dropBlockAsItemWithChance(World par1World, int par2, int par3, int par4, int par5, float par6, int par7) {
    }

    @Override
    public void onBlockHarvested(World par1World, int par2, int par3, int par4, int par5, EntityPlayer par6EntityPlayer) {
        if (!par1World.isRemote)
        {
            int par7 = EnchantmentHelper.getFortuneModifier(par6EntityPlayer);
            int var8 = this.quantityDroppedWithBonus(par7, par1World.rand);
            float par6 = 1.0F;

            for (int var9 = 0; var9 < var8; ++var9)
            {
                if (par1World.rand.nextFloat() <= par6)
                {
                    int var10 = this.idDropped(par5, par1World.rand, par7);

                    if (var10 > 0)
                    {
                        ItemStack stack = new ItemStack(var10, 1, this.damageDropped(par5));
                        // Get extra metadata using extension
                        int extra = WorldExtension.cast(par1World).getBlockExtraMetadata(par2, par3, par4);
                        // Set item spoil time as fraction of max spoil time using extra metadata
                        float fraction = 1 - (float)(extra >> 1) / (float)FoodSpoilMod.PLANT_SPOIL_AGE;
                        if (stack.stackTagCompound == null) {
                            stack.setTagCompound(new NBTTagCompound());
                        }
                        stack.stackTagCompound.setLong("spoilDate", (long)(fraction * FoodType.getDecayTimeFast(stack.itemID)) + par1World.getTotalWorldTime());
                        this.dropBlockAsItem_do(par1World, par2, par3, par4, stack);
                    }
                }
            }
        }
    }

    @Override
    public ItemStack getStackRetrievedByBlockDispenser(World world, int i, int j, int k) {
        ItemStack stack = super.getStackRetrievedByBlockDispenser(world, i, j, k);
        if (stack != null) {
            // Get extra metadata using extension
            int extra = WorldExtension.cast(world).getBlockExtraMetadata(i, j, k);
            // Set item spoil time as fraction of max spoil time using extra metadata
            float fraction = 1 - (float)(extra >> 1) / (float)FoodSpoilMod.PLANT_SPOIL_AGE;
            if (stack.stackTagCompound == null) {
                stack.setTagCompound(new NBTTagCompound());
            }
            stack.stackTagCompound.setLong("spoilDate", (long)(fraction * FoodType.getDecayTimeFast(stack.itemID)) + world.getTotalWorldTime());
        }
        return stack;
    }

    // TODO Remove
    /*@Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick) {
        System.out.println(WorldExtension.cast(world).getBlockExtraMetadata(i, j, k));
        return super.onBlockActivated(world, i, j, k, player, iFacing, fXClick, fYClick, fZClick);
    }*/
}
