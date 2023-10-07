package btw.community.arminias.metadata.extension;

import net.minecraft.src.IBlockAccess;

public interface IBlockAccessExtension {
    int getBlockExtraMetadata(int var1, int var2, int var3);

    static IBlockAccessExtension cast(IBlockAccess blockAccess) {
        return (IBlockAccessExtension) blockAccess;
    }
}
