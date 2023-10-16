package btw.community.arminias.foodspoil;

import io.github.minecraftcursedlegacy.api.ModPostInitializer;
import net.fabricmc.api.ModInitializer;

public class FoodSpoilMod implements ModInitializer, ModPostInitializer {

	public static final int ANIMAL_AGE_WATCHER_ID = 27;
	public static final int ANIMAL_OLD_AGE = 100 * 24000;
	public static final int ANIMAL_DEATH_AGE = 2 * ANIMAL_OLD_AGE;
	public static final int PLANT_SPOIL_AGE = 7; // 1 + X days
	public static final double FOOD_GETTING_BAD_PERCENTAGE = 0.15; // 15% of the total time
    public static final double WORLD_ICE_PRESERVATION_FACTOR = 0.8;
	public static final float AUTOMATIC_MERGE_THRESHOLD = 0.15F;


    @Override
	public void onPostInitialize() {

	}

	@Override
	public void onInitialize() {

	}
}
