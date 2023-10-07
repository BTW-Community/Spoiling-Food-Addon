package btw.community.arminias.metadata.mixin;

import btw.community.arminias.metadata.extension.ChunkExtension;
import btw.community.arminias.metadata.extension.WorldExtension;
import net.minecraft.src.Block;
import net.minecraft.src.Chunk;
import net.minecraft.src.Profiler;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(World.class)
public abstract class WorldMixin implements WorldExtension {

    @Shadow public abstract Chunk getChunkFromChunkCoords(int par1, int par2);

    @Shadow public boolean isRemote;

    @Shadow public abstract void markBlockForUpdate(int par1, int par2, int par3);

    @Shadow public abstract void notifyBlockChange(int par1, int par2, int par3, int par4);

    @Shadow public abstract void func_96440_m(int par1, int par2, int par3, int par4);

    @Shadow @Final public Profiler theProfiler;

    @Shadow public abstract void updateAllLightTypes(int par1, int par2, int par3);

    @Override
    public boolean setBlockWithExtra(int par1, int par2, int par3, int par4, int par5, int par6, int extraMeta)
    {
        if (par1 >= -30000000 && par3 >= -30000000 && par1 < 30000000 && par3 < 30000000)
        {
            if (par2 < 0)
            {
                return false;
            }
            else if (par2 >= 256)
            {
                return false;
            }
            else
            {
                Chunk var7 = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
                int var8 = 0;

                if ((par6 & 1) != 0)
                {
                    var8 = var7.getBlockID(par1 & 15, par2, par3 & 15);
                }

                boolean var9 = ((ChunkExtension) var7).setBlockIDWithMetadataAndExtraMetadata(par1 & 15, par2, par3 & 15, par4, par5, extraMeta);
                this.theProfiler.startSection("checkLight");
                this.updateAllLightTypes(par1, par2, par3);
                this.theProfiler.endSection();

                if (var9)
                {
                    if ((par6 & 2) != 0 && (!this.isRemote || (par6 & 4) == 0))
                    {
                        this.markBlockForUpdate(par1, par2, par3);
                    }

                    if (!this.isRemote && (par6 & 1) != 0)
                    {
                        this.notifyBlockChange(par1, par2, par3, var8);
                        Block var10 = Block.blocksList[par4];

                        if (var10 != null && var10.hasComparatorInputOverride())
                        {
                            this.func_96440_m(par1, par2, par3, par4);
                        }
                    }
                }

                return var9;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns the block extra metadata at coords x,y,z
     */
    @Override
    public int getBlockExtraMetadata(int par1, int par2, int par3)
    {
        if (par1 >= -30000000 && par3 >= -30000000 && par1 < 30000000 && par3 < 30000000)
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
                Chunk var4 = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
                par1 &= 15;
                par3 &= 15;
                return ((ChunkExtension) var4).getBlockExtraMetadata(par1, par2, par3);
            }
        }
        else
        {
            return 0;
        }
    }


    /**
     * Sets the blocks metadata and if set will then notify blocks that this block changed, depending on the flag. Args:
     * x, y, z, metadata, flag, extraMetadata. See setBlock for flag description
     */
    /**
     * FCNOTE: Bit 1 notify neighbors.  Bit 2 sends change to clients.
     * Bit 4 seems to prevent a render update when called on client
     */
    @Override
    public boolean setBlockMetadataAndExtraWithNotify(int par1, int par2, int par3, int par4, int par5, int extraMetadata)
    {
        if (par1 >= -30000000 && par3 >= -30000000 && par1 < 30000000 && par3 < 30000000)
        {
            if (par2 < 0)
            {
                return false;
            }
            else if (par2 >= 256)
            {
                return false;
            }
            else
            {
                Chunk var6 = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
                int var7 = par1 & 15;
                int var8 = par3 & 15;
                boolean var9 = var6.setBlockMetadata(var7, par2, var8, par4);
                boolean var9_e = ((ChunkExtension) var6).setBlockExtraMetadata(var7, par2, var8, extraMetadata);

                if (var9 || var9_e)
                {
                    int var10 = var6.getBlockID(var7, par2, var8);

                    if ((par5 & 2) != 0 && (!this.isRemote || (par5 & 4) == 0))
                    {
                        this.markBlockForUpdate(par1, par2, par3);
                    }

                    if (!this.isRemote && (par5 & 1) != 0)
                    {
                        this.notifyBlockChange(par1, par2, par3, var10);
                        Block var11 = Block.blocksList[var10];

                        if (var11 != null && var11.hasComparatorInputOverride())
                        {
                            this.func_96440_m(par1, par2, par3, var10);
                        }
                    }
                }

                return var9;
            }
        }
        else
        {
            return false;
        }
    }


    /**
     * Sets the blocks extra metadata and if set will then notify blocks that this block changed, depending on the flag. Args:
     * x, y, z, extra metadata, flag. See setBlock for flag description
     */
    /**
     * FCNOTE: Bit 1 notify neighbors.  Bit 2 sends change to clients.
     * Bit 4 seems to prevent a render update when called on client
     */
    @Override
    public boolean setBlockExtraMetadataWithNotify(int par1, int par2, int par3, int par4, int par5)
    {
        if (par1 >= -30000000 && par3 >= -30000000 && par1 < 30000000 && par3 < 30000000)
        {
            if (par2 < 0)
            {
                return false;
            }
            else if (par2 >= 256)
            {
                return false;
            }
            else
            {
                Chunk var6 = this.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
                int var7 = par1 & 15;
                int var8 = par3 & 15;
                boolean var9 = ((ChunkExtension) var6).setBlockExtraMetadata(var7, par2, var8, par4);

                if (var9)
                {
                    int var10 = var6.getBlockID(var7, par2, var8);

                    if ((par5 & 2) != 0 && (!this.isRemote || (par5 & 4) == 0))
                    {
                        this.markBlockForUpdate(par1, par2, par3);
                    }

                    if (!this.isRemote && (par5 & 1) != 0)
                    {
                        this.notifyBlockChange(par1, par2, par3, var10);
                        Block var11 = Block.blocksList[var10];

                        if (var11 != null && var11.hasComparatorInputOverride())
                        {
                            this.func_96440_m(par1, par2, par3, var10);
                        }
                    }
                }

                return var9;
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean setBlockAndMetadataAndExtraMetadataWithNotify(int i, int j, int k, int iBlockID, int iMetadata, int extraMetadata)
    {
        return this.setBlockWithExtra(i, j, k, iBlockID, iMetadata, 3, extraMetadata);
    }
}
