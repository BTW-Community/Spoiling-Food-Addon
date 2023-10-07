package btw.community.arminias.foodspoil;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

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

    public static void mergeDecayNBTsPartial(ItemStack stack1, ItemStack stack2, int amountToBeRemovedFromStack2) {
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
        return item != null && (tag = item.getTagCompound()) != null &&
                (spoilDate = tag.getLong("spoilDate")) > 0 && worldTime > spoilDate;
    }

    public static float getPercentageSpoilTimeLeft(ItemStack item, long worldTime) {
        NBTTagCompound tag;
        long spoilDate;
        if (item != null && (tag = item.getTagCompound()) != null &&
                (spoilDate = tag.getLong("spoilDate")) > 0) {
            if (worldTime < spoilDate) {
                return (float) (spoilDate - worldTime) / (float) FoodType.getDecayTimeFast(item.itemID);
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
}