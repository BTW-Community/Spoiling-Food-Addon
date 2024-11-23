package btw.community.arminias.foodspoil.mixin.plants;

import btw.block.blocks.PlantsBlock;
import btw.community.arminias.metadata.extension.WorldExtension;
import net.minecraft.src.ChatMessageComponent;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlantsBlock.class)
public class PlantsBlockMixin {
    // TODO Remove
    /*@Inject(method = "onBlockActivated", at = @At("HEAD"))
    public void onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick, CallbackInfoReturnable<Boolean> cir) {
        //System.out.println(WorldExtension.cast(world).getBlockExtraMetadata(i, j, k));
        player.sendChatToPlayer(ChatMessageComponent.createFromText("Metadata: " + WorldExtension.cast(world).getBlockExtraMetadata(i, j, k)));
    }*/
}
