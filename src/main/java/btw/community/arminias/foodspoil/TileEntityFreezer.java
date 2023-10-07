package btw.community.arminias.foodspoil;

import btw.block.tileentity.TileEntityDataPacketHandler;
import btw.inventory.util.InventoryUtils;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class TileEntityFreezer extends TileEntity implements ISidedInventory, TileEntityDataPacketHandler {
    private static final int STACK_SIZE_TO_DROP_WHEN_TIPPED = 8;
    private static final int TICK_EVERY = 1;
    private static final int[] coolingItemIndex = new int[]{9};
    private static final int[] emptyItemIndex = new int[]{};
    private static final int[] cooledItemIndex = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    protected final int BASE_FREEZE_TIME;
    protected final int BASE_TICK_MULTIPLIER;
    /**
     * The number of players currently using this freezer
     */
    public int numUsingPlayers;
    public int freezeTime = 0;
    public int currentItemFreezeTime = 0;
    public byte overflowCounter = 0;
    public byte badFoodTickCounter = 0;
    protected byte numNeighbors = 0;
    private ItemStack[] freezerContents = new ItemStack[10];
    /**
     * Server sync counter (once per 20 ticks)
     */
    private int ticksSinceSync;
    private String field_94045_s;
    private long lastTickTime = 0L;

    public TileEntityFreezer() {
        this.BASE_FREEZE_TIME = 400;
        this.BASE_TICK_MULTIPLIER = 8;
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory() {
        return this.freezerContents.length;
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int par1) {
        return this.freezerContents[par1];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    public ItemStack decrStackSize(int par1, int par2) {
        if (this.freezerContents[par1] != null) {
            ItemStack var3;

            if (this.freezerContents[par1].stackSize <= par2) {
                var3 = this.freezerContents[par1];
                this.freezerContents[par1] = null;
                this.onInventoryChanged();
                return var3;
            } else {
                var3 = this.freezerContents[par1].splitStack(par2);

                if (this.freezerContents[par1].stackSize == 0) {
                    this.freezerContents[par1] = null;
                }

                this.onInventoryChanged();
                return var3;
            }
        } else {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    public ItemStack getStackInSlotOnClosing(int par1) {
        if (this.freezerContents[par1] != null) {
            ItemStack var2 = this.freezerContents[par1];
            this.freezerContents[par1] = null;
            return var2;
        } else {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
        this.freezerContents[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }

        this.onInventoryChanged();
    }

    /**
     * Returns the name of the inventory.
     */
    public String getInvName() {
        return this.isInvNameLocalized() ? this.field_94045_s : "foodspoil.freezer.container";
    }

    /**
     * If this returns false, the inventory name will be used as an unlocalized name, and translated into the player's
     * language. Otherwise it will be used directly.
     */
    public boolean isInvNameLocalized() {
        return this.field_94045_s != null && this.field_94045_s.length() > 0;
    }

    /**
     * Returns true if the furnace is currently freezing
     */
    public boolean isFreezing() {
        return this.freezeTime > 0;
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
        this.freezerContents = new ItemStack[this.getSizeInventory()];

        if (par1NBTTagCompound.hasKey("CustomName")) {
            this.field_94045_s = par1NBTTagCompound.getString("CustomName");
        }

        for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
            NBTTagCompound var4 = (NBTTagCompound) var2.tagAt(var3);
            int var5 = var4.getByte("Slot") & 255;

            if (var5 >= 0 && var5 < this.freezerContents.length) {
                this.freezerContents[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }
        //this.freezeTime = par1NBTTagCompound.getShort("FreezeTime");
        this.currentItemFreezeTime = getItemFreezeTime(this.freezerContents[9]);
        this.lastTickTime = par1NBTTagCompound.getLong("lastTickTime");
        this.numNeighbors = par1NBTTagCompound.getByte("numNeighbors");
        this.overflowCounter = par1NBTTagCompound.getByte("overflowCounter");
        this.badFoodTickCounter = par1NBTTagCompound.getByte("badFoodTickCounter");


        // FCMOD: Code added to track extended freeze times
        if (par1NBTTagCompound.hasKey("fcFreezeTimeEx")) {
            freezeTime = par1NBTTagCompound.getInteger("fcFreezeTimeEx");

            if (par1NBTTagCompound.hasKey("fcItemFreezeTimeEx")) {
                currentItemFreezeTime = par1NBTTagCompound.getInteger("fcItemFreezeTimeEx");
            }
        }
        // END FCMOD
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        NBTTagList var2 = new NBTTagList();

        for (int var3 = 0; var3 < this.freezerContents.length; ++var3) {
            if (this.freezerContents[var3] != null) {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte) var3);
                this.freezerContents[var3].writeToNBT(var4);
                var2.appendTag(var4);
            }
        }

        par1NBTTagCompound.setTag("Items", var2);

        if (this.isInvNameLocalized()) {
            par1NBTTagCompound.setString("CustomName", this.field_94045_s);
        }

        // FCMOD: Code added to track extended burn times
        par1NBTTagCompound.setInteger("fcFreezeTimeEx", this.freezeTime);
        par1NBTTagCompound.setInteger("fcItemFreezeTimeEx", this.currentItemFreezeTime);
        par1NBTTagCompound.setLong("lastTickTime", this.lastTickTime);
        par1NBTTagCompound.setByte("numNeighbors", this.numNeighbors);
        par1NBTTagCompound.setByte("overflowCounter", this.overflowCounter);
        par1NBTTagCompound.setByte("badFoodTickCounter", this.badFoodTickCounter);
        // END FCMOD
    }

    public int getItemFreezeTime(ItemStack stack) {
        if (stack != null && isStackValidForSlot(9, stack)) {
            if (stack.getItem() instanceof ItemBlock) {
                Block block = Block.blocksList[((ItemBlock) stack.getItem()).getBlockID()];
                if (block.blockMaterial == Material.ice || block.blockMaterial == Material.snow || block.blockMaterial == Material.craftedSnow) {
                    return BASE_FREEZE_TIME * 4 * BASE_TICK_MULTIPLIER;
                }
            }
            return BASE_FREEZE_TIME * BASE_TICK_MULTIPLIER;
        }
        return 0;
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    public int getInventoryStackLimit() {
        return 16;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
        return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && par1EntityPlayer.getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D) <= 64.0D;
    }

    /**
     * Causes the TileEntity to reset all it's cached values for it's container block, blockID, metaData and in the case
     * of freezers, the adjcacent freezer check
     */
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
    }

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    public void updateEntity() {
        super.updateEntity();
        long tick = this.worldObj.getTotalWorldTime();
        if (this.lastTickTime == 0L) {
            this.lastTickTime = tick;
        }
        if (tick % TICK_EVERY == 0) {
            long oldTickTime = this.lastTickTime;
            this.lastTickTime = tick;
            long timeSinceLastTick = this.lastTickTime - oldTickTime;
            if (freezeTime > 0 && overflowCounter < 8) {
                freezeTime -= timeSinceLastTick * BASE_TICK_MULTIPLIER / (numNeighbors * 0.25 + 0.25);
                updateItemSpoilTimes(timeSinceLastTick);
                /*if (overflowCounter > 0) {
                    boolean couldOverflow = updateWaterOverflowing(timeSinceLastTick);
                    if (couldOverflow) {
                        overflowCounter = 0;
                    }
                }*/
            } else if (overflowCounter < 8) {
                if (this.freezerContents[9] == null || !isStackValidForSlot(9, this.freezerContents[9])) {
                    // Grab the next valid item from the inventory, swap it into slot 9
                    for (int i = 0; i < 9; ++i) {
                        if (this.freezerContents[i] != null && isStackValidForSlot(9, this.freezerContents[i])) {
                            ItemStack temp = this.freezerContents[9];
                            this.freezerContents[9] = this.freezerContents[i];
                            this.freezerContents[i] = temp;
                            break;
                        }
                    }
                }
                if (this.freezerContents[9] != null && isStackValidForSlot(9, this.freezerContents[9])) {
                    this.currentItemFreezeTime = this.freezeTime = getItemFreezeTime(this.freezerContents[9]);
                    decrStackSize(9, 1);
                    updateItemSpoilTimes(timeSinceLastTick);
                    overflowCounter++;
                    for (int i = 0; i < 9; i++) {
                        // Check for (almost) bad food
                        if (this.freezerContents[i] != null && isStackValidForSlot(i, this.freezerContents[i])) {
                            ItemStack item = this.freezerContents[i];
                            double spoilPercentage = Utils.getPercentageSpoilTimeLeft(item, worldObj.getTotalWorldTime());
                            if (spoilPercentage > 0 && spoilPercentage <= FoodSpoilMod.FOOD_GETTING_BAD_PERCENTAGE
                                && (FoodType.getFoodTypeFast(item.getItem()) == FoodType.FRUIT || FoodType.getFoodTypeFast(item.getItem()) == FoodType.VEGETABLE)) {
                                badFoodTickCounter = (byte) Math.min(8, badFoodTickCounter + 1);
                                if (worldObj != null && !worldObj.isRemote) {
                                    worldObj.setBlockTileEntity(xCoord, yCoord, zCoord, this);
                                }
                                break;
                            }
                        }
                    }
                    this.onInventoryChanged();
                    /*boolean couldOverflow = updateWaterOverflowing(timeSinceLastTick);
                    if (couldOverflow) {
                        overflowCounter = 0;
                    }
                    else {
                        overflowCounter++;
                    }*/
                }
            } else {
                boolean couldOverflow = updateWaterOverflowing(timeSinceLastTick);
                if (couldOverflow) {
                    overflowCounter = 0;
                    badFoodTickCounter = 0;
                    if (worldObj != null && !worldObj.isRemote) {
                        worldObj.setBlockTileEntity(xCoord, yCoord, zCoord, this);
                    }
                    this.onInventoryChanged();
                }
            }
            if (this.worldObj != null && !this.worldObj.isRemote && this.freezerContents != null) {
                for (int i = 0; i < this.getSizeInventory(); ++i) {
                    ItemStack item = this.freezerContents[i];
                    if (Utils.isBeyondSpoilDate(item, this.worldObj.getTotalWorldTime())) {
                        this.freezerContents[i] = FoodType.doItemDecayFast(item);
                        this.onInventoryChanged();
                    }
                }
            }
        }

        ++this.ticksSinceSync;
        float var1;

        if (!this.worldObj.isRemote && this.numUsingPlayers != 0 && (this.ticksSinceSync + this.xCoord + this.yCoord + this.zCoord) % 200 == 0) {
            this.numUsingPlayers = 0;
            var1 = 5.0F;
            List var2 = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getAABBPool().getAABB((float) this.xCoord - var1, (float) this.yCoord - var1, (float) this.zCoord - var1, (float) (this.xCoord + 1) + var1, (float) (this.yCoord + 1) + var1, (float) (this.zCoord + 1) + var1));

            for (Object o : var2) {
                EntityPlayer var4 = (EntityPlayer) o;

                if (var4.openContainer instanceof ContainerFreezer) {
                    IInventory var5 = ((ContainerFreezer) var4.openContainer).getTileEntityFreezer();
                    if (var5 == this) {
                        ++this.numUsingPlayers;
                    }
                }
            }
        }
        int iBlockID = worldObj.getBlockId(xCoord, yCoord, zCoord);

        Block block = Block.blocksList[iBlockID];

        if (block == null || !(block instanceof FreezerBlock)) {
            // shouldn't happen but just in case... don't want to crash the game over it. Just stop ticking. The block will be removed soon.
            return;
        }

        FreezerBlock freezerBlock = (FreezerBlock) block;
        if (freezerBlock.getMechanicallyPoweredFlag(worldObj, xCoord, yCoord, zCoord)) {
            int iTiltFacing = freezerBlock.getTiltFacing(worldObj, xCoord, yCoord, zCoord);
            attemptToEjectStackFromInv(iTiltFacing);
            if (overflowCounter > 0) {
                boolean couldEmpty = attemptToEjectWater(iTiltFacing);
                if (couldEmpty) {
                    overflowCounter = 0;
                    badFoodTickCounter = 0;
                    if (worldObj != null && !worldObj.isRemote) {
                        worldObj.setBlockTileEntity(xCoord, yCoord, zCoord, this);
                    }
                    this.onInventoryChanged();
                }
            }
        }
    }

    @Override
    public void onInventoryChanged() {
        super.onInventoryChanged();
    }

    protected boolean updateWaterOverflowing(long timeSinceLastTick) {
        boolean couldOverflow = false;
        for (int i = 0; i < 6; ++i) {
            if (worldObj.isAirBlock(
                    xCoord + Facing.offsetsXForSide[i],
                    yCoord + Facing.offsetsYForSide[i],
                    zCoord + Facing.offsetsZForSide[i])) {
                MiscUtils.placeNonPersistentWater(worldObj,
                        xCoord + Facing.offsetsXForSide[i],
                        yCoord + Facing.offsetsYForSide[i],
                        zCoord + Facing.offsetsZForSide[i]);
                couldOverflow = true;
            }
        }
        return couldOverflow;
    }

    protected boolean attemptToEjectWater(int facing) {
        BlockPos targetPos = new BlockPos(xCoord, yCoord, zCoord);
        targetPos.addFacingAsOffset(facing);
        boolean couldEmpty = false;
        if (worldObj.isAirBlock(targetPos.x, targetPos.y, targetPos.z)) {
            couldEmpty = true;
            MiscUtils.placeNonPersistentWater(worldObj,
                    targetPos.x,
                    targetPos.y,
                    targetPos.z
            );
        }
        return couldEmpty;
    }

    protected void updateItemSpoilTimes(long timeSinceLastTick) {
        for (int i = 0; i < this.getSizeInventory(); ++i) {
            ItemStack item = this.getStackInSlot(i);
            NBTTagCompound tag;
            long spoilDate;
            if (item != null && (tag = item.getTagCompound()) != null &&
                    (spoilDate = tag.getLong("spoilDate")) > 0) {
                tag.setLong("spoilDate", spoilDate + timeSinceLastTick);
            }
        }
    }

    private void attemptToEjectStackFromInv(int iTiltFacing) {
        int iStackIndex = InventoryUtils.getFirstOccupiedStackNotOfItem(this, Item.brick.itemID);

        if (iStackIndex >= 0 && iStackIndex < getSizeInventory()) {
            ItemStack invStack = getStackInSlot(iStackIndex);

            int iEjectStackSize = Math.min(STACK_SIZE_TO_DROP_WHEN_TIPPED, invStack.stackSize);

            ItemStack ejectStack = new ItemStack(invStack.itemID, iEjectStackSize, invStack.getItemDamage());

            InventoryUtils.copyEnchantments(ejectStack, invStack);

            BlockPos targetPos = new BlockPos(xCoord, yCoord, zCoord);

            targetPos.addFacingAsOffset(iTiltFacing);

            boolean bEjectIntoWorld = false;

            if (worldObj.isAirBlock(targetPos.x, targetPos.y, targetPos.z)) {
                bEjectIntoWorld = true;
            } else {
                if (WorldUtils.isReplaceableBlock(worldObj, targetPos.x, targetPos.y, targetPos.z)) {
                    bEjectIntoWorld = true;
                } else {
                    int iTargetBlockID = worldObj.getBlockId(targetPos.x, targetPos.y, targetPos.z);

                    Block targetBlock = Block.blocksList[iTargetBlockID];

                    if (!targetBlock.blockMaterial.isSolid()) {
                        bEjectIntoWorld = true;
                    }
                }
            }

            if (bEjectIntoWorld) {
                ejectStack(ejectStack, iTiltFacing);

                decrStackSize(iStackIndex, iEjectStackSize);
            }
        }
    }

    private void ejectStack(ItemStack stack, int iFacing) {
        Vec3 itemPos = MiscUtils.convertBlockFacingToVector(iFacing);

        itemPos.xCoord *= 0.5F;
        itemPos.yCoord *= 0.5F;
        itemPos.zCoord *= 0.5F;

        itemPos.xCoord += ((float) xCoord) + 0.5F;
        itemPos.yCoord += ((float) yCoord) + 0.25F;
        itemPos.zCoord += ((float) zCoord) + 0.5F;

        EntityItem entityItem = (EntityItem) EntityList.createEntityOfType(EntityItem.class, worldObj, itemPos.xCoord, itemPos.yCoord, itemPos.zCoord, stack);

        Vec3 itemVel = MiscUtils.convertBlockFacingToVector(iFacing);

        itemVel.xCoord *= 0.1F;
        itemVel.yCoord *= 0.1F;
        itemVel.zCoord *= 0.1F;

        entityItem.motionX = itemVel.xCoord;
        entityItem.motionY = itemVel.yCoord;
        entityItem.motionZ = itemVel.zCoord;

        entityItem.delayBeforeCanPickup = 10;

        worldObj.spawnEntityInWorld(entityItem);
    }

    /**
     * Called when a client event is received with the event number and argument, see World.sendClientEvent
     */
    public boolean receiveClientEvent(int par1, int par2) {
        if (par1 == 1) {
            this.numUsingPlayers = par2;
            return true;
        } else {
            return super.receiveClientEvent(par1, par2);
        }
    }

    public void openChest() {
        if (this.numUsingPlayers < 0) {
            this.numUsingPlayers = 0;
        }

        ++this.numUsingPlayers;
        this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID, 1, this.numUsingPlayers);
        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType().blockID);
    }

    public void closeChest() {
        if (this.getBlockType() != null && this.getBlockType() instanceof FreezerBlock) {
            --this.numUsingPlayers;
            this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID, 1, this.numUsingPlayers);
            this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
            this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType().blockID);
        }
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    public boolean isStackValidForSlot(int slot, ItemStack itemStack) {
        if (slot == 9) {
            if (itemStack.itemID == Item.snowball.itemID) {
                return true;
            } else {
                if (itemStack.getItem() instanceof ItemBlock) {
                    Block block = Block.blocksList[((ItemBlock) itemStack.getItem()).getBlockID()];
                    return block.blockMaterial == Material.ice || block.blockMaterial == Material.snow || block.blockMaterial == Material.craftedSnow;
                } else {
                    return false;
                }
            }
        } else {
            return true;
        }
    }

    /**
     * invalidates a tile entity
     */
    public void invalidate() {
        super.invalidate();
        this.updateContainingBlockInfo();
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int var1) {
        return var1 == 0 ? coolingItemIndex : ((var1 >= 2 && var1 <= 5) ? cooledItemIndex : emptyItemIndex);
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemStack, int side) {
        if (side == 0 && slot == 9 || side >= 2 && side <= 5) {
            return isStackValidForSlot(slot, itemStack);
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack itemStack, int side) {
        return side == 1 && slot != 9;
    }

    @Environment(EnvType.CLIENT)
    public int getFreezeTimeRemainingScaled(int par1) {
        if (this.currentItemFreezeTime == 0) {
            this.currentItemFreezeTime = 200;
        }

        return this.freezeTime * par1 / this.currentItemFreezeTime;
    }

    public void onNeighborChanged() {
        byte numNeighbors = 0;
        for (int i = 0; i < 6; ++i) {
            if (worldObj.getBlockMaterial(
                    xCoord + Facing.offsetsXForSide[i],
                    yCoord + Facing.offsetsYForSide[i],
                    zCoord + Facing.offsetsZForSide[i]).isSolid()) {
                numNeighbors++;
            }
        }
        this.numNeighbors = numNeighbors;
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByte("x", this.badFoodTickCounter);
        tag.setByte("y", this.overflowCounter);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void readNBTFromPacket( NBTTagCompound tag )
    {
        this.badFoodTickCounter = tag.getByte("x");
        this.overflowCounter = tag.getByte("y");
        //worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
    }

    public byte getWaterLevel() {
        return overflowCounter;
    }

    public boolean hasPotionReady() {
        return overflowCounter > 0 && this.badFoodTickCounter >= 7;
    }

    public ItemStack getPotion() {
        return FoodSpoilAddon.sleepingPotion.createNormalPotion();
    }

    public void resetPotion() {
        badFoodTickCounter = 0;
        this.onInventoryChanged();
    }

    public void decrementOverflowCounter() {
        overflowCounter--;
        this.onInventoryChanged();
    }
}
