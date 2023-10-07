package btw.community.arminias.metadata.mixin;

import btw.community.arminias.metadata.HunkArray;
import btw.community.arminias.metadata.extension.ExtendedBlockStorageExtension;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;

@Mixin(Packet51MapChunk.class)
public class Packet51MapChunkMixin {
    @Shadow private static byte[] temp;

    private static int copyArrayLong2Byte(long[] data, int dataPos, byte[] dest, int destPos, int length) {
        ByteBuffer.wrap(dest, destPos, length * 8).asLongBuffer().put(data);
        return destPos + length * 8;
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void initInject(CallbackInfo ci) {
        temp = new byte[196864 * 2];
    }

    @ModifyConstant(method = "readPacketData", constant = @Constant(intValue = 12288))
    private static int readPacketDataInject(int constant) {
        return constant + 2048 * 8;
    }

    /**
     * @author Arminias
     */
    @Overwrite
    public static Packet51MapChunkData getMapChunkData(Chunk par0Chunk, boolean par1, int par2)
    {
        int var3 = 0;
        ExtendedBlockStorage[] var4 = par0Chunk.getBlockStorageArray();
        int var5 = 0;
        Packet51MapChunkData var6 = new Packet51MapChunkData();
        byte[] var7 = temp;

        if (par1)
        {
            par0Chunk.sendUpdates = true;
        }

        int var8;

        for (var8 = 0; var8 < var4.length; ++var8)
        {
            if (var4[var8] != null && (!par1 || !var4[var8].isEmpty()) && (par2 & 1 << var8) != 0)
            {
                var6.chunkExistFlag |= 1 << var8;

                if (var4[var8].getBlockMSBArray() != null)
                {
                    var6.chunkHasAddSectionFlag |= 1 << var8;
                    ++var5;
                }
            }
        }

        for (var8 = 0; var8 < var4.length; ++var8)
        {
            if (var4[var8] != null && (!par1 || !var4[var8].isEmpty()) && (par2 & 1 << var8) != 0)
            {
                byte[] var9 = var4[var8].getBlockLSBArray();
                System.arraycopy(var9, 0, var7, var3, var9.length);
                var3 += var9.length;
            }
        }

        NibbleArray var10;

        for (var8 = 0; var8 < var4.length; ++var8)
        {
            if (var4[var8] != null && (!par1 || !var4[var8].isEmpty()) && (par2 & 1 << var8) != 0)
            {
                var10 = var4[var8].getMetadataArray();
                System.arraycopy(var10.data, 0, var7, var3, var10.data.length);
                var3 += var10.data.length;
            }
        }
        //EDIT
        HunkArray var10_2;
        for (var8 = 0; var8 < var4.length; ++var8)
        {
            if (var4[var8] != null && (!par1 || !var4[var8].isEmpty()) && (par2 & 1 << var8) != 0)
            {
                var10_2 = ((ExtendedBlockStorageExtension) var4[var8]).getExtraMetadataArray();
                var3 = copyArrayLong2Byte(var10_2.data, 0, var7, var3, var10_2.data.length);
            }
        }

        for (var8 = 0; var8 < var4.length; ++var8)
        {
            if (var4[var8] != null && (!par1 || !var4[var8].isEmpty()) && (par2 & 1 << var8) != 0)
            {
                var10 = var4[var8].getBlocklightArray();
                System.arraycopy(var10.data, 0, var7, var3, var10.data.length);
                var3 += var10.data.length;
            }
        }

        if (!par0Chunk.worldObj.provider.hasNoSky)
        {
            for (var8 = 0; var8 < var4.length; ++var8)
            {
                if (var4[var8] != null && (!par1 || !var4[var8].isEmpty()) && (par2 & 1 << var8) != 0)
                {
                    var10 = var4[var8].getSkylightArray();
                    System.arraycopy(var10.data, 0, var7, var3, var10.data.length);
                    var3 += var10.data.length;
                }
            }
        }

        if (var5 > 0)
        {
            for (var8 = 0; var8 < var4.length; ++var8)
            {
                if (var4[var8] != null && (!par1 || !var4[var8].isEmpty()) && var4[var8].getBlockMSBArray() != null && (par2 & 1 << var8) != 0)
                {
                    var10 = var4[var8].getBlockMSBArray();
                    System.arraycopy(var10.data, 0, var7, var3, var10.data.length);
                    var3 += var10.data.length;
                }
            }
        }

        if (par1)
        {
            byte[] var11 = par0Chunk.getBiomeArray();
            System.arraycopy(var11, 0, var7, var3, var11.length);
            var3 += var11.length;
        }

        var6.compressedData = new byte[var3];
        System.arraycopy(var7, 0, var6.compressedData, 0, var3);
        return var6;
    }

}
