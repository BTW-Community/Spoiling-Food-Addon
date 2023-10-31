package btw.community.arminias.foodspoil;

import btw.BTWAddon;
import btw.block.BTWBlocks;
import btw.crafting.recipe.RecipeManager;
import btw.inventory.util.InventoryUtils;
import btw.item.BTWItems;
import net.minecraft.src.*;

import static net.minecraft.src.Item.FILTERABLE_SMALL;

public class FoodSpoilAddon extends BTWAddon {
    public static Item spoiledCrop;
    public static Item goldenCarrotTest;
    public static Block freezer;
    public static SleepingPotionItem sleepingPotion;
    public static Potion sleeping;

    public FoodSpoilAddon() {
        super("Food Spoil Addon", "0.3.0", "FoodSpoil");
    }

    @Override
    public void initialize() {
        spoiledCrop = new SpoiledCropItem(15673);
        goldenCarrotTest = ( new ItemFood( 15675, 1, 0F, false ) ).setNonBuoyant().setFilterableProperties(FILTERABLE_SMALL).setUnlocalizedName("spoil_test");
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
            throw new RuntimeException("Item ID 2877 is already occupied by " + Item.itemsList[freezerBlockID] + " when adding " + freezer);
        }


    }

    @Override
    public void postInitialize() {
        FoodType.addOverrideItemBasedOnType(Item.itemsList[BTWBlocks.freshPumpkin.blockID], FoodType.VEGETABLE, 4);
        FoodType.addOverrideItemBasedOnType(Item.itemsList[Block.melon.blockID], FoodType.FRUIT, 4);

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
}
