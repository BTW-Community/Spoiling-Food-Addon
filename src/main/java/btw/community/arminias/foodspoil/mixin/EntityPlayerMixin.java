package btw.community.arminias.foodspoil.mixin;

import btw.community.arminias.foodspoil.FoodType;
import btw.community.arminias.foodspoil.Utils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLiving {

    public EntityPlayerMixin(World par1World) {
        super(par1World);
    }

    @Shadow public InventoryPlayer inventory;

    @Shadow public abstract void addChatMessage(String par1Str);

    @Inject(method = "onUpdate", at=@At("HEAD"))
    private void addDecayInventory(CallbackInfo ci) {
        if (!this.worldObj.isRemote) {
            long worldTime = this.worldObj.getTotalWorldTime();
            for (int i = 0; i < this.inventory.mainInventory.length; i++) {
                ItemStack item = this.inventory.mainInventory[i];
                if (Utils.isBeyondSpoilDate(item, worldTime)) {
                    this.addChatMessage(item.stackSize + " " + item.getDisplayName() + " just rotted away!");
                    this.inventory.mainInventory[i] = FoodType.doItemDecayFast(item);
                }
            }
        }
    }
}
