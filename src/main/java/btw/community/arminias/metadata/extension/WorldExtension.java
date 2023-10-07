package btw.community.arminias.metadata.extension;

import net.minecraft.src.World;

public interface WorldExtension {
    boolean setBlockWithExtra(int par1, int par2, int par3, int par4, int par5, int par6, int extraMeta);
    int getBlockExtraMetadata(int par1, int par2, int par3);
    boolean setBlockMetadataAndExtraWithNotify(int par1, int par2, int par3, int par4, int par5, int extraMetadata);
    boolean setBlockExtraMetadataWithNotify(int par1, int par2, int par3, int par4, int par5);
    boolean setBlockAndMetadataAndExtraMetadataWithNotify(int i, int j, int k, int iBlockID, int iMetadata, int extraMetadata);

    default boolean SetBlockExtraMetadataWithNotify(int i, int j, int k, int iMetadata, int iNotifyBitField)
    {
        return setBlockExtraMetadataWithNotify(i, j, k, iMetadata, iNotifyBitField);
    }

    default boolean setBlockExtraMetadata(int i, int j, int k, int iMetadata)
    {
        return this.setBlockExtraMetadataWithNotify(i, j, k, iMetadata, 0);
    }

    default boolean setBlockExtraMetadataWithNotify(int i, int j, int k, int iMetadata)
    {
        return this.setBlockExtraMetadataWithNotify(i, j, k, iMetadata, 3);
    }

    default boolean setBlockExtraMetadataWithClient(int i, int j, int k, int iMetadata)
    {
        return this.setBlockExtraMetadataWithNotify(i, j, k, iMetadata, 2);
    }

    default boolean setBlockExtraWithNotifyNoClient(int i, int j, int k, int iMetadata)
    {
        return this.setBlockExtraMetadataWithNotify(i, j, k, iMetadata, 1);
    }

    static WorldExtension cast(World world) {
        return (WorldExtension) world;
    }

}
