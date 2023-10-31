package btw.community.arminias.foodspoil.mixin;

import btw.block.tileentity.CauldronTileEntity;
import btw.block.tileentity.CookingVesselTileEntity;
import btw.community.arminias.foodspoil.Utils;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CauldronTileEntity.class)
public abstract class CauldronTileEntityMixin extends CookingVesselTileEntity {
    @Inject(method = "attemptToCookFood", at = @At(value = "INVOKE", target = "Lbtw/block/tileentity/CauldronTileEntity;decrStackSize(II)Lnet/minecraft/src/ItemStack;"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void inheritFoodDecay(CallbackInfoReturnable<Boolean> cir, int i, ItemStack tempStack, ItemStack cookedItem) {
        if (cookedItem != null) {
            Utils.inheritFoodDecay(contents[i], cookedItem, this.worldObj.getTotalWorldTime());
        }
    }
}
