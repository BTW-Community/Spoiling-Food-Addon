package btw.community.arminias.metadata;

import btw.community.arminias.metadata.extension.TileEntityPistonExtension;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityPiston;

public class PistonHelper {
    /**
     * gets a new TileEntityPiston created with the arguments provided.
     */
    public static TileEntity getTileEntity(int par0, int par1, int par2, boolean par3, boolean par4, int extraMetadata)
    {
        return ((TileEntityPistonExtension)(new TileEntityPiston(par0, par1, par2, par3, par4))).setExtraMetadata(extraMetadata);
    }

    public static TileEntity getShoveledTileEntity(int iBlockID, int iMetadata, int iFacing, int extraMetadata)
    {
        return ((TileEntityPistonExtension)(new TileEntityPiston( iBlockID, iMetadata, iFacing, true, false, true))).setExtraMetadata(extraMetadata);
    }
}
