package btw.community.arminias.metadata.mixin;

import btw.community.arminias.metadata.extension.WorldExtension;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityChest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityChest.class)
public class TileEntityChestMixin extends TileEntity {
    // TODO Remove
    /*@Inject(method = "openChest", at = @At(value = "HEAD"))
    private void openChestInject(CallbackInfo ci) {
        WorldExtension.cast(worldObj).setBlockExtraMetadata(this.xCoord, this.yCoord, this.zCoord, WorldExtension.cast(worldObj).getBlockExtraMetadata(this.xCoord, this.yCoord, this.zCoord) - 1);
    }*/
}
