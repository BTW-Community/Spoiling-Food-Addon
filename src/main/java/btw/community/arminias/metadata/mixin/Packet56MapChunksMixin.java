package btw.community.arminias.metadata.mixin;

import net.minecraft.src.Packet56MapChunks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

@Mixin(Packet56MapChunks.class)
public class Packet56MapChunksMixin {
    @Shadow private int dataLength;

    @Shadow private boolean skyLightSent;

    @Shadow private int[] chunkPostX;

    @Shadow private int[] chunkPosZ;

    @Shadow public int[] field_73590_a;

    @Shadow public int[] field_73588_b;

    @Shadow private byte[][] field_73584_f;

    @Shadow private static byte[] chunkDataNotCompressed;

    /*@ModifyConstant(method = "readPacketData", constant = @Constant(intValue = 196864))
    private static int readPacketDataInject(int constant) {
        return constant * 2;
    }*/

    /*@Group(name = "readPacketDataInject2", min = 1, max = 1)
    @ModifyConstant(method = "readPacketData", constant = @Constant(intValue = 8192, ordinal = 0))
    private static int readPacketDataInject2(int constant) {
        return constant + 8 * 2048;
    }

    @Group(name = "readPacketDataInject2", min = 1, max = 1)
    @ModifyConstant(method = "readPacketData", constant = @Constant(intValue = 2048, ordinal = 0),
            slice = @Slice(to = @At(value = "CONSTANT", args = "intValue=256", ordinal = 0)))
    private static int readPacketDataInject22(int constant) {
        return constant + 8 * 2048;
    }*/

    /**
     * @author Arminias
     */
    @Overwrite
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        short var2 = par1DataInputStream.readShort();
        this.dataLength = par1DataInputStream.readInt();
        this.skyLightSent = par1DataInputStream.readBoolean();
        this.chunkPostX = new int[var2];
        this.chunkPosZ = new int[var2];
        this.field_73590_a = new int[var2];
        this.field_73588_b = new int[var2];
        this.field_73584_f = new byte[var2][];

        if (chunkDataNotCompressed.length < this.dataLength)
        {
            chunkDataNotCompressed = new byte[this.dataLength];
        }

        par1DataInputStream.readFully(chunkDataNotCompressed, 0, this.dataLength);
        byte[] var3 = new byte[196864 * 2 * var2];
        Inflater var4 = new Inflater();
        var4.setInput(chunkDataNotCompressed, 0, this.dataLength);

        try
        {
            var4.inflate(var3);
        }
        catch (DataFormatException var12)
        {
            throw new IOException("Bad compressed data format");
        }
        finally
        {
            var4.end();
        }

        int var5 = 0;

        for (int var6 = 0; var6 < var2; ++var6)
        {
            this.chunkPostX[var6] = par1DataInputStream.readInt();
            this.chunkPosZ[var6] = par1DataInputStream.readInt();
            this.field_73590_a[var6] = par1DataInputStream.readShort();
            this.field_73588_b[var6] = par1DataInputStream.readShort();
            int var7 = 0;
            int var8 = 0;
            int var9;

            for (var9 = 0; var9 < 16; ++var9)
            {
                var7 += this.field_73590_a[var6] >> var9 & 1;
                var8 += this.field_73588_b[var6] >> var9 & 1;
            }

            var9 = (2048 * 4 + 8 * 2048) * var7 + 256;
            var9 += 2048 * var8;

            if (this.skyLightSent)
            {
                var9 += 2048 * var7;
            }

            this.field_73584_f[var6] = new byte[var9];
            System.arraycopy(var3, var5, this.field_73584_f[var6], 0, var9);
            var5 += var9;
        }
    }
}
