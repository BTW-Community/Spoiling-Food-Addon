package btw.community.arminias.foodspoil.mixin.plants;

import btw.community.arminias.foodspoil.FallingBlockExtension;
import btw.community.arminias.metadata.extension.WorldExtension;
import net.minecraft.src.Block;
import net.minecraft.src.EntityFallingSand;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Redirect(method = "checkForFall", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Block;onStartFalling(Lnet/minecraft/src/EntityFallingSand;)V"))
    private void checkForFall(Block instance, EntityFallingSand entity, World world, int i, int j, int k) {
        ((FallingBlockExtension) entity).foodspoilmod$setExtraMeta(WorldExtension.cast(world).getBlockExtraMetadata(i, j, k));
        instance.onStartFalling(entity);
    }

    @Redirect(method = "checkForFall", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;setBlock(IIII)Z"))
    private boolean checkForFall(World world, int i, int j, int k, int id) {
        return WorldExtension.cast(world).setBlockAndMetadataAndExtraMetadataWithNotify(i, j, k, id, world.getBlockMetadata(i, j, k), WorldExtension.cast(world).getBlockExtraMetadata(i, j, k));
    }
}
