package btw.community.arminias.foodspoil;

import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

public class ContainerFreezerGui extends GuiContainer {
    public TileEntityFreezer freezer;

    public ContainerFreezerGui(InventoryPlayer par1InventoryPlayer, TileEntityFreezer par2TileEntityDispenser)
    {
        super(new ContainerFreezer(par1InventoryPlayer, par2TileEntityDispenser));
        this.freezer = par2TileEntityDispenser;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        String var3 = this.freezer.isInvNameLocalized() ? this.freezer.getInvName() : StatCollector.translateToLocal(this.freezer.getInvName());
        this.fontRenderer.drawString(var3, this.xSize / 2 - this.fontRenderer.getStringWidth(var3) / 2, 6, 4210752);
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture("/gui/freezer.png");
        int var4 = (this.width - this.xSize) / 2;
        int var5 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var4, var5, 0, 0, this.xSize, this.ySize);
        int var6;
        if (this.freezer.isFreezing())
        {
            var6 = this.freezer.getFreezeTimeRemainingScaled(16);
            //this.drawTexturedModalRect(var4 + 56, var5 + 36 + 12 - var6, 176, 12 - var6, 14, var6 + 2);
            this.drawTexturedModalRect(var4 + 25, var5 + 64 - var6, 176, 16 - var6, 17, var6 + 2);
            //62 + 18 - 54, 17 + 18 - 8
        }
        // Draw the overflow progress bar.
        var6 = (int) (this.freezer.getWaterLevel() * 6.5F); // 0-56
        //this.drawGradientRect(var4 + 9, var5 + 69 - 56,   var4 + 9 + 8, var5 + 69, 0xFF8b8b8b, 0xFF8b8b8b);
        //this.drawGradientRect(var4 + 9, var5 + 69 - 52,   var4 + 9 + 8, var5 + 69, 0xFFFF8b8b, 0xFFFF8b8b);

        if (this.freezer.hasPotionReady()) {
            this.drawGradientRect(var4 + 9, var5 + 69 - var6, var4 + 9 + 8, var5 + 69, 0xFF55AAAA, 0xFF55AAAA);
        } else {
            this.drawGradientRect(var4 + 9, var5 + 69 - var6, var4 + 9 + 8, var5 + 69, 0xFF2222DD, 0xFF2222DD);
        }
    }
}
