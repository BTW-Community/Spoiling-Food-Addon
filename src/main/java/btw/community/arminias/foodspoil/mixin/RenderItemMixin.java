package btw.community.arminias.foodspoil.mixin;

import btw.community.arminias.foodspoil.FoodType;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderItem.class)
public abstract class RenderItemMixin {

    @Shadow protected abstract void renderQuad(Tessellator tessellator, int x, int y, int width, int height, int color);

    @Unique
    protected void renderQuad2(Tessellator tessellator, int x, int y, int width, int height, int color) {
        renderQuad(tessellator, x, y, height, width, color);
    }

    @Inject(method = "renderItemAndEffectIntoGUI", at = @At("TAIL"))
    public void renderItemAndEffectIntoGUI(FontRenderer par1FontRenderer, TextureManager par2TextureManager, ItemStack itemStack, int par4, int par5, CallbackInfo ci) {
        if (itemStack != null && itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("spoilDate")) { //&& !(Minecraft.getMinecraft().currentScreen instanceof ContainerFreezerGui)) {
            long spoilDate = itemStack.getTagCompound().getLong("spoilDate");
            long totalWorldTime = Minecraft.getMinecraft().theWorld.getTotalWorldTime();
            int spoilPercentage = (int)Math.round(255.0D * (spoilDate - totalWorldTime) / FoodType.getDecayTimeFast(itemStack.itemID));
            if (spoilPercentage <= 127) {
                int spoilPercentage2 = (int) Math.round(14D * (spoilDate - totalWorldTime) / FoodType.getDecayTimeFast(itemStack.itemID));
                spoilPercentage2 = Math.min(spoilPercentage2, 14);

                spoilPercentage = Math.max(spoilPercentage, 0);
                spoilPercentage2 = Math.max(spoilPercentage2, 0);

                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                Tessellator var9 = Tessellator.instance;
                int var10 = 255 - spoilPercentage << 16 | spoilPercentage << 8;
                int var11 = (255 - spoilPercentage) / 4 << 16 | 16128;
                this.renderQuad2(var9, par4 + 1, par5 + 2, 14, 2, 0);
                this.renderQuad2(var9, par4 + 1, par5 + 2, 13, 1, var10);
                this.renderQuad2(var9, par4 + 1, par5 + 2, 13 - spoilPercentage2, 1, var11);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }

        }
    }
}
