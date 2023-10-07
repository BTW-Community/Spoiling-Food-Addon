package btw.community.arminias.metadata.mixin;

import btw.community.arminias.metadata.extension.TileEntityExtension;
import btw.community.arminias.metadata.extension.TileEntityPistonExtension;
import btw.community.arminias.metadata.extension.WorldExtension;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityPiston;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityPiston.class)
public class TileEntityPistonMixin extends TileEntity implements TileEntityPistonExtension {
    @Shadow public TileEntity cachedTileEntity;
    private int storedExtraMetadata;

    @Override
    public int getBlockExtraMetadata() {
        return storedExtraMetadata;
    }

    @Override
    public TileEntity setExtraMetadata(int extraMeta) {
        storedExtraMetadata = extraMeta;
        return this;
    }

    @Inject(method = "storeCachedTileEntity", at = @At("RETURN"))
    private void storeCachedTileEntity(CallbackInfo ci) {
        ((TileEntityExtension) cachedTileEntity).setBlockExtraMetadata(this.storedExtraMetadata);
    }

    @Redirect(method = "clearPistonTileEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;setBlock(IIIIII)Z"))
    private boolean clearPistonTileEntity(World world, int i, int j, int k, int i1, int i2, int i3) {
        return ((WorldExtension) world).setBlockWithExtra(i, j, k, i1, i2, i3, this.storedExtraMetadata);
    }

    @Redirect(method = "restoreStoredBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;setBlock(IIIIII)Z"))
    private boolean restoreStoredBlock(World world, int i, int j, int k, int i1, int i2, int i3) {
        return ((WorldExtension) world).setBlockWithExtra(i, j, k, i1, i2, i3, this.storedExtraMetadata);
    }

    @Inject(method = "readFromNBT", at = @At("RETURN"))
    private void readFromNBT(NBTTagCompound par1NBTTagCompound, CallbackInfo ci) {
        this.storedExtraMetadata = par1NBTTagCompound.getInteger("blockExtraData");
    }

    @Inject(method = "writeToNBT", at = @At("RETURN"))
    private void writeToNBT(NBTTagCompound par1NBTTagCompound, CallbackInfo ci) {
        par1NBTTagCompound.setInteger("blockExtraData", this.storedExtraMetadata);
    }

    // createPackedBlockOfTypeAtLocation is left alone because it does not use the extra metadata

}
