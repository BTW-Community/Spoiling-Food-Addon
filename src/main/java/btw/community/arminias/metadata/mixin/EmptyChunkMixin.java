package btw.community.arminias.metadata.mixin;

import btw.community.arminias.metadata.extension.EmptyChunkExtension;
import net.minecraft.src.EmptyChunk;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EmptyChunk.class)
public abstract class EmptyChunkMixin implements EmptyChunkExtension {

    /**
     * Return the extra metadata corresponding to the given coordinates inside a chunk.
     */
    @Override
    public int getBlockExtraMetadata(int par1, int par2, int par3)
    {
        return 0;
    }

    /**
     * Set the extra metadata of a block in the chunk
     */
    @Override
    public boolean setBlockExtraMetadata(int par1, int par2, int par3, int par4)
    {
        return false;
    }

}
