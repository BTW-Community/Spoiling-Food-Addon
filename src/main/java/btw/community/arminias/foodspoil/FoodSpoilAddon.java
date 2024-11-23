package btw.community.arminias.foodspoil;

import btw.BTWAddon;
import btw.block.BTWBlocks;
import btw.crafting.recipe.RecipeManager;
import btw.inventory.util.InventoryUtils;
import btw.item.BTWItems;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static net.minecraft.src.Item.FILTERABLE_SMALL;

public class FoodSpoilAddon extends BTWAddon {
    private static FoodSpoilAddon instance;
    private static final Map<FoodType, Float> decayMultipliers = new HashMap<FoodType, Float>();
    private static float GLOBAL_FOOD_DECAY = 1.0F;
    private static int PLANT_SPOIL_AGE = 7; // 1 + X days
    private static double FOOD_GETTING_BAD_PERCENTAGE = 0.15; // 15% of the total time
    private static double WORLD_ICE_PRESERVATION_FACTOR = 0.8;
    private static float AUTOMATIC_MERGE_THRESHOLD = 0.15F;
    private static float COOKING_SPOILING_BONUS = 0.2F;
    private static int ANIMAL_OLD_AGE = 28 * 24000;
    private static int ANIMAL_DEATH_AGE = 56 * 24000;
    private static int BASE_FREEZE_TIME = 400;
    private static final ArrayList<Integer> SPOILING_BLOCKS = new ArrayList<>();

    public static Item spoiledCrop;
    public static Item goldenCarrotTest;
    public static Block freezer;
    public static SleepingPotionItem sleepingPotion;
    public static Potion sleeping;

    private Map<String, String> config;

    public FoodSpoilAddon() {
        //super("Food Spoil Addon", "0.3.0", "FoodSpoil");
        super();
        instance = this;
    }

    @Override
    public void initialize() {
        spoiledCrop = new SpoiledCropItem(15673);
        goldenCarrotTest = new ItemFood(15675, 1, 0F, false).setNonBuoyant().setFilterableProperties(FILTERABLE_SMALL).setUnlocalizedName("foodspoilmod:spoil_test");
        int freezerBlockID = 2877;
        freezer = new FreezerBlock(freezerBlockID).setHardness(0.5F).setStepSound(Block.soundGlassFootstep).setUnlocalizedName("foodspoil.freezer")
                .setCreativeTab(CreativeTabs.tabDecorations);
        TileEntity.addMapping(TileEntityFreezer.class, "Freezer");
        sleeping = (new SleepingPotion(27, false, 0x33AAAA)).setPotionName("foodspoil.sleeping").setIconIndex(5, 1);
        sleepingPotion = new SleepingPotionItem(15674);

        //PotionHelper.potionRequirements.put(Integer.valueOf(sleeping.getId()), "0 & 1 & 2 & !3 & 0+6");

        if (Item.itemsList[freezerBlockID] == null) {
            Item.itemsList[freezerBlockID] = new ItemBlock(freezerBlockID - 256);
        } else {
            throw new RuntimeException("Item ID " + freezerBlockID + " is already occupied by " + Item.itemsList[freezerBlockID] + " when adding " + freezer);
        }

        parseLocalConfig();
    }

    private void parseConfig(Map<String, String> parseableConfig) {
        for (FoodType foodType : FoodType.values()) {
            String key = foodType.name() + "_DECAY";
            if (parseableConfig.containsKey(key)) {
                float decay = Float.parseFloat(parseableConfig.get(key));
                decayMultipliers.put(foodType, decay);
            }
        }

        GLOBAL_FOOD_DECAY = Float.parseFloat(parseableConfig.get("GLOBAL_FOOD_DECAY"));
        PLANT_SPOIL_AGE = Integer.parseInt(parseableConfig.get("PLANT_SPOIL_AGE"));
        FOOD_GETTING_BAD_PERCENTAGE = Double.parseDouble(parseableConfig.get("FOOD_GETTING_BAD_PERCENTAGE"));
        WORLD_ICE_PRESERVATION_FACTOR = Double.parseDouble(parseableConfig.get("WORLD_ICE_PRESERVATION_FACTOR"));
        AUTOMATIC_MERGE_THRESHOLD = Float.parseFloat(parseableConfig.get("AUTOMATIC_MERGE_THRESHOLD"));
        COOKING_SPOILING_BONUS = Float.parseFloat(parseableConfig.get("COOKING_SPOILING_BONUS"));
        ANIMAL_OLD_AGE = Integer.parseInt(parseableConfig.get("ANIMAL_OLD_AGE"));
        ANIMAL_DEATH_AGE = Integer.parseInt(parseableConfig.get("ANIMAL_DEATH_AGE"));
        BASE_FREEZE_TIME = Integer.parseInt(parseableConfig.get("BASE_FREEZE_TIME"));
    }

    public void parseLocalConfig() {
        parseConfig(config);
    }

    @Override
    public void preInitialize() {
        this.registerProperty("GLOBAL_FOOD_DECAY", "1.0");
        this.registerProperty("MEAT_DECAY", "6.0", "All decay/age values in MC days");
        this.registerProperty("VEGETABLE_DECAY", "9.0");
        this.registerProperty("FRUIT_DECAY", "9.0");
        this.registerProperty("BREAD_DECAY", "10.0");
        this.registerProperty("FISH_DECAY", "4.0");
        this.registerProperty("MILK_DECAY", "7.0");
        this.registerProperty("EGG_DECAY", "7.0");
        this.registerProperty("SWEET_DECAY", "10.0");
        this.registerProperty("MUSHROOM_DECAY", "7.0");
        this.registerProperty("SOUP_DECAY", "7.0");
        this.registerProperty("PLANT_DECAY", "10.0");
        this.registerProperty("OTHER_DECAY", "10.0");

        this.registerProperty("PLANT_SPOIL_AGE", "14");
        this.registerProperty("FOOD_GETTING_BAD_PERCENTAGE", "0.15");
        this.registerProperty("WORLD_ICE_PRESERVATION_FACTOR", "0.8");
        this.registerProperty("AUTOMATIC_MERGE_THRESHOLD", "0.15");
        this.registerProperty("COOKING_SPOILING_BONUS", "0.2");
        this.registerProperty("ANIMAL_OLD_AGE", "28");
        this.registerProperty("ANIMAL_DEATH_AGE", "56");
        this.registerProperty("BASE_FREEZE_TIME", "400");
    }

    @Override
    public void handleConfigProperties(Map<String, String> propertyValues) {
        this.config = propertyValues;
    }

    @Override
    public void postInitialize() {
        FoodType.addOverrideItemBasedOnType(Item.itemsList[BTWBlocks.freshPumpkin.blockID], FoodType.VEGETABLE, 4);
        FoodType.addOverrideItemBasedOnType(Item.itemsList[Block.melon.blockID], FoodType.FRUIT, 4);
        FoodType.addOverrideItemBasedOnType(Item.itemsList[Block.hay.blockID], FoodType.PLANT, 1);
        FoodType.addOverrideItemBasedOnType(Item.itemsList[Block.cake.blockID], FoodType.SWEET, 1);
        FoodType.addOverrideItemBasedOnType(Item.itemsList[Block.mushroomBrown.blockID], FoodType.MUSHROOM, 1);
        FoodType.addOverrideItemBasedOnType(Item.itemsList[Block.mushroomRed.blockID], FoodType.MUSHROOM, 1);
        FoodType.addOverrideItemBasedOnType(Item.itemsList[BTWBlocks.placedMilkBucket.blockID], FoodType.MILK, 1);
        FoodType.addOverrideItemBasedOnType(Item.itemsList[BTWBlocks.placedMilkChocolateBucket.blockID], FoodType.MILK, 1);

        SPOILING_BLOCKS.addAll(Arrays.asList(
                Block.cake.blockID,
                BTWBlocks.freshPumpkin.blockID,
                Block.melon.blockID,
                Block.hay.blockID,
                Block.mushroomBrown.blockID,
                Block.mushroomRed.blockID,
                BTWBlocks.placedMilkBucket.blockID,
                BTWBlocks.placedMilkChocolateBucket.blockID
        ));

        RecipeManager.addRecipe(new ItemStack(freezer), new Object[] {
                "###",
                "#B#",
                "###",
                '#', new ItemStack(BTWItems.woodSidingStubID, 1, InventoryUtils.IGNORE_METADATA), 'B', new ItemStack(Item.bucketEmpty, 1, InventoryUtils.IGNORE_METADATA)
        });

        // Debug Food Item with short spoil time
        FoodType.addOverrideItemBasedOnType(Item.itemsList[goldenCarrotTest.itemID], FoodType.VEGETABLE, 0.002F);
        // Build the food type registry
        FoodType.build();
    }

    @Override
    public void serverPlayerConnectionInitialized(NetServerHandler serverHandler, EntityPlayerMP playerMP) {
        serverHandler.sendPacketToPlayer(new Packet250CustomPayload("foodspoilmod|config", config.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).reduce((a, b) -> a + "\n" + b).get().getBytes()));
    }

    @Override
    public boolean interceptCustomClientPacket(Minecraft mc, Packet250CustomPayload packet) {
        if (packet.channel.equals("foodspoilmod|config")) {
            String[] configLines = new String(packet.data).split("\n");
            Map<String, String> newConfig = new HashMap<>();
            for (String line : configLines) {
                String[] split = line.split("=");
                newConfig.put(split[0], split[1]);
            }
            parseConfig(newConfig);
            FoodType.rebuild();
            return true;
        }
        return false;
    }

    public static float getDecayMultiplier(FoodType foodType) {
        return decayMultipliers.get(foodType) * GLOBAL_FOOD_DECAY;
    }

    public static int getPlantSpoilAge() {
        return PLANT_SPOIL_AGE;
    }

    public static double getFoodGettingBadPercentage() {
        return FOOD_GETTING_BAD_PERCENTAGE;
    }

    public static double getWorldIcePreservationFactor() {
        return WORLD_ICE_PRESERVATION_FACTOR;
    }

    public static float getAutomaticMergeThreshold() {
        return AUTOMATIC_MERGE_THRESHOLD;
    }

    public static float getCookingSpoilingBonus() {
        return COOKING_SPOILING_BONUS;
    }

    public static int getBaseFreezeTime() {
        return BASE_FREEZE_TIME;
    }

    public static int getAnimalOldAge() {
        return ANIMAL_OLD_AGE * 24000;
    }

    public static int getAnimalDeathAge() {
        return ANIMAL_DEATH_AGE * 24000;
    }

    public static ArrayList<Integer> getSpoilingBlocks() {
        return SPOILING_BLOCKS;
    }

    public static FoodSpoilAddon getInstance() {
        return instance;
    }
}
