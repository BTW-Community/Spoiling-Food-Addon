package btw.community.arminias.metadata.mixin;

import btw.community.arminias.metadata.PistonHelper;
import btw.community.arminias.metadata.extension.TileEntityPistonExtension;
import btw.community.arminias.metadata.extension.WorldExtension;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockPistonBase.class)
public abstract class BlockPistonBaseMixin extends Block {
    protected BlockPistonBaseMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }

    @Shadow protected abstract boolean isIndirectlyPowered(World par1World, int par2, int par3, int par4, int par5);

    @Shadow protected abstract boolean tryExtend(World world, int i, int j, int k, int iFacing);

    @Shadow @Final protected boolean isSticky;

    @Shadow
    public static NBTTagCompound getBlockTileEntityData(World worldObj, int x, int y, int z) {
        return null;
    }

    @Redirect(method = "updatePistonState", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;setBlockMetadataWithNotify(IIIII)Z"))
    private boolean updatePistonStateRedirect(World world, int x, int y, int z, int metadata, int notify, World par1World, int par2, int par3, int par4) {
        return ((WorldExtension) world).setBlockMetadataAndExtraWithNotify(x, y, z, metadata, notify, ((WorldExtension) world).getBlockExtraMetadata(par2, par3, par4));
    }


    /**
     * @author Arminias
     * Called when the block receives a BlockEvent - see World.addBlockEvent. By default, passes it on to the tile
     * entity at this location. Args: world, x, y, z, blockID, EventID, event parameter
     */
    @Overwrite
    public boolean onBlockEventReceived(World par1World, int par2, int par3, int par4, int par5, int par6)
    {
        if (!par1World.isRemote)
        {
            boolean var7 = this.isIndirectlyPowered(par1World, par2, par3, par4, par6);

            if (var7 && par5 == 1)
            {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, par6 | 8, 2);
                return false;
            }

            if (!var7 && par5 == 0)
            {
                return false;
            }
        }

        if (par5 == 0)
        {
            if (!this.tryExtend(par1World, par2, par3, par4, par6))
            {
                return false;
            }

            par1World.setBlockMetadataWithNotify(par2, par3, par4, par6 | 8, 2);
            par1World.playSoundEffect((double)par2 + 0.5D, (double)par3 + 0.5D, (double)par4 + 0.5D, "tile.piston.out", 0.5F, par1World.rand.nextFloat() * 0.25F + 0.6F);
        }
        else if (par5 == 1)
        {
            TileEntity var16 = par1World.getBlockTileEntity(par2 + Facing.offsetsXForSide[par6], par3 + Facing.offsetsYForSide[par6], par4 + Facing.offsetsZForSide[par6]);

            if (var16 instanceof TileEntityPiston)
            {
                ((TileEntityPiston)var16).clearPistonTileEntity();
            }
            //EDITMICRO
            int extra = ((WorldExtension) par1World).getBlockExtraMetadata(par2, par3, par4);
            ((WorldExtension) par1World).setBlockWithExtra(par2, par3, par4, Block.pistonMoving.blockID, par6, 3, extra);
            par1World.setBlockTileEntity(par2, par3, par4, PistonHelper.getTileEntity(this.blockID, par6, par6, false, true, extra));

            if (this.isSticky)
            {
                int var8 = par2 + Facing.offsetsXForSide[par6] * 2;
                int var9 = par3 + Facing.offsetsYForSide[par6] * 2;
                int var10 = par4 + Facing.offsetsZForSide[par6] * 2;
                int var11 = par1World.getBlockId(var8, var9, var10);
                int var12 = par1World.getBlockMetadata(var8, var9, var10);
                //EDITMICRO
                extra = ((WorldExtension) par1World).getBlockExtraMetadata(var8, var9, var10);
                boolean var13 = false;

                if (var11 == Block.pistonMoving.blockID)
                {
                    TileEntity var14 = par1World.getBlockTileEntity(var8, var9, var10);

                    if (var14 instanceof TileEntityPiston)
                    {
                        TileEntityPiston var15 = (TileEntityPiston)var14;

                        if (var15.getPistonOrientation() == par6 && var15.isExtending())
                        {
                            var15.clearPistonTileEntity();
                            var11 = var15.getStoredBlockID();
                            var12 = var15.getBlockMetadata();
                            extra = ((TileEntityPistonExtension) var15).getBlockExtraMetadata();
                            var13 = true;
                        }
                    }
                }

                Block targetBlock = Block.blocksList[var11];

                if ( !var13 && targetBlock != null &&
                        targetBlock.canBlockBePulledByPiston(par1World, var8, var9, var10, Block.getOppositeFacing(par6)) )
                {
                    // FCMOD: Added
                    var12 =  targetBlock.adjustMetadataForPistonMove(var12);
                    // END FCMOD

                    par2 += Facing.offsetsXForSide[par6];
                    par3 += Facing.offsetsYForSide[par6];
                    par4 += Facing.offsetsZForSide[par6];

                    NBTTagCompound tileEntityData = getBlockTileEntityData(par1World, var8, var9, var10);
                    par1World.removeBlockTileEntity(var8, var9, var10);

                    ((WorldExtension) par1World).setBlockWithExtra(par2, par3, par4, Block.pistonMoving.blockID, var12, 3, extra);
                    par1World.setBlockTileEntity(par2, par3, par4, PistonHelper.getTileEntity(var11, var12, par6, false, false, extra));
                    ((TileEntityPiston) par1World.getBlockTileEntity(par2, par3, par4)).storeTileEntity(tileEntityData);
                    par1World.setBlockToAir(var8, var9, var10);
                }
                else if (!var13)
                {
                    par1World.setBlockToAir(par2 + Facing.offsetsXForSide[par6], par3 + Facing.offsetsYForSide[par6], par4 + Facing.offsetsZForSide[par6]);
                }
            }
            else
            {
                par1World.setBlockToAir(par2 + Facing.offsetsXForSide[par6], par3 + Facing.offsetsYForSide[par6], par4 + Facing.offsetsZForSide[par6]);
            }

            par1World.playSoundEffect((double)par2 + 0.5D, (double)par3 + 0.5D, (double)par4 + 0.5D, "tile.piston.in", 0.5F, par1World.rand.nextFloat() * 0.15F + 0.6F);
        }

        return true;
    }
}
