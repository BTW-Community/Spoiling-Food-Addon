package btw.community.arminias.foodspoil;

import btw.community.arminias.metadata.extension.WorldExtension;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Unique;

public class Utils {

    public static void mergeDecayNBTs(ItemStack stack1, ItemStack stack2) {
        if (FoodType.getFoodTypeFast(stack1.itemID) == FoodType.UNSPOILABLE) {
            stack1.stackTagCompound.setLong("spoilDate", Long.MAX_VALUE);
            stack1.stackTagCompound.setLong("creationDate", 0);
            stack2.stackTagCompound.setLong("spoilDate", Long.MAX_VALUE);
            stack2.stackTagCompound.setLong("creationDate", 0);
        } else {
            long avg = (stack1.stackTagCompound.getLong("spoilDate") * stack1.stackSize + stack2.stackSize * stack2.stackTagCompound.getLong("spoilDate")) / (long) (stack1.stackSize + stack2.stackSize);
            long avg2 = (stack1.stackTagCompound.getLong("creationDate") * stack1.stackSize + stack2.stackSize * stack2.stackTagCompound.getLong("creationDate")) / (long) (stack1.stackSize + stack2.stackSize);
            stack2.stackTagCompound.setLong("spoilDate", avg);
            stack2.stackTagCompound.setLong("creationDate", avg2);
            stack1.stackTagCompound.setLong("spoilDate", avg);
            stack1.stackTagCompound.setLong("creationDate", avg2);
        }
    }

    public static boolean mergeDecayNBTsStricter(ItemStack stack1, ItemStack stack2, long totalWorldTime) {
        if (FoodType.getFoodTypeFast(stack1.itemID) == FoodType.UNSPOILABLE) {
            stack1.stackTagCompound.setLong("spoilDate", Long.MAX_VALUE);
            stack1.stackTagCompound.setLong("creationDate", 0);
            stack2.stackTagCompound.setLong("spoilDate", Long.MAX_VALUE);
            stack2.stackTagCompound.setLong("creationDate", 0);
            return true;
        } else if (Math.abs(Utils.getPercentageSpoilTimeLeft(stack1, totalWorldTime) - Utils.getPercentageSpoilTimeLeft(stack2, totalWorldTime)) < FoodSpoilAddon.getAutomaticMergeThreshold()) {
            long avg = (stack1.stackTagCompound.getLong("spoilDate") * stack1.stackSize + stack2.stackSize * stack2.stackTagCompound.getLong("spoilDate")) / (long) (stack1.stackSize + stack2.stackSize);
            long avg2 = (stack1.stackTagCompound.getLong("creationDate") * stack1.stackSize + stack2.stackSize * stack2.stackTagCompound.getLong("creationDate")) / (long) (stack1.stackSize + stack2.stackSize);
            stack2.stackTagCompound.setLong("spoilDate", avg);
            stack2.stackTagCompound.setLong("creationDate", avg2);
            stack1.stackTagCompound.setLong("spoilDate", avg);
            stack1.stackTagCompound.setLong("creationDate", avg2);
            return true;
        }
        return false;
    }

    public static void mergeDecayNBTsPartialRelaxed(ItemStack stack1, ItemStack stack2, int amountToBeRemovedFromStack2) {
        if (FoodType.getFoodTypeFast(stack1.itemID) == FoodType.UNSPOILABLE) {
            stack1.stackTagCompound.setLong("spoilDate", Long.MAX_VALUE);
            stack2.stackTagCompound.setLong("spoilDate", Long.MAX_VALUE);
            stack2.stackTagCompound.setLong("creationDate", 0);
            stack1.stackTagCompound.setLong("creationDate", 0);
        } else {
            long avg = (stack1.stackTagCompound.getLong("spoilDate") * stack1.stackSize + amountToBeRemovedFromStack2 * stack2.stackTagCompound.getLong("spoilDate")) / (long) (stack1.stackSize + amountToBeRemovedFromStack2);
            long avg2 = (stack1.stackTagCompound.getLong("creationDate") * stack1.stackSize + amountToBeRemovedFromStack2 * stack2.stackTagCompound.getLong("creationDate")) / (long) (stack1.stackSize + amountToBeRemovedFromStack2);
            stack1.stackTagCompound.setLong("spoilDate", avg);
            stack1.stackTagCompound.setLong("creationDate", avg2);
        }
    }

    public static boolean mergeDecayNBTsPartialStricter(ItemStack stack1, ItemStack stack2, int amountToBeRemovedFromStack2, long totalWorldTime) {
        if (FoodType.getFoodTypeFast(stack1.itemID) == FoodType.UNSPOILABLE) {
            stack1.stackTagCompound.setLong("spoilDate", Long.MAX_VALUE);
            stack2.stackTagCompound.setLong("spoilDate", Long.MAX_VALUE);
            stack2.stackTagCompound.setLong("creationDate", 0);
            stack1.stackTagCompound.setLong("creationDate", 0);
            return true;
        } else if (Math.abs(Utils.getPercentageSpoilTimeLeft(stack1, totalWorldTime) - Utils.getPercentageSpoilTimeLeft(stack2, totalWorldTime)) < FoodSpoilAddon.getAutomaticMergeThreshold()) {
            long avg = (stack1.stackTagCompound.getLong("spoilDate") * stack1.stackSize + amountToBeRemovedFromStack2 * stack2.stackTagCompound.getLong("spoilDate")) / (long) (stack1.stackSize + amountToBeRemovedFromStack2);
            long avg2 = (stack1.stackTagCompound.getLong("creationDate") * stack1.stackSize + amountToBeRemovedFromStack2 * stack2.stackTagCompound.getLong("creationDate")) / (long) (stack1.stackSize + amountToBeRemovedFromStack2);
            stack1.stackTagCompound.setLong("spoilDate", avg);
            stack1.stackTagCompound.setLong("creationDate", avg2);
            return true;
        }
        return false;
    }

    public static void initFoodItemServer(ItemStack itemStack, int itemID) {
        FoodType foodType = FoodType.getFoodTypeFast(itemID);
        if (foodType != null) {
            if (itemStack.stackTagCompound == null) {
                itemStack.setTagCompound(new NBTTagCompound());
            }
            if (!itemStack.stackTagCompound.hasKey("spoilDate")) {
                itemStack.stackTagCompound.setLong("spoilDate", MinecraftServer.getServer().worldServers[0].getTotalWorldTime() + FoodType.getDecayTimeFast(itemID));
            }
            if (!itemStack.stackTagCompound.hasKey("creationDate")) {
                itemStack.stackTagCompound.setLong("creationDate", MinecraftServer.getServer().worldServers[0].getTotalWorldTime());
            }
        }
    }

    public static void initFoodItemClient(ItemStack itemStack, int par1) {
        FoodType foodType = FoodType.getFoodTypeFast(par1);
        if (foodType != null) {
            if (itemStack.stackTagCompound == null) {
                itemStack.setTagCompound(new NBTTagCompound());
            }
            if (!itemStack.stackTagCompound.hasKey("spoilDate")) {
                itemStack.stackTagCompound.setLong("spoilDate", Minecraft.getMinecraft().theWorld.getTotalWorldTime() + FoodType.getDecayTimeFast(par1));
            }
            if (!itemStack.stackTagCompound.hasKey("creationDate")) {
                itemStack.stackTagCompound.setLong("creationDate", Minecraft.getMinecraft().theWorld.getTotalWorldTime());
            }
        }
    }

    public static boolean isBeyondSpoilDate(ItemStack item, long worldTime) {
        NBTTagCompound tag;
        long spoilDate;
        return item != null && (tag = item.getTagCompound()) != null && tag.hasKey("spoilDate") &&
                (spoilDate = tag.getLong("spoilDate")) > 0 && worldTime > spoilDate;
    }

    public static float getPercentageSpoilTimeLeft(ItemStack item, long worldTime) {
        NBTTagCompound tag;
        long spoilDate;
        if (item != null && (tag = item.getTagCompound()) != null && tag.hasKey("spoilDate") &&
                (spoilDate = tag.getLong("spoilDate")) > 0) {
            if (worldTime < spoilDate) {
                return (float) ((double) (spoilDate - worldTime) / (double) FoodType.getDecayTimeFast(item.itemID));
            } else {
                return 0.0F;
            }
        } else {
            return -1.0F;
        }
    }

    /*public static String formatTimeWindow(long timeWindowInSeconds) {
        long hours = timeWindowInSeconds / 3600;
        long minutes = (timeWindowInSeconds % 3600) / 60;
        long seconds = timeWindowInSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }*/

    // Alternative implementation of formatTimeWindow, returning in game time. Input is in ticks / 20.
    public static String formatTimeWindow(long timeWindowInSeconds, boolean extraRealTime) {
        long timeWindowInTicks = timeWindowInSeconds * 20;
        long days = timeWindowInTicks / 24000;
        long hours = (timeWindowInTicks % 24000) / 1000;
        if (!extraRealTime) {
            // Return in game time
            if (days > 0) {
                return String.format("%d days, %d hours", days, hours);
            } else {
                return String.format("%d hours", hours);
            }
        }
        else {
            long minutes = timeWindowInSeconds / 60;
            long seconds = timeWindowInSeconds % 60;
            if (days > 0) {
                //return String.format("%d days, %d hours (%d:%d)", days, hours, minutes, seconds);
                // Format it alternatively with leading zeros
                return String.format("%d days, %d hours (%02d:%02d)", days, hours, minutes, seconds);
            } else {
                return String.format("%d hours (%02d:%02d)", hours, minutes, seconds);
            }
        }
    }

    public static boolean areItemStackTagsEqualFood(ItemStack par0ItemStack, ItemStack par1ItemStack)
    {
        // TODO
        throw new RuntimeException("Not implemented");
    }

    public static boolean isCoolingBlock(Block block) {
        return block != null && (block.blockMaterial == Material.ice || block.blockMaterial == Material.snow || block.blockMaterial == Material.craftedSnow);
    }

    public static long getTotalWorldTime() {
        if (MinecraftServer.getIsServer()) {
            return MinecraftServer.getServer().worldServers[0].getTotalWorldTime();
        } else {
            return Minecraft.getMinecraft().theWorld.getTotalWorldTime();
        }
    }

    public static void inheritFoodDecay(ItemStack oldItem, ItemStack newItem, long worldTime) {
        // Inherit it percentage wise
        if (oldItem != null && newItem != null && oldItem.hasTagCompound() && oldItem.stackTagCompound.hasKey("spoilDate") && oldItem.stackTagCompound.hasKey("creationDate") && FoodType.getFoodTypeFast(newItem.itemID) != null) {
            if (FoodType.getFoodTypeFast(newItem.itemID) != FoodType.UNSPOILABLE) {
                long oldCreationDate = oldItem.stackTagCompound.getLong("creationDate");
                float percentageSpoilTimeLeft = Utils.getPercentageSpoilTimeLeft(oldItem, worldTime);
                percentageSpoilTimeLeft = Math.min(percentageSpoilTimeLeft + FoodSpoilAddon.getCookingSpoilingBonus(), 1.0F);
                long newSpoilDate = worldTime + (long) (percentageSpoilTimeLeft * (float) FoodType.getDecayTimeFast(newItem.itemID));
                long newCreationDate = worldTime - (long) (percentageSpoilTimeLeft * (float) (worldTime - oldCreationDate));
                newItem.stackTagCompound.setLong("spoilDate", newSpoilDate);
                newItem.stackTagCompound.setLong("creationDate", newCreationDate);
            }
        }
    }

    public static void setSpoilTime(ItemStack item, float spoilPercentage, long worldTime) {
        // Inherit it percentage wise
        if (item != null && FoodType.getFoodTypeFast(item.itemID) != null) {
            if (FoodType.getFoodTypeFast(item.itemID) != FoodType.UNSPOILABLE) {
                long newSpoilDate = worldTime + (long) (spoilPercentage * (float) FoodType.getDecayTimeFast(item.itemID));
                if (item.stackTagCompound == null) {
                    item.setTagCompound(new NBTTagCompound());
                }
                item.stackTagCompound.setLong("spoilDate", newSpoilDate);
                item.stackTagCompound.setLong("creationDate", worldTime);
            }
        }
    }

    public static void dropSpoiledBlock(World par1World, int par2, int par3, int par4, int par5) {
        if (!par1World.isRemote)
        {
            Block _this = Block.blocksList[par1World.getBlockId(par2, par3, par4)];
            int var8 = _this.quantityDroppedWithBonus(0, par1World.rand);

            for (int var9 = 0; var9 < var8; ++var9)
            {
                par1World.rand.nextFloat();
                int var10 = _this.idDropped(par5, par1World.rand, 0);

                if (var10 > 0)
                {
                    ItemStack stack = new ItemStack(var10, 1, _this.damageDropped(par5));
                    // Get extra metadata using extension
                    int extra = WorldExtension.cast(par1World).getBlockExtraMetadata(par2, par3, par4);
                    // Set item spoil time as fraction of max spoil time using extra metadata
                    if (stack.stackTagCompound == null) {
                        stack.setTagCompound(new NBTTagCompound());
                    }
                    stack.stackTagCompound.setLong("spoilDate", getSpoilAtTime(extra));
                    _this.dropBlockAsItem_do(par1World, par2, par3, par4, stack);
                }
            }
        }
    }

    private static long getSpoilAtTime(int extra) {
        return (long) extra << FoodSpoilMod.SPOIL_TIME_QUANTIZATION_SHIFT;
    }

    private static void setSpoilAtTime(World world, int i, int j, int k, long spoilAtTime) {
        WorldExtension.cast(world).setBlockExtraMetadata(i, j, k, (int) (spoilAtTime >>> FoodSpoilMod.SPOIL_TIME_QUANTIZATION_SHIFT));
    }
}
