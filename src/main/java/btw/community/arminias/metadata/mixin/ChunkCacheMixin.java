package btw.community.arminias.metadata.mixin;

import btw.community.arminias.metadata.extension.ChunkCacheExtension;
import btw.community.arminias.metadata.extension.ChunkExtension;
import net.minecraft.src.Chunk;
import net.minecraft.src.ChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkCache.class)
public class ChunkCacheMixin implements ChunkCacheExtension {
    @Shadow private int chunkX;

    @Shadow private int chunkZ;

    @Shadow private Chunk[][] chunkArray;

    @Override
    public int getBlockExtraMetadata(int par1, int par2, int par3)
    {
        if (par2 < 0)
        {
            return 0;
        }
        else if (par2 >= 256)
        {
            return 0;
        }
        else
        {
            int var4 = (par1 >> 4) - this.chunkX;
            int var5 = (par3 >> 4) - this.chunkZ;
            return ((ChunkExtension) this.chunkArray[var4][var5]).getBlockExtraMetadata(par1 & 15, par2, par3 & 15);
        }
    }
}
