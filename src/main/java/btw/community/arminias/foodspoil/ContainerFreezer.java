package btw.community.arminias.foodspoil;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class ContainerFreezer extends Container
{
    public static final int ID = 50;
    private TileEntityFreezer tileEntityFreezer;
    private int lastFreezeTime;
    private int lastItemFreezeTime;
    private byte lastOverflowCounter;
    private byte lastBadFoodTickCounter;

    public ContainerFreezer(IInventory par1IInventory, TileEntityFreezer par2TileEntityFreezer)
    {
        this.tileEntityFreezer = par2TileEntityFreezer;
        int var3;
        int var4;

        for (var3 = 0; var3 < 3; ++var3)
        {
            for (var4 = 0; var4 < 3; ++var4)
            {
                this.addSlotToContainer(new Slot(par2TileEntityFreezer, var4 + var3 * 3, 62 + var4 * 18, 17 + var3 * 18));
            }
        }
        this.addSlotToContainer(new Slot(par2TileEntityFreezer, 9, 62 + 18 - 54, 17 + 18 - 8));

        for (var3 = 0; var3 < 3; ++var3)
        {
            for (var4 = 0; var4 < 9; ++var4)
            {
                this.addSlotToContainer(new Slot(par1IInventory, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
            }
        }

        for (var3 = 0; var3 < 9; ++var3)
        {
            this.addSlotToContainer(new Slot(par1IInventory, var3, 8 + var3 * 18, 142));
        }

    }

    @Environment(EnvType.CLIENT)
    public void updateProgressBar(int par1, int par2)
    {
        if (par1 == 1)
        {
            this.tileEntityFreezer.freezeTime = par2;
        }

        if (par1 == 2)
        {
            this.tileEntityFreezer.currentItemFreezeTime = par2;
        }

        if (par1 == 3)
        {
            this.tileEntityFreezer.overflowCounter = (byte) par2;
        }

        if (par1 == 4)
        {
            this.tileEntityFreezer.badFoodTickCounter = (byte) par2;
        }
    }

    public void addCraftingToCrafters(ICrafting par1ICrafting)
    {
        super.addCraftingToCrafters(par1ICrafting);
        par1ICrafting.sendProgressBarUpdate(this, 1, this.tileEntityFreezer.freezeTime);
        par1ICrafting.sendProgressBarUpdate(this, 2, this.tileEntityFreezer.currentItemFreezeTime);
        par1ICrafting.sendProgressBarUpdate(this, 3, this.tileEntityFreezer.overflowCounter);
        par1ICrafting.sendProgressBarUpdate(this, 4, this.tileEntityFreezer.badFoodTickCounter);
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int var1 = 0; var1 < this.crafters.size(); ++var1)
        {
            ICrafting var2 = (ICrafting)this.crafters.get(var1);

            if (this.lastFreezeTime != this.tileEntityFreezer.freezeTime)
            {
                var2.sendProgressBarUpdate(this, 1, this.tileEntityFreezer.freezeTime);
            }

            if (this.lastItemFreezeTime != this.tileEntityFreezer.currentItemFreezeTime)
            {
                var2.sendProgressBarUpdate(this, 2, this.tileEntityFreezer.currentItemFreezeTime);
            }

            if (this.lastOverflowCounter!= this.tileEntityFreezer.overflowCounter)
            {
                var2.sendProgressBarUpdate(this, 3, this.tileEntityFreezer.overflowCounter);
            }

            if (this.lastBadFoodTickCounter!= this.tileEntityFreezer.badFoodTickCounter)
            {
                var2.sendProgressBarUpdate(this, 4, this.tileEntityFreezer.badFoodTickCounter);
            }
        }

        this.lastFreezeTime = this.tileEntityFreezer.freezeTime;
        this.lastItemFreezeTime = this.tileEntityFreezer.currentItemFreezeTime;
        this.lastOverflowCounter = this.tileEntityFreezer.overflowCounter;
        this.lastBadFoodTickCounter = this.tileEntityFreezer.badFoodTickCounter;
    }

    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return this.tileEntityFreezer.isUseableByPlayer(par1EntityPlayer);
    }

    public IInventory getTileEntityFreezer()
    {
        return this.tileEntityFreezer;
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack var3 = null;
        Slot var4 = (Slot)this.inventorySlots.get(par2);

        if (var4 != null && var4.getHasStack())
        {
            ItemStack var5 = var4.getStack();
            var3 = var5.copy();

            if (par2 < 9)
            {
                if (!this.mergeItemStack(var5, 9, 45, true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(var5, 0, 9, false))
            {
                return null;
            }

            if (var5.stackSize == 0)
            {
                var4.putStack((ItemStack)null);
            }
            else
            {
                var4.onSlotChanged();
            }

            if (var5.stackSize == var3.stackSize)
            {
                return null;
            }

            var4.onPickupFromSlot(par1EntityPlayer, var5);
        }

        return var3;
    }
}
