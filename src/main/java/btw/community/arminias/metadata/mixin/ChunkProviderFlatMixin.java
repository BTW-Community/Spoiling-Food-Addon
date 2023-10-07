package btw.community.arminias.metadata.mixin;

import btw.community.arminias.metadata.extension.ExtendedBlockStorageExtension;
import net.minecraft.src.Chunk;
import net.minecraft.src.ChunkProviderFlat;
import net.minecraft.src.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ChunkProviderFlat.class)
public class ChunkProviderFlatMixin {
    @Inject(method = "provideChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ExtendedBlockStorage;setExtBlockMetadata(IIII)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void provideChunk(int par1, int par2, CallbackInfoReturnable<Chunk> cir, Chunk var3, int var4, int var5, ExtendedBlockStorage var6, int var7, int var8) {
        ((ExtendedBlockStorageExtension) var6).setExtBlockExtraMetadata(var7, var4 & 15, var8, 0);
    }
}
