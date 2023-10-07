package btw.community.arminias.metadata.mixin;

import btw.community.arminias.metadata.extension.TileEntityExtension;
import btw.community.arminias.metadata.extension.WorldExtension;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntity.class)
public class TileEntityMixin implements TileEntityExtension {
    @Shadow public World worldObj;
    @Shadow public int xCoord;
    @Shadow public int yCoord;
    @Shadow public int zCoord;
    public int blockExtraMetadata = -1;

    @Inject(method = "onInventoryChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;updateTileEntityChunkAndDoNothing(IIILnet/minecraft/src/TileEntity;)V"))
    private void onInventoryChanged(CallbackInfo ci) {
        this.blockExtraMetadata = ((WorldExtension) worldObj).getBlockExtraMetadata(this.xCoord, this.yCoord, this.zCoord);
    }

    @Inject(method = "updateContainingBlockInfo", at = @At("RETURN"))
    private void updateContainingBlockInfo(CallbackInfo ci) {
        this.blockExtraMetadata = -1;
    }

    @Override
    public void setBlockExtraMetadata(int par1) {
        blockExtraMetadata = par1;
    }

    /**
     * Returns extra block data at the location of this entity (client-only).
     */
    @Override
    public int getBlockExtraMetadata()
    {
        if (this.blockExtraMetadata == -1)
        {
            this.blockExtraMetadata = ((WorldExtension) worldObj).getBlockExtraMetadata(this.xCoord, this.yCoord, this.zCoord);
        }

        return this.blockExtraMetadata;
    }
}
