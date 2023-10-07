package btw.community.arminias.metadata.extension;

import net.minecraft.src.ChunkCache;

public interface ChunkCacheExtension {
    int getBlockExtraMetadata(int par1, int par2, int par3);

    static ChunkCacheExtension cast(ChunkCache chunkCache) {
        return (ChunkCacheExtension) chunkCache;
    }
}
