package btw.community.arminias.foodspoil.mixin.plants;

import btw.community.arminias.metadata.extension.WorldExtension;
import api.item.items.SeedFoodItem;
import api.item.items.SeedItem;
import api.world.BlockPos;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SeedFoodItem.class)
public class SeedFoodItemMixin {

    @Inject(method = "onItemUse", at = @At("RETURN"))
    private void onItemUse(ItemStack itemStack, EntityPlayer player, World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            WorldExtension.cast(world).setBlockExtraMetadata(i, j + 1, k, -1);
        }
    }

    @Inject(method = "onItemUsedByBlockDispenser", at = @At("RETURN"))
    private void onItemUsedByBlockDispenserInject(ItemStack stack, World world, int i, int j, int k, int iFacing, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            BlockPos targetPos = new BlockPos( i, j, k, iFacing );
            WorldExtension.cast(world).setBlockExtraMetadata(targetPos.x, targetPos.y, targetPos.z, -1);
        }
    }
}
