package btw.community.arminias.foodspoil.mixin.additions;

import btw.community.arminias.foodspoil.FoodSpoilAddon;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import btw.community.arminias.foodspoil.Utils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFood.class)
public class ItemFoodMixin {
    @Inject(method = "onEaten", at = @At("HEAD"))
    private void onEatenInject(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer, CallbackInfoReturnable<ItemStack> cir) {
        //System.out.println("ItemFoodMixin.onEatenInject");
        float p = Utils.getPercentageSpoilTimeLeft(par1ItemStack, par2World.getTotalWorldTime());
        if (p > 0 && p <= FoodSpoilAddon.getFoodGettingBadPercentage()) {
            if (par3EntityPlayer.isPotionActive(FoodSpoilAddon.sleeping.id) && par3EntityPlayer.isPotionActive(Potion.confusion.getId())) {
                par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.blindness.getId(), 1200, 0, false));
            }
            if (par3EntityPlayer.isPotionActive(FoodSpoilAddon.sleeping.id)) {
                par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.confusion.getId(), 1200, 0, false));
            }
            par3EntityPlayer.addPotionEffect(new PotionEffect(FoodSpoilAddon.sleeping.id, 3000, 0));
        }
    }

}
