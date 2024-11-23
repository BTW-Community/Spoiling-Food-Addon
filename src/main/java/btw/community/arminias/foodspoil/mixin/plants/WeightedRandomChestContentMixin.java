package btw.community.arminias.foodspoil.mixin.plants;


import btw.item.items.SeedFoodItem;
import btw.item.items.SeedItem;
import net.minecraft.src.ItemStack;
import net.minecraft.src.WeightedRandomChestContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WeightedRandomChestContent.class)
public class WeightedRandomChestContentMixin {
    @Redirect(method = "generateChestContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemStack;copy()Lnet/minecraft/src/ItemStack;"))
    private static ItemStack copy(ItemStack itemStack) {
        if (itemStack != null && itemStack.hasTagCompound() && itemStack.stackTagCompound.hasKey("spoilDate")) {
            if (itemStack.getItem() instanceof SeedFoodItem || itemStack.getItem() instanceof SeedItem) {
                ItemStack copy = itemStack.copy();
                copy.stackTagCompound.removeTag("spoilDate");
                if (copy.stackTagCompound.hasKey("creationDate")) {
                    copy.stackTagCompound.removeTag("creationDate");
                }
                return copy;
            } else {
                return itemStack.copy();
            }
        }
        return itemStack != null ? itemStack.copy() : null;
    }
}
