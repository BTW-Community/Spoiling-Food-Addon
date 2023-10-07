package btw.community.arminias.foodspoil.mixin;

import btw.community.arminias.foodspoil.Utils;
import btw.inventory.util.InventoryUtils;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryUtils.class)
public class InventoryUtilsMixin {

    @Inject(method = "canStacksMerge", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemStack;areItemStackTagsEqual(Lnet/minecraft/src/ItemStack;Lnet/minecraft/src/ItemStack;)Z", shift = At.Shift.BEFORE), cancellable = true)
    private static void mergeDecayTimers(ItemStack sourceStack, ItemStack destStack, int iInventoryStackSizeLimit, CallbackInfoReturnable<Boolean> cir) {
        if (sourceStack.hasTagCompound() && destStack.hasTagCompound() && destStack.stackTagCompound.hasKey("spoilDate") && sourceStack.stackTagCompound.hasKey("spoilDate")) {
            Utils.mergeDecayNBTs(sourceStack, destStack);
            cir.setReturnValue(true);
        }
    }
}
