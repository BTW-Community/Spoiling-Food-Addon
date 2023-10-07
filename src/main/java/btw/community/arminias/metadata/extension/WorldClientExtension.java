package btw.community.arminias.metadata.extension;

import net.minecraft.src.World;

public interface WorldClientExtension {
    boolean setBlockAndMetadataAndExtraMetadataAndInvalidate(int x, int y, int z, int type, int meta, int extraMeta);

    static WorldClientExtension cast(World world) {
        return (WorldClientExtension) world;
    }
}
