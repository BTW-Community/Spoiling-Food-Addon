package btw.community.arminias.metadata.extension;

import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityPiston;

public interface TileEntityPistonExtension {
    int getBlockExtraMetadata();
    TileEntity setExtraMetadata(int extraMeta);

    static TileEntityPistonExtension cast(TileEntity tileEntity) {
        return (TileEntityPistonExtension) tileEntity;
    }
}
