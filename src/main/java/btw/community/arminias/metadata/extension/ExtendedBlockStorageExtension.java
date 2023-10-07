package btw.community.arminias.metadata.extension;

import btw.community.arminias.metadata.HunkArray;
import net.minecraft.src.ExtendedBlockStorage;

public interface ExtendedBlockStorageExtension {
    int getExtBlockExtraMetadata(int par1, int par2, int par3);
    void setExtBlockExtraMetadata(int par1, int par2, int par3, int par4);
    HunkArray getExtraMetadataArray();
    void setBlockExtraMetadataArray(HunkArray par1HunkArray);

    static ExtendedBlockStorageExtension cast(ExtendedBlockStorage extendedBlockStorage) {
        return (ExtendedBlockStorageExtension) extendedBlockStorage;
    }

}
