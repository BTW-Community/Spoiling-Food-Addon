package btw.community.arminias.foodspoil.mixin.plants;

import btw.community.arminias.foodspoil.FoodSpoilAddon;
import btw.community.arminias.foodspoil.Utils;
import btw.community.arminias.metadata.extension.WorldExtension;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class WorldMixin {

    @Shadow public abstract int getBlockId(int par1, int par2, int par3);

    @Inject(method = "setBlock(IIIIII)Z", at = @At("HEAD"), cancellable = true)
    private void setBlockToAir(int par1, int par2, int par3, int blockId, int meta, int notify, CallbackInfoReturnable<Boolean> cir) {
        if (FoodSpoilAddon.getSpoilingBlocks().contains(this.getBlockId(par1, par2, par3))) {
            cir.setReturnValue(WorldExtension.cast((World) (Object) this)
                    .setBlockWithExtra(par1, par2, par3, blockId, meta, notify,
                            WorldExtension.cast((World) (Object) this).getBlockExtraMetadata(par1, par2, par3)
            ));
        }
    }
}
