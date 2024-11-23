package btw.community.arminias.foodspoil.mixin.plants;

import btw.community.arminias.metadata.extension.WorldExtension;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockGlass.class)
public class BlockGlassMixin extends Block {
    protected BlockGlassMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }

    /*@Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick) {
        System.out.println(WorldExtension.cast(world).getBlockExtraMetadata(i, j, k));
        return super.onBlockActivated(world, i, j, k, player, iFacing, fXClick, fYClick, fZClick);
    }*/
}
