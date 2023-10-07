package btw.community.arminias.metadata.mixin;

import btw.community.arminias.metadata.HunkArray;
import btw.community.arminias.metadata.extension.ChunkExtension;
import btw.community.arminias.metadata.extension.ExtendedBlockStorageExtension;
import btw.community.arminias.metadata.extension.TileEntityExtension;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.nio.ByteBuffer;
import java.util.Map;

@Mixin(Chunk.class)
public abstract class ChunkMixin implements ChunkExtension {
    @Shadow private ExtendedBlockStorage[] storageArrays;

    @Shadow public World worldObj;

    @Shadow private byte[] blockBiomeArray;

    @Environment(EnvType.CLIENT)
    @Shadow
    public abstract void generateHeightMap();

    @Shadow public Map chunkTileEntityMap;

    @Shadow public abstract TileEntity getChunkBlockTileEntity(int par1, int par2, int par3);

    @Shadow public boolean isModified;

    @Shadow protected abstract void propagateSkylightOcclusion(int par1, int par2);

    @Shadow protected abstract void relightBlock(int par1, int par2, int par3);

    @Shadow public abstract void generateSkylightMap();

    @Shadow @Final public int xPosition;

    @Shadow @Final public int zPosition;

    @Shadow public abstract int getBlockMetadata(int par1, int par2, int par3);

    @Shadow public abstract int getBlockID(int par1, int par2, int par3);

    @Shadow public int[] heightMap;

    @Shadow public int[] precipitationHeightMap;

    private int CopyArrayByte2Long(byte[] data, int dataPos, long[] dest, int destPos, int length) {
        ByteBuffer.wrap(data, dataPos, length * 8).asLongBuffer().get(dest);
        return dataPos + length * 8;
    }

    @Inject(method = "setBlockIDWithMetadata", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ExtendedBlockStorage;setExtBlockMetadata(IIII)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void setBlockExtraMetadata(int par1, int par2, int par3, int par4, int par5, CallbackInfoReturnable<Boolean> cir, int var6, int var7, int var8, int var9, ExtendedBlockStorage var10) {
        ((ExtendedBlockStorageExtension) var10).setExtBlockExtraMetadata(par1, par2 & 15, par3, 0);
    }

    /**
     * Sets a blockID of a position within a chunk with metadata and extra metadata. Args: x, y, z, blockID, metadata, extra metadata
     */
    public boolean setBlockIDWithMetadataAndExtraMetadata(int par1, int par2, int par3, int par4, int par5, int extraMeta)
    {
        int var6 = par3 << 4 | par1;

        if (par2 >= this.precipitationHeightMap[var6] - 1)
        {
            this.precipitationHeightMap[var6] = -999;
        }

        int var7 = this.heightMap[var6];
        int var8 = this.getBlockID(par1, par2, par3);
        int var9 = this.getBlockMetadata(par1, par2, par3);
        int oldExtraMeta = this.getBlockExtraMetadata(par1, par2, par3);

        if (var8 == par4 && var9 == par5 && oldExtraMeta == extraMeta)
        {
            return false;
        }
        else
        {
            ExtendedBlockStorage var10 = this.storageArrays[par2 >> 4];
            boolean var11 = false;

            if (var10 == null)
            {
                if (par4 == 0)
                {
                    return false;
                }

                var10 = this.storageArrays[par2 >> 4] = new ExtendedBlockStorage(par2 >> 4 << 4, !this.worldObj.provider.hasNoSky);
                var11 = par2 >= var7;
            }

            int var12 = this.xPosition * 16 + par1;
            int var13 = this.zPosition * 16 + par3;

            if (var8 != 0 && !this.worldObj.isRemote)
            {
                Block.blocksList[var8].onSetBlockIDWithMetaData(this.worldObj, var12, par2, var13, var9);
            }

            var10.setExtBlockID(par1, par2 & 15, par3, par4);

            if (var8 != 0)
            {
                if (!this.worldObj.isRemote)
                {
                    Block.blocksList[var8].breakBlock(this.worldObj, var12, par2, var13, var8, var9);
                }
                else if ( var8 != par4 )
                {
                    Block.blocksList[var8].clientBreakBlock(this.worldObj, var12, par2, var13, var8, var9);

                    if ( Block.blocksList[var8] instanceof ITileEntityProvider && Block.blocksList[var8].shouldDeleteTileEntityOnBlockChange( par4 ) )
                    {
                        this.worldObj.removeBlockTileEntity(var12, par2, var13);
                    }
                }
            }

            if (var10.getExtBlockID(par1, par2 & 15, par3) != par4)
            {
                return false;
            }
            else
            {
                var10.setExtBlockMetadata(par1, par2 & 15, par3, par5);
                //EDIT
                ((ExtendedBlockStorageExtension) var10).setExtBlockExtraMetadata(par1, par2 & 15, par3, extraMeta);

                if (var11)
                {
                    this.generateSkylightMap();
                }
                else
                {
                    if (Block.lightOpacity[par4 & 4095] > 0)
                    {
                        if (par2 >= var7)
                        {
                            this.relightBlock(par1, par2 + 1, par3);
                        }
                    }
                    else if (par2 == var7 - 1)
                    {
                        this.relightBlock(par1, par2, par3);
                    }

                    this.propagateSkylightOcclusion(par1, par3);
                }

                TileEntity var14;

                if (par4 != 0)
                {
                    if (!this.worldObj.isRemote)
                    {
                        Block.blocksList[par4].onBlockAdded(this.worldObj, var12, par2, var13);
                    }
                    // FCMOD: Code added
                    else if ( var8 != par4 )
                    {
                        Block.blocksList[par4].clientBlockAdded(worldObj, var12, par2, var13);
                    }
                    // END FCMOD

                    if (Block.blocksList[par4] instanceof ITileEntityProvider)
                    {
                        var14 = this.getChunkBlockTileEntity(par1, par2, par3);

                        if (var14 == null)
                        {
                            var14 = ((ITileEntityProvider)Block.blocksList[par4]).createNewTileEntity(this.worldObj);
                            this.worldObj.setBlockTileEntity(var12, par2, var13, var14);
                        }

                        if (var14 != null)
                        {
                            var14.updateContainingBlockInfo();
                        }
                    }
                }
                else if (var8 > 0 && Block.blocksList[var8] instanceof ITileEntityProvider)
                {
                    var14 = this.getChunkBlockTileEntity(par1, par2, par3);

                    if (var14 != null)
                    {
                        var14.updateContainingBlockInfo();
                    }
                }

                this.isModified = true;
                return true;
            }
        }
    }

    /**
     * Set the extra metadata of a block in the chunk
     */
    public boolean setBlockExtraMetadata(int par1, int par2, int par3, int par4)
    {
        ExtendedBlockStorage var5 = this.storageArrays[par2 >> 4];

        if (var5 == null)
        {
            return false;
        }
        else
        {
            int var6 = ((ExtendedBlockStorageExtension) var5).getExtBlockExtraMetadata(par1, par2 & 15, par3);

            if (var6 == par4)
            {
                return false;
            }
            else
            {
                this.isModified = true;
                ((ExtendedBlockStorageExtension) var5).setExtBlockExtraMetadata(par1, par2 & 15, par3, par4);
                int var7 = var5.getExtBlockID(par1, par2 & 15, par3);

                if (var7 > 0 && Block.blocksList[var7] instanceof ITileEntityProvider)
                {
                    TileEntity var8 = this.getChunkBlockTileEntity(par1, par2, par3);

                    if (var8 != null)
                    {
                        var8.updateContainingBlockInfo();
                        ((TileEntityExtension) var8).setBlockExtraMetadata(par4);
                    }
                }

                return true;
            }
        }
    }

    /**
     * Return the extra metadata corresponding to the given coordinates inside a chunk.
     */
    public int getBlockExtraMetadata(int par1, int par2, int par3)
    {
        if (par2 >> 4 >= this.storageArrays.length)
        {
            return 0;
        }
        else
        {
            ExtendedBlockStorageExtension var4 = (ExtendedBlockStorageExtension) this.storageArrays[par2 >> 4];
            return var4 != null ? var4.getExtBlockExtraMetadata(par1, par2 & 15, par3) : 0;
        }
    }

    /**
     * @author Arminias
     */
    @Overwrite
    @Environment(EnvType.CLIENT)
    public void fillChunk(byte[] par1ArrayOfByte, int par2, int par3, boolean par4)
    {
        int var5 = 0;
        boolean var6 = !this.worldObj.provider.hasNoSky;
        int var7;

        for (var7 = 0; var7 < this.storageArrays.length; ++var7)
        {
            if ((par2 & 1 << var7) != 0)
            {
                if (this.storageArrays[var7] == null)
                {
                    this.storageArrays[var7] = new ExtendedBlockStorage(var7 << 4, var6);
                }

                byte[] var8 = this.storageArrays[var7].getBlockLSBArray();
                System.arraycopy(par1ArrayOfByte, var5, var8, 0, var8.length);
                var5 += var8.length;
            }
            else if (par4 && this.storageArrays[var7] != null)
            {
                this.storageArrays[var7] = null;
            }
        }

        NibbleArray var9;

        for (var7 = 0; var7 < this.storageArrays.length; ++var7)
        {
            if ((par2 & 1 << var7) != 0 && this.storageArrays[var7] != null)
            {
                var9 = this.storageArrays[var7].getMetadataArray();
                System.arraycopy(par1ArrayOfByte, var5, var9.data, 0, var9.data.length);
                var5 += var9.data.length;
            }
        }

        //EDIT
        HunkArray var9_2;
        for (var7 = 0; var7 < this.storageArrays.length; ++var7)
        {
            if ((par2 & 1 << var7) != 0 && this.storageArrays[var7] != null)
            {
                var9_2 = ((ExtendedBlockStorageExtension) this.storageArrays[var7]).getExtraMetadataArray();
                var5 = CopyArrayByte2Long(par1ArrayOfByte, var5, var9_2.data, 0, var9_2.data.length);
            }
        }

        for (var7 = 0; var7 < this.storageArrays.length; ++var7)
        {
            if ((par2 & 1 << var7) != 0 && this.storageArrays[var7] != null)
            {
                var9 = this.storageArrays[var7].getBlocklightArray();
                System.arraycopy(par1ArrayOfByte, var5, var9.data, 0, var9.data.length);
                var5 += var9.data.length;
            }
        }

        if (var6)
        {
            for (var7 = 0; var7 < this.storageArrays.length; ++var7)
            {
                if ((par2 & 1 << var7) != 0 && this.storageArrays[var7] != null)
                {
                    var9 = this.storageArrays[var7].getSkylightArray();
                    System.arraycopy(par1ArrayOfByte, var5, var9.data, 0, var9.data.length);
                    var5 += var9.data.length;
                }
            }
        }

        for (var7 = 0; var7 < this.storageArrays.length; ++var7)
        {
            if ((par3 & 1 << var7) != 0)
            {
                if (this.storageArrays[var7] == null)
                {
                    var5 += 2048;
                }
                else
                {
                    var9 = this.storageArrays[var7].getBlockMSBArray();

                    if (var9 == null)
                    {
                        var9 = this.storageArrays[var7].createBlockMSBArray();
                    }

                    System.arraycopy(par1ArrayOfByte, var5, var9.data, 0, var9.data.length);
                    var5 += var9.data.length;
                }
            }
            else if (par4 && this.storageArrays[var7] != null && this.storageArrays[var7].getBlockMSBArray() != null)
            {
                this.storageArrays[var7].clearMSBArray();
            }
        }

        if (par4)
        {
            System.arraycopy(par1ArrayOfByte, var5, this.blockBiomeArray, 0, this.blockBiomeArray.length);
        }

        for (var7 = 0; var7 < this.storageArrays.length; ++var7)
        {
            if (this.storageArrays[var7] != null && (par2 & 1 << var7) != 0)
            {
                this.storageArrays[var7].removeInvalidBlocks();
            }
        }

        this.generateHeightMap();

        for (Object o : this.chunkTileEntityMap.values()) {
            TileEntity var11 = (TileEntity) o;
            var11.updateContainingBlockInfo();
        }
    }
}
