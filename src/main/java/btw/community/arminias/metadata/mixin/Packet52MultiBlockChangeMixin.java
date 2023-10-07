package btw.community.arminias.metadata.mixin;

import btw.community.arminias.metadata.extension.ChunkExtension;
import net.minecraft.src.Chunk;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet52MultiBlockChange;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Mixin(Packet52MultiBlockChange.class)
public abstract class Packet52MultiBlockChangeMixin extends Packet {
    @Shadow public int size;

    @Shadow public int xPosition;

    @Shadow public int zPosition;

    @Shadow private static byte[] field_73449_e;

    @Shadow public byte[] metadataArray;

    /*@Overwrite(aliases = {"<init>"}, remap = false)
    private void initInject(int par1, int par2, short[] par3ArrayOfShort, int par4, World par5World, CallbackInfo ci) {
        this.isChunkDataPacket = true;
        this.xPosition = par1;
        this.zPosition = par2;
        this.size = par4;
        int var6 = 8 * par4;
        Chunk var7 = par5World.getChunkFromChunkCoords(par1, par2);

        try
        {
            if (par4 >= 64)
            {
                this.field_98193_m.logInfo("ChunkTilesUpdatePacket compress " + par4);

                if (field_73449_e.length < var6)
                {
                    field_73449_e = new byte[var6];
                }
            }
            else
            {
                ByteArrayOutputStream var8 = new ByteArrayOutputStream(var6);
                DataOutputStream var9 = new DataOutputStream(var8);

                for (int var10 = 0; var10 < par4; ++var10)
                {
                    int var11 = par3ArrayOfShort[var10] >> 12 & 15;
                    int var12 = par3ArrayOfShort[var10] >> 8 & 15;
                    int var13 = par3ArrayOfShort[var10] & 255;
                    var9.writeShort(par3ArrayOfShort[var10]);
                    var9.writeShort((short)((var7.getBlockID(var11, var13, var12) & 4095) << 4 | var7.getBlockMetadata(var11, var13, var12) & 15));
                    var9.writeInt(((ChunkExtension) var7).getBlockExtraMetadata(var11, var13, var12));
                }

                this.metadataArray = var8.toByteArray();

                if (this.metadataArray.length != var6)
                {
                    throw new RuntimeException("Expected length " + var6 + " doesn\'t match received length " + this.metadataArray.length);
                }
            }
        }
        catch (IOException var14)
        {
            this.field_98193_m.logSevereException("Couldn\'t create chunk packet", var14);
            this.metadataArray = null;
        }
        ci.cancel();
    }*/

    /*@ModifyConstant(method = "<init>(II[SILnet/minecraft/src/World;)V", constant = @Constant(intValue = 4, ordinal = 0))
    private static int initInject(int par1) {
        return 8;
    }

    @Inject(method = "<init>(II[SILnet/minecraft/src/World;)V", at = @At(value = "INVOKE", target = "Ljava/io/DataOutputStream;writeShort(I)V", ordinal = 1, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void initInject(int par1, int par2, short[] par3ArrayOfShort, int par4, World par5World, CallbackInfo ci, int var6, Chunk var7, ByteArrayOutputStream var8, DataOutputStream var9, int var10, int var11, int var12, int var13) {
        try {
            var9.writeInt(((ChunkExtension) var7).getBlockExtraMetadata(var11, var13, var12));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/

    @Inject(method = "getPacketSize", at = @At("RETURN"), cancellable = true)
    private void getPacketSizeInject(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cir.getReturnValue() + size * 4);
    }
}
