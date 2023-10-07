package btw.community.arminias.metadata.mixin;

import btw.community.arminias.metadata.extension.WorldClientExtension;
import btw.community.arminias.metadata.extension.WorldExtension;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WorldClient.class)
public abstract class WorldClientMixin extends World implements WorldClientExtension {
    public WorldClientMixin(ISaveHandler par1ISaveHandler, String par2Str, WorldProvider par3WorldProvider, WorldSettings par4WorldSettings, Profiler par5Profiler, ILogAgent par6ILogAgent) {
        super(par1ISaveHandler, par2Str, par3WorldProvider, par4WorldSettings, par5Profiler, par6ILogAgent);
    }

    @Shadow public abstract void invalidateBlockReceiveRegion(int par1, int par2, int par3, int par4, int par5, int par6);

    @Override
    public boolean setBlockAndMetadataAndExtraMetadataAndInvalidate(int x, int y, int z, int type, int meta, int extraMeta) {
        this.invalidateBlockReceiveRegion(x, y, z, x, y, z);

        int oldBlockID = getBlockId(x , y, z);

        if (oldBlockID == type)
        {
            Block block = Block.blocksList[oldBlockID];

            if (block != null)
            {
                int iOldBlockMetadata = getBlockMetadata(x, y, z);

                block.clientNotificationOfMetadataChange(this, x, y, z, iOldBlockMetadata, meta);
            }
        }

        return ((WorldExtension) this).setBlockWithExtra(x, y, z, type, meta, 3, extraMeta);
    }
}
