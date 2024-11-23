package btw.community.arminias.foodspoil;

import net.fabricmc.api.ModInitializer;
import org.spongepowered.asm.mixin.Unique;

public class FoodSpoilMod implements ModInitializer {

	public static final int ANIMAL_SPAWNED_AT_WATCHER_ID = 27;
	public static final int SPAWNED_AT_QUANTIZATION_SHIFT = 8;
	public static final int SPOIL_TIME_QUANTIZATION_SHIFT = 8;

	@Override
	public void onInitialize() {

	}
}
