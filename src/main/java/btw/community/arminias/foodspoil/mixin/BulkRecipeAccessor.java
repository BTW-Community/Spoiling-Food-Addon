package btw.community.arminias.foodspoil.mixin;

import btw.crafting.recipe.types.BulkRecipe;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(BulkRecipe.class)
public interface BulkRecipeAccessor {
    @Accessor
    List<ItemStack> getRecipeOutputStacks();

    @Accessor
    List<ItemStack> getRecipeInputStacks();

    @Accessor
    boolean isMetadataExclusive();

    @Invoker
    boolean callDoStacksMatch(ItemStack stack1, ItemStack stack2);
}
