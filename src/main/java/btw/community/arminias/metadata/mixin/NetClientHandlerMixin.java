package btw.community.arminias.metadata.mixin;

import btw.community.arminias.metadata.extension.Packet53BlockChangeExtension;
import btw.community.arminias.metadata.extension.WorldClientExtension;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.DataInputStream;
import java.io.IOException;

@Mixin(NetClientHandler.class)
public class NetClientHandlerMixin {
    @Shadow private WorldClient worldClient;

    @Inject(method = "handleMultiBlockChange", at = @At(value = "CONSTANT", args = "intValue=255", ordinal = 0, shift = At.Shift.BY, by = 3), locals = LocalCapture.CAPTURE_FAILSOFT, require = 0)
    private void handleMultiBlockChangeInject(Packet52MultiBlockChange par1Packet52MultiBlockChange, CallbackInfo ci, int var2, int var3, DataInputStream var4, int var5, short var6, short var7, int var8, int var9, int var10, int var11, int var12) {
        try {
            int extraMeta = var4.readInt();
            ((WorldClientExtension) this.worldClient).setBlockAndMetadataAndExtraMetadataAndInvalidate(var10 + var2, var12, var11 + var3, var8, var9, extraMeta);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Inject(method = "handleMultiBlockChange", at = @At(value = "CONSTANT", args = "intValue=255", ordinal = 0, shift = At.Shift.BY, by = 3), locals = LocalCapture.CAPTURE_FAILSOFT, require = 0)
    private void handleMultiBlockChangeInject2(Packet52MultiBlockChange par1Packet52MultiBlockChange, CallbackInfo ci, int var2, int var3, DataInputStream var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) {
        try {
            int extraMeta = var4.readInt();
            ((WorldClientExtension) this.worldClient).setBlockAndMetadataAndExtraMetadataAndInvalidate(var10 + var2, var12, var11 + var3, var8, var9, extraMeta);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Redirect(method = "handleMultiBlockChange", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldClient;setBlockAndMetadataAndInvalidate(IIIII)Z"))
    private boolean handleMultiBlockChangeRedirect(WorldClient worldClient, int par1, int par2, int par3, int par4, int par5) {
        return false;
    }

    @Redirect(method = "handleBlockChange", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldClient;setBlockAndMetadataAndInvalidate(IIIII)Z"))
    private boolean handleBlockChangeRedirect(WorldClient worldClient, int par1, int par2, int par3, int par4, int par5, Packet53BlockChange par1Packet53BlockChange) {
        return ((WorldClientExtension) this.worldClient).setBlockAndMetadataAndExtraMetadataAndInvalidate(par1Packet53BlockChange.xPosition, par1Packet53BlockChange.yPosition, par1Packet53BlockChange.zPosition, par1Packet53BlockChange.type, par1Packet53BlockChange.metadata,
                ((Packet53BlockChangeExtension) par1Packet53BlockChange).getExtraMetadata());
    }

}
