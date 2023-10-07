package btw.community.arminias.foodspoil.mixin;

import btw.community.arminias.foodspoil.FoodType;
import btw.community.arminias.foodspoil.Utils;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Item.class)
public abstract class ItemMixin {
    public ItemMixin() {
        super();
    }

    @Inject(method = "addInformation", at = @At(value = "HEAD"))
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4, CallbackInfo ci) {
        FoodType foodType = FoodType.getFoodTypeFast(itemStack.itemID);
        if (foodType != null && itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("spoilDate")) {
            if (foodType != FoodType.UNSPOILABLE) {
                list.add("Expires in: " +
                        Utils.formatTimeWindow((itemStack.getTagCompound().getLong("spoilDate") - player.worldObj.getTotalWorldTime())
                                / 20, Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)));
            } else {
                list.add("Never expires.");
            }
            // Debug
            //list.add(itemStack.getTagCompound().toString());
        }
    }
}
