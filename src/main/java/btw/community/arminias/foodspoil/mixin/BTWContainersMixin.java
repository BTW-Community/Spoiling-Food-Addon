package btw.community.arminias.foodspoil.mixin;

import btw.community.arminias.foodspoil.ContainerFreezer;
import btw.community.arminias.foodspoil.ContainerFreezerGui;
import btw.community.arminias.foodspoil.TileEntityFreezer;
import btw.inventory.BTWContainers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.GuiContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BTWContainers.class)
public class BTWContainersMixin {

    @Environment(EnvType.CLIENT)
    @Inject(at = @At("HEAD"), method = "getAssociatedGui", cancellable = true)
    private static void getAssociatedGui(EntityClientPlayerMP entityclientplayermp, int iContainerID, CallbackInfoReturnable<GuiContainer> cir) {
        if (iContainerID == ContainerFreezer.ID) {
            cir.setReturnValue(new ContainerFreezerGui(entityclientplayermp.inventory,  new TileEntityFreezer()));
        }
    }
}
