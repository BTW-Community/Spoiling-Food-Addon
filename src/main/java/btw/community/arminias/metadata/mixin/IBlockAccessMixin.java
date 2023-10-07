package btw.community.arminias.metadata.mixin;

import btw.community.arminias.metadata.extension.IBlockAccessExtension;
import net.minecraft.src.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IBlockAccess.class)
public interface IBlockAccessMixin extends IBlockAccessExtension {
}
