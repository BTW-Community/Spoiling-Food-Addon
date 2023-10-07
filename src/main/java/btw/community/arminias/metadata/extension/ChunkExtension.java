package btw.community.arminias.metadata.extension;

import net.minecraft.src.Chunk;

public interface ChunkExtension {
    int getBlockExtraMetadata(int par1, int par2, int par3);
    boolean setBlockIDWithMetadataAndExtraMetadata(int par1, int par2, int par3, int par4, int par5, int extraMeta);
    boolean setBlockExtraMetadata(int par1, int par2, int par3, int par4);

    static ChunkExtension cast(Chunk chunk) {
        return (ChunkExtension) chunk;
    }
}
