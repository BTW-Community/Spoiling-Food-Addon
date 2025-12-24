package btw.community.arminias.foodspoil;

import btw.item.BTWItems;
import btw.item.items.MushroomSoupItem;
import btw.item.items.SoupItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static api.util.MiscUtils.TICKS_PER_GAME_DAY;


public enum FoodType {
    MEAT(TICKS_PER_GAME_DAY, Item.rottenFlesh),
    VEGETABLE(TICKS_PER_GAME_DAY, BTWItems.foulFood),
    FRUIT(TICKS_PER_GAME_DAY, BTWItems.foulFood),
    BREAD(TICKS_PER_GAME_DAY, BTWItems.foulFood),
    FISH(TICKS_PER_GAME_DAY, BTWItems.foulFood),
    MILK(TICKS_PER_GAME_DAY, Item.bucketEmpty),
    EGG(TICKS_PER_GAME_DAY, BTWItems.foulFood),
    SWEET(TICKS_PER_GAME_DAY, BTWItems.foulFood),
    MUSHROOM(TICKS_PER_GAME_DAY, null),
    SOUP(TICKS_PER_GAME_DAY, Item.bowlEmpty),
    PLANT(TICKS_PER_GAME_DAY, FoodSpoilAddon.spoiledCrop),
    UNSPOILABLE(Long.MAX_VALUE, null),
    OTHER(TICKS_PER_GAME_DAY, null),
    OVERRIDE(9999L, null);

    private static final ArrayList<Integer> MEAT_ITEMS = new ArrayList<>();
    private static final ArrayList<Integer> VEGETABLE_ITEMS = new ArrayList<>();
    private static final ArrayList<Integer> FRUIT_ITEMS = new ArrayList<>();
    private static final ArrayList<Integer> BREAD_ITEMS = new ArrayList<>();
    private static final ArrayList<Integer> FISH_ITEMS = new ArrayList<>();
    private static final ArrayList<Integer> MILK_ITEMS = new ArrayList<>();
    private static final ArrayList<Integer> EGG_ITEMS = new ArrayList<>();
    private static final ArrayList<Integer> SWEET_ITEMS = new ArrayList<>();
    private static final ArrayList<Integer> MUSHROOM_ITEMS = new ArrayList<>();
    private static final ArrayList<Integer> SOUP_ITEMS = new ArrayList<>();
    private static final ArrayList<Integer> PLANT_ITEMS = new ArrayList<>();
    private static final ArrayList<Integer> UNSPOILABLE_ITEMS = new ArrayList<>();
    private static final ArrayList<Integer> OTHER_ITEMS = new ArrayList<>();
    private static final ArrayList<Integer> OVERRIDE_ITEMS = new ArrayList<>();

    private static final Map<Integer, FoodTypeRecord> OVERRIDE_ITEMS_MAP = new HashMap<>();

    private static final FoodTypeRecord[] FAST_LOOKUP = new FoodTypeRecord[Item.itemsList.length];
    
    private static boolean isBuilt = false;

    static {
        MEAT_ITEMS.addAll(Arrays.asList(
                Item.porkRaw.itemID, Item.porkCooked.itemID, Item.beefRaw.itemID, Item.beefCooked.itemID, Item.chickenRaw.itemID, Item.chickenCooked.itemID,
                BTWItems.burnedMeat.itemID, BTWItems.batWing.itemID, BTWItems.curedMeat.itemID, BTWItems.kibble.itemID,
                BTWItems.rawKebab.itemID, BTWItems.cookedKebab.itemID, BTWItems.rawMutton.itemID, BTWItems.cookedMutton.itemID,
                BTWItems.rawWolfChop.itemID, BTWItems.cookedWolfChop.itemID, BTWItems.rawMysteryMeat.itemID, BTWItems.cookedMysteryMeat.itemID,
                BTWItems.rawLiver.itemID, BTWItems.cookedLiver.itemID, BTWItems.steakAndPotatoes.itemID, BTWItems.steakDinner.itemID,
                BTWItems.wolfDinner.itemID, BTWItems.porkDinner.itemID, BTWItems.tastySandwich.itemID, BTWItems.rawCheval.itemID, BTWItems.cookedCheval.itemID)
        );
        VEGETABLE_ITEMS.addAll(Arrays.asList(
                Item.carrot.itemID, Item.potato.itemID, Item.bakedPotato.itemID, BTWItems.carrot.itemID, BTWItems.cookedCarrot.itemID, BTWItems.boiledPotato.itemID
                )
        );
        FRUIT_ITEMS.addAll(Arrays.asList(
                Item.appleRed.itemID, Item.melon.itemID, BTWItems.mashedMelon.itemID
                )
        );
        BREAD_ITEMS.addAll(Arrays.asList(
                Item.bread.itemID)
        );
        FISH_ITEMS.addAll(Arrays.asList(
                Item.fishRaw.itemID, Item.fishCooked.itemID)
        );
        MILK_ITEMS.addAll(Arrays.asList(
                Item.bucketMilk.itemID, BTWItems.milkChocolateBucket.itemID
                )
        );
        EGG_ITEMS.addAll(Arrays.asList(
                BTWItems.rawEgg.itemID, BTWItems.friedEgg.itemID, BTWItems.rawScrambledEggs.itemID, BTWItems.cookedScrambledEggs.itemID,
                BTWItems.cookedMushroomOmelet.itemID, BTWItems.rawMushroomOmelet.itemID, BTWItems.hardBoiledEgg.itemID, BTWItems.hamAndEggs.itemID,
                BTWItems.unbakedPumpkinPie.itemID, BTWItems.unbakedCake.itemID, Item.egg.itemID)
        );
        SWEET_ITEMS.addAll(Arrays.asList(
                Item.cookie.itemID, Item.pumpkinPie.itemID, Item.cake.itemID, BTWItems.chocolate.itemID, BTWItems.donut.itemID)
        );
        MUSHROOM_ITEMS.addAll(Arrays.asList(
                BTWItems.redMushroom.itemID, BTWItems.brownMushroom.itemID)
        );
        SOUP_ITEMS.addAll(Arrays.asList(
                Item.bowlSoup.itemID, BTWItems.chickenSoup.itemID, BTWItems.heartyStew.itemID, BTWItems.chowder.itemID)
        );
        PLANT_ITEMS.addAll(Arrays.asList(
                Item.wheat.itemID, BTWItems.wheat.itemID, Item.melonSeeds.itemID, Item.pumpkinSeeds.itemID, Item.seeds.itemID, Item.netherStalkSeeds.itemID,
                BTWItems.carrotSeeds.itemID, BTWItems.wheatSeeds.itemID, BTWItems.cocoaBeans.itemID)
        );
        UNSPOILABLE_ITEMS.addAll(Arrays.asList(
                Item.rottenFlesh.itemID, Item.spiderEye.itemID, Item.poisonousPotato.itemID, Item.speckledMelon.itemID, Item.goldenCarrot.itemID, Item.appleGold.itemID,
                BTWItems.creeperOysters.itemID, BTWItems.foulFood.itemID)
        );
        OTHER_ITEMS.addAll(Arrays.asList(
                BTWItems.chickenFeed.itemID, BTWItems.breadDough.itemID, BTWItems.unbakedCookies.itemID, BTWItems.flour.itemID
                )
        );
    }

    private final long decayTime;
    private final Item decayItem;

    /**
     * The food type for the item.
     * @param decayTime The time in ticks for the item to decay.
     *                  1 second = 20 ticks
     *                  1 minute = 1200 ticks
     *                  1 hour = 72000 ticks
     * @param decayItem The item to decay into. If null, the item will be destroyed.
     */
    FoodType(long decayTime, Item decayItem) {
        this.decayTime = decayTime;
        this.decayItem = decayItem;
    }

    /**
     * @param item The item to check.
     * @return The item to decay into.
     */
    public static ItemStack doItemDecayFast(ItemStack item) {
        if (item == null || item.getItem() == null) {
            return null;
        }
        FoodTypeRecord record = FAST_LOOKUP[item.getItem().itemID];
        if (record == null || record.decayItem == null) {
            return null;
        }
        ItemStack decayItem = item.copy();
        decayItem.itemID = record.decayItem.itemID;
        decayItem.stackTagCompound = null;

        if (MinecraftServer.getIsServer()) {
            Utils.initFoodItemServer(decayItem, decayItem.itemID);
        } else {
            Utils.initFoodItemClient(decayItem, decayItem.itemID);
        }

        if (getFoodTypeFast(record.decayItem.itemID) == UNSPOILABLE) {
            decayItem.stackTagCompound.setLong("spoilDate", Long.MAX_VALUE);
            decayItem.stackTagCompound.setLong("creationDate", 0);
        }

        return decayItem;
    }

    /**
     * @param itemID The item to check.
     * @return The itemID to decay into.
     */
    public static int getDecayItemIDFast(int itemID) {
        FoodTypeRecord record = FAST_LOOKUP[itemID];
        if (record == null) {
            return 0;
        }
        return record.decayItem.itemID;
    }


    /**
     * @param item The item to check.
     * @return The food type for the item.
     */
    public static FoodType getFoodTypeFast(Item item) {
        if (item == null) {
            return null;
        }
        return getFoodTypeFast(item.itemID);
    }

    /**
     * @param itemID The item to check.
     * @return The food type for the item.
     */
    public static FoodType getFoodTypeFast(int itemID) {
        FoodTypeRecord record = FAST_LOOKUP[itemID];
        if (record == null) {
            return null;
        }
        return record.foodType;
    }

    /**
     * @param item The item to check.
     * @return The time in ticks for the item to decay.
     *         1 second = 20 ticks
     *         1 minute = 1200 ticks
     *         1 hour = 72000 ticks
     */
    public static long getDecayTimeFast(Item item) {
        if (item == null) {
            return -1;
        }
        return getDecayTimeFast(item.itemID);
    }

    /**
     * @param itemID The item to check.
     * @return The time in ticks for the item to decay.
     *         1 second = 20 ticks
     *         1 minute = 1200 ticks
     *         1 hour = 72000 ticks
     */
    public static long getDecayTimeFast(int itemID) {
        FoodTypeRecord record = FAST_LOOKUP[itemID];
        if (record == null) {
            // Return a very large number
            return Long.MAX_VALUE;
        }
        return record.decayTime;
    }

    /**
     * Only use before the game starts. Is slow.
     * @param item The item to check.
     * @return The food type for the item.
     */
    public static FoodType getFoodTypeSlow(Item item) {
        if (OVERRIDE_ITEMS.contains(item.itemID)) {
            return OVERRIDE_ITEMS_MAP.get(item.itemID).foodType;
        } else if (MEAT_ITEMS.contains(item.itemID)) {
            return MEAT;
        } else if (VEGETABLE_ITEMS.contains(item.itemID)) {
            return VEGETABLE;
        } else if (FRUIT_ITEMS.contains(item.itemID)) {
            return FRUIT;
        } else if (BREAD_ITEMS.contains(item.itemID)) {
            return BREAD;
        } else if (FISH_ITEMS.contains(item.itemID)) {
            return FISH;
        } else if (MILK_ITEMS.contains(item.itemID)) {
            return MILK;
        } else if (EGG_ITEMS.contains(item.itemID)) {
            return EGG;
        } else if (SWEET_ITEMS.contains(item.itemID)) {
            return SWEET;
        } else if (MUSHROOM_ITEMS.contains(item.itemID)) {
            return MUSHROOM;
        } else if (SOUP_ITEMS.contains(item.itemID)) {
            return SOUP;
        } else if (PLANT_ITEMS.contains(item.itemID)) {
            return PLANT;
        } else if (UNSPOILABLE_ITEMS.contains(item.itemID)) {
            return UNSPOILABLE;
        } else if (OTHER_ITEMS.contains(item.itemID)) {
            return OTHER;
        } else {
            return null;
        }
    }

    /**
     * Only use before the game starts.
     * @param item The item to check.
     * @return The item to decay into.
     */
    public static ItemStack doItemDecaySlow(ItemStack item) {
        if (item == null) {
            return null;
        } else if (OVERRIDE_ITEMS_MAP.containsKey(item.getItem().itemID)) {
            ItemStack decayItem = item.copy();
            decayItem.itemID = OVERRIDE_ITEMS_MAP.get(item.getItem().itemID).decayItem.itemID;
            decayItem.stackTagCompound = null;
            return decayItem;
        } else if (item.getItem() instanceof SoupItem || item.getItem() instanceof ItemSoup || item.getItem() instanceof MushroomSoupItem) {
            ItemStack decayItem = item.copy();
            decayItem.itemID = Item.bowlEmpty.itemID;
            decayItem.stackTagCompound = null;
            return decayItem;
        } else {
            FoodType foodType = getFoodTypeSlow(item.getItem());
            if (foodType == null) {
                return item;
            } else if (foodType.getDecayItem() == null) {
                return null;
            } else {
                ItemStack decayItem = item.copy();
                decayItem.itemID = foodType.getDecayItem().itemID;
                decayItem.stackTagCompound = null;
                return decayItem;
            }
        }
    }

    /**
     * @return The time in ticks for the item to decay.
     */
    private long getDecayTime() {
        return decayTime;
    }

    /**
     * @return The item to decay into.
     */
    private Item getDecayItem() {
        return decayItem;
    }

    /**
     * This method should only be called before the game starts.
     * @param item The item to add.
     * @param foodType The food type to add the item to.
     */
    public static void addItemToFoodType(Item item, FoodType foodType) {
        if (isBuilt) {
            throw new IllegalStateException("FoodTypeRegistry is already built.");
        }
        switch (foodType) {
            case MEAT:
                MEAT_ITEMS.add(item.itemID);
                break;
            case VEGETABLE:
                VEGETABLE_ITEMS.add(item.itemID);
                break;
            case FRUIT:
                FRUIT_ITEMS.add(item.itemID);
                break;
            case BREAD:
                BREAD_ITEMS.add(item.itemID);
                break;
            case FISH:
                FISH_ITEMS.add(item.itemID);
                break;
            case MILK:
                MILK_ITEMS.add(item.itemID);
                break;
            case EGG:
                EGG_ITEMS.add(item.itemID);
                break;
            case SWEET:
                SWEET_ITEMS.add(item.itemID);
                break;
            case MUSHROOM:
                MUSHROOM_ITEMS.add(item.itemID);
                break;
            case SOUP:
                SOUP_ITEMS.add(item.itemID);
                break;
            case PLANT:
                PLANT_ITEMS.add(item.itemID);
                break;
            case UNSPOILABLE:
                UNSPOILABLE_ITEMS.add(item.itemID);
                break;
            case OTHER:
                OTHER_ITEMS.add(item.itemID);
                break;
            case OVERRIDE:
                throw new IllegalArgumentException("Call addOverrideItem() instead.");
        }
    }

    /**
     * This method should only be called before the game starts.
     * @param itemID The item to add.
     * @param foodType The food type to add the item to.
     */
    public static void addItemToFoodType(int itemID, FoodType foodType) {
        if (isBuilt) {
            throw new IllegalStateException("FoodTypeRegistry is already built.");
        }
        addItemToFoodType(Item.itemsList[itemID], foodType);
    }

    /**
     * This method should only be called before the game starts.
     * Adds an item to the override food type, which enables you to specify a custom decay time and decay item.
     * @param item The item to add.
     * @param decayTime The decay time in ticks.
     * @param decayItem The item to decay into.
     */
    public static void addOverrideItem(Item item, long decayTime, Item decayItem, FoodType baseFoodType) {
        if (isBuilt) {
            throw new IllegalStateException("FoodTypeRegistry is already built.");
        }
        OVERRIDE_ITEMS_MAP.put(item.itemID, new FoodTypeRecord(baseFoodType, decayTime, decayItem));
        OVERRIDE_ITEMS.add(item.itemID);
    }

    /**
     * This method should only be called before the game starts.
     * Adds an item to the override food type, which enables you to specify a custom decay time and decay item.
     * The item will inherit the decay time and decay item of the food type it is based on.
     * @param item The item to add.
     * @param foodType The food type the item is based on.
     * @param multiplier The multiplier to apply to the decay time.
     */
    public static void addOverrideItemBasedOnType(Item item, FoodType foodType, float multiplier) {
        if (isBuilt) {
            throw new IllegalStateException("FoodTypeRegistry is already built.");
        }
        OVERRIDE_ITEMS_MAP.put(item.itemID, new FoodTypeRecord(foodType, (long) (foodType.decayTime * multiplier), foodType.decayItem));
        OVERRIDE_ITEMS.add(item.itemID);
    }

    /**
     * Builds the registry. This method should be called after all items have been registered.
     */
    public static void build() {
        if (isBuilt) {
            throw new IllegalStateException("FoodTypeRegistry is already built.");
        }
        for (int i = 0; i < FAST_LOOKUP.length; i++) {
            Item item = Item.itemsList[i];
            if (item != null) {
                if (OVERRIDE_ITEMS_MAP.containsKey(i)) {
                    FoodTypeRecord record = OVERRIDE_ITEMS_MAP.get(i);
                    FAST_LOOKUP[i] = record;
                } else {
                    FoodType foodType = getFoodTypeSlow(item);
                    if (foodType != null) {
                        FAST_LOOKUP[i] = new FoodTypeRecord(foodType, foodType.getDecayTime(), foodType.getDecayItem());
                    }
                }
            }
        }
        isBuilt = true;
    }

    /**
     * Rebuilds the registry. Only used internally. Register food types at the start of the game instead.
     */
    static void rebuild() {
        isBuilt = false;
        build();
    }

    private static final class FoodTypeRecord {
        public final FoodType foodType;
        public final long decayTime;
        public final Item decayItem;

        private FoodTypeRecord(FoodType foodType, long decayTime, Item decayItem) {
            this.foodType = foodType;
            this.decayTime = (long) (decayTime * (foodType != UNSPOILABLE ? FoodSpoilAddon.getDecayMultiplier(foodType) : 1));
            this.decayItem = decayItem;
        }
    }
}
