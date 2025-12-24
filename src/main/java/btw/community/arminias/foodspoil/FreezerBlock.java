package btw.community.arminias.foodspoil;

import api.inventory.InventoryUtils;
import api.world.WorldUtils;
import btw.BTWMod;
import btw.block.blocks.VesselBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class FreezerBlock extends VesselBlock {
    public FreezerBlock(int id) {
        super(id, Block.ice.blockMaterial);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon( int iSide, int iMetadata )
    {
        return iconBySideArray[iSide];
    }

    @Environment(EnvType.CLIENT)
    private Icon[] iconBySideArray;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        iconBySideArray = new Icon[6];

        Icon bottomIcon = register.registerIcon("foodspoilmod:freezer_bottom");

        blockIcon = bottomIcon; // for hit effects

        iconBySideArray[0] = bottomIcon;
        iconBySideArray[1] = register.registerIcon("foodspoilmod:freezer_top");

        Icon sideIcon = register.registerIcon("foodspoilmod:freezer_side");

        iconBySideArray[2] = sideIcon;
        iconBySideArray[3] = sideIcon;
        iconBySideArray[4] = sideIcon;
        iconBySideArray[5] = sideIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k)
    {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
                renderer.blockAccess, i, j, k));

        return renderer.renderStandardBlock(this, i, j, k);
    }

    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
        renderBlocks.renderBlockAsItemVanilla( this, iItemDamage, fBrightness );
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return new TileEntityFreezer();
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    @Override
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        super.onBlockAdded(par1World, par2, par3, par4);
        TileEntityFreezer var5 = (TileEntityFreezer) this.createNewTileEntity(par1World);
        par1World.setBlockTileEntity(par2, par3, par4, var5);
        var5.onNeighborChanged();
    }

    @Override
    public void breakBlock( World world, int i, int j, int k, int iBlockID, int iMetadata )
    {
        InventoryUtils.ejectInventoryContents(world, i, j, k, (IInventory)world.getBlockTileEntity(i, j, k));

        super.breakBlock( world, i, j, k, iBlockID, iMetadata );
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        TileEntityFreezer freezer = this.getInventory(par1World, par2, par3, par4);
        if (par5EntityPlayer.getHeldItem() != null && par5EntityPlayer.getHeldItem().getItem() == Item.glassBottle) {
            if (freezer.hasPotionReady()) {
                if (par5EntityPlayer.inventory.addItemStackToInventory(freezer.getPotion())) {
                    par5EntityPlayer.getHeldItem().stackSize--;
                    freezer.resetPotion();
                    freezer.decrementOverflowCounter();
                    if (par5EntityPlayer.getHeldItem().stackSize <= 0) {
                        par5EntityPlayer.inventory.setInventorySlotContents(par5EntityPlayer.inventory.currentItem, null);
                    }
                    return true;
                }
            } else if (freezer.getWaterLevel() > 0) {
                if (par5EntityPlayer.inventory.addItemStackToInventory(new ItemStack(Item.potion, 1, 0))) {
                    par5EntityPlayer.getHeldItem().stackSize--;
                    freezer.decrementOverflowCounter();
                    if (par5EntityPlayer.getHeldItem().stackSize <= 0) {
                        par5EntityPlayer.inventory.setInventorySlotContents(par5EntityPlayer.inventory.currentItem, null);
                    }
                    return true;
                }
            }
        }
        if (par1World.isRemote)
        {
            return true;
        }
        else if (!WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(par1World, par2, par3 + 1, par4, 0))
        {
            if (par5EntityPlayer instanceof EntityPlayerMP) // should always be true
            {
                ContainerFreezer container = new ContainerFreezer( par5EntityPlayer.inventory, freezer);

                BTWMod.serverOpenCustomInterface((EntityPlayerMP)par5EntityPlayer, container, ContainerFreezer.ID);
            }
        }
        return true;

    }

    /**
     * Gets the inventory of the chest at the specified coords, accounting for blocks or ocelots on top of the chest,
     * and double chests.
     */
    public TileEntityFreezer getInventory(World par1World, int par2, int par3, int par4)
    {
        Object var5 = par1World.getBlockTileEntity(par2, par3, par4);

        if (!(var5 instanceof TileEntityFreezer))
        {
            return null;
        }
        else if (par1World.isBlockNormalCube(par2, par3 + 1, par4))
        {
            return null;
        }
        else
        {
            return (TileEntityFreezer)var5;
        }
    }

    @Override
    public boolean isOpaqueCube() {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return true;
    }

    @Override
    public void onNeighborBlockChange(World world, int i, int j, int k, int iBlockID) {
        super.onNeighborBlockChange(world, i, j, k, iBlockID);
        TileEntityFreezer freezer = getInventory(world, i, j, k);
        if (freezer != null) {
            freezer.onNeighborChanged();
        }
    }
}
