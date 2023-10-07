package btw.community.arminias.metadata.mixin;

import btw.community.arminias.metadata.extension.Packet53BlockChangeExtension;
import btw.community.arminias.metadata.extension.WorldExtension;
import net.minecraft.src.Packet53BlockChange;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Mixin(Packet53BlockChange.class)
public class Packet53BlockChangeMixin implements Packet53BlockChangeExtension {
    /** Extra metadata of the block. */
    public int extraMetadata;

    @Override
    public int getExtraMetadata() {
        return this.extraMetadata;
    }

    @Inject(method = "<init>(IIILnet/minecraft/src/World;)V", at = @At("RETURN"))
    private void initInject(int par1, int par2, int par3, World par4World, CallbackInfo ci) {
        this.extraMetadata = ((WorldExtension) par4World).getBlockExtraMetadata(par1, par2, par3);;
    }

    @Inject(method = "readPacketData", at = @At("RETURN"))
    private void readPacketDataInject(DataInputStream par1DataInputStream, CallbackInfo ci) {
        try {
            this.extraMetadata = par1DataInputStream.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Inject(method = "writePacketData", at = @At("RETURN"))
    private void writePacketDataInject(DataOutputStream par1DataOutputStream, CallbackInfo ci) {
        try {
            par1DataOutputStream.writeInt(this.extraMetadata);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @ModifyConstant(method = "getPacketSize", constant = @Constant())
    private int getPacketSizeInject(int par0) {
        return par0 + 4;
    }
}
