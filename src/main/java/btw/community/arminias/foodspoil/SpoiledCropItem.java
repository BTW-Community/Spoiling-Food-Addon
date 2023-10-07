package btw.community.arminias.foodspoil;

import btw.item.items.FoodItem;
import btw.item.items.RottenFleshItem;
import net.minecraft.src.Item;

public class SpoiledCropItem extends FoodItem {
    public SpoiledCropItem(int iItemID )
    {
        super(iItemID, 3, 0F, false, "spoiledcropitem");

        setIncreasedFoodPoisoningEffect();
        setAsBasicHerbivoreFood();
        setAsBasicPigFood();

    }
}
