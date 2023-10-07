package btw.community.arminias.foodspoil.mixin;

import btw.community.arminias.foodspoil.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    public NBTTagCompound stackTagCompound;

    @Environment(value = EnvType.CLIENT)
    @Inject(method = "<init>(III)V", at = @At("RETURN"))
    private void addDecayTime(int par1, int par2, int par3, CallbackInfo ci) {
        if (par2 > 0 && Minecraft.getMinecraft() != null && Minecraft.getMinecraft().theWorld != null) {
            Utils.initFoodItemClient((ItemStack) (Object) this, par1);
        }
    }

    @Environment(value = EnvType.SERVER)
    @Inject(method = "<init>(III)V", at = @At("RETURN"))
    private void addDecayTimeServer(int par1, int par2, int par3, CallbackInfo ci) {
        if (par2 > 0 && MinecraftServer.getServer() != null && MinecraftServer.getServer().worldServers != null && MinecraftServer.getServer().worldServers[0] != null) {
            Utils.initFoodItemServer((ItemStack) (Object) this, par1);
        }
    }

    @Inject(method = "copy", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void copyDecayTime(CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack) {
        if (this.stackTagCompound == null) {
            itemStack.stackTagCompound = null;
        }
    }

    @Shadow
    public abstract void setTagCompound(NBTTagCompound nbtTagCompound);


}
