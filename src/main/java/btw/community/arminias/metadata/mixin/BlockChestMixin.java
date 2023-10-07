package btw.community.arminias.metadata.mixin;

import btw.community.arminias.metadata.extension.WorldExtension;
import net.minecraft.src.BlockChest;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockChest.class)
public class BlockChestMixin {

    // TODO Remove
    /*@Inject(method = "onBlockActivated", at = @org.spongepowered.asm.mixin.injection.At(value = "HEAD"))
    private void onBlockActivatedInject(World world, int x, int y, int z, net.minecraft.src.EntityPlayer player, int side, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        System.out.println(((WorldExtension) world).getBlockExtraMetadata(x, y, z));
    }*/

}
