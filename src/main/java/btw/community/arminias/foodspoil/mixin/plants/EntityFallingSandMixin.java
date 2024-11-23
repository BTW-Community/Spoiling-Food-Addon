package btw.community.arminias.foodspoil.mixin.plants;


import btw.community.arminias.foodspoil.FallingBlockExtension;
import btw.community.arminias.metadata.extension.WorldExtension;
import net.minecraft.src.EntityFallingSand;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityFallingSand.class)
public class EntityFallingSandMixin implements FallingBlockExtension {
    @Unique
    private int extra;

    @Override
    public void foodspoilmod$setExtraMeta(int extraMeta) {
        this.extra = extraMeta;
    }

    @Override
    public long foodspoilmod$getExtraMeta() {
        return this.extra;
    }

    @Inject(method = "readEntityFromNBT", at = @At("RETURN"))
    private void readEntityFromNBTMixin(NBTTagCompound par1NBTTagCompound, CallbackInfo ci) {
        this.extra = par1NBTTagCompound.getInteger("ExtraMeta");
    }

    @Inject(method = "writeEntityToNBT", at = @At("RETURN"))
    private void writeEntityToNBTMixin(NBTTagCompound par1NBTTagCompound, CallbackInfo ci) {
        par1NBTTagCompound.setInteger("ExtraMeta", this.extra);
    }

    @Redirect(method = "attemptToReplaceBlockAtPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;setBlock(IIIIII)Z"))
    private boolean attemptToReplaceBlockAtPositionMixin(World world, int x, int y, int z, int blockId, int meta, int notify) {
        return WorldExtension.cast(world).setBlockAndMetadataAndExtraMetadataWithNotify(x, y, z, blockId, meta, this.extra);
    }
}
