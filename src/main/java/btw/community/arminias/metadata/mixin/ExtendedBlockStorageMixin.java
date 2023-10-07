package btw.community.arminias.metadata.mixin;

import btw.community.arminias.metadata.HunkArray;
import btw.community.arminias.metadata.extension.ExtendedBlockStorageExtension;
import net.minecraft.src.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExtendedBlockStorage.class)
public class ExtendedBlockStorageMixin implements ExtendedBlockStorageExtension {
    private HunkArray blockExtraMetadataArray;
    @Shadow private byte[] blockLSBArray;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(int par1, boolean par2, CallbackInfo ci) {
        this.blockExtraMetadataArray = new HunkArray(this.blockLSBArray.length, 4);
    }

    /**
     * Returns the extra metadata associated with the block at the given coordinates in this ExtendedBlockStorage.
     */
    @Override
    public int getExtBlockExtraMetadata(int par1, int par2, int par3)
    {
        return this.blockExtraMetadataArray.get(par1, par2, par3);
    }

    /**
     * Sets the extra metadata of the Block at the given coordinates in this ExtendedBlockStorage to the given metadata.
     */
    @Override
    public void setExtBlockExtraMetadata(int par1, int par2, int par3, int par4)
    {
        this.blockExtraMetadataArray.set(par1, par2, par3, par4);
    }

    @Override
    public HunkArray getExtraMetadataArray()
    {
        return this.blockExtraMetadataArray;
    }

    /**
     * Sets the HunkArray of block extra metadata (blockMetadataArray) for this ExtendedBlockStorage.
     */
    @Override
    public void setBlockExtraMetadataArray(HunkArray par1HunkArray)
    {
        // This is world conversion code! No extraData -> extraData
        if (par1HunkArray.data.length == 0) {
            par1HunkArray = new HunkArray(this.blockLSBArray.length, 4);
        }
        this.blockExtraMetadataArray = par1HunkArray;
    }
}
