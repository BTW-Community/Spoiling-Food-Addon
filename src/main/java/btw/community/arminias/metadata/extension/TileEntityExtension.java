package btw.community.arminias.metadata.extension;

import net.minecraft.src.TileEntity;

public interface TileEntityExtension {
    void setBlockExtraMetadata(int par1);
    int getBlockExtraMetadata();

    static TileEntityExtension cast(TileEntity tileEntity) {
        return (TileEntityExtension) tileEntity;
    }
}
