package btw.community.arminias.foodspoil.mixin;

import btw.community.arminias.foodspoil.FoodSpoilAddon;
import btw.community.arminias.foodspoil.FoodSpoilMod;
import btw.community.arminias.foodspoil.FoodType;
import btw.community.arminias.foodspoil.Utils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityItem.class)
public abstract class EntityItemMixin extends Entity {

    @Shadow public abstract ItemStack getEntityItem();

    @Shadow public abstract void setEntityItemStack(ItemStack par1ItemStack);

    long lastTick = 0;

    public EntityItemMixin(World par1World) {
        super(par1World);
    }


    @Inject(method = "onUpdate", at = @At("RETURN"))
    private void addDecayWorld(CallbackInfo ci) {
        //EDIT
        if (!this.worldObj.isRemote) {
            ItemStack item = this.getEntityItem();
            NBTTagCompound tag;
            long spoilDate;
            long totalWorldTime = this.worldObj.getTotalWorldTime();
            if (item != null && totalWorldTime % 10 == 0 && (tag = item.getTagCompound()) != null && tag.hasKey("spoilDate") &&
                    (spoilDate = tag.getLong("spoilDate")) > 0 && lastTick != 0) {
                // Check if the item is in or on ice or snow
                Block block = Block.blocksList[this.worldObj.getBlockId((int) this.posX, (int) this.posY, (int) this.posZ)];
                if (Utils.isCoolingBlock(block)) {
                    spoilDate = (long) (spoilDate + (totalWorldTime - lastTick) * FoodSpoilAddon.getWorldIcePreservationFactor());
                    tag.setLong("spoilDate", spoilDate);
                }
                else {
                    for (int i = 0; i < 6; ++i) {
                        if (Utils.isCoolingBlock(Block.blocksList[worldObj.getBlockId(
                                (int) (posX + Facing.offsetsXForSide[i]),
                                (int) (posY + Facing.offsetsYForSide[i]),
                                (int) (posZ + Facing.offsetsZForSide[i]))])) {
                            spoilDate = (long) (spoilDate + (totalWorldTime - lastTick) * FoodSpoilAddon.getWorldIcePreservationFactor());
                            tag.setLong("spoilDate", spoilDate);
                            /*System.out.println("Block: " + Block.blocksList[worldObj.getBlockId(
                                    (int) (posX + Facing.offsetsXForSide[i]),
                                    (int) (posY + Facing.offsetsYForSide[i]),
                                    (int) (posZ + Facing.offsetsZForSide[i]))]);*/
                            break;
                        }
                    }
                }

                if (totalWorldTime > spoilDate) {
                    ItemStack decayItem = FoodType.doItemDecayFast(item);
                    if (decayItem != null) {
                        this.setEntityItemStack(decayItem);
                    } else {
                        this.kill();
                    }

                }
                lastTick = totalWorldTime;
            }
            else if (lastTick == 0) {
                lastTick = totalWorldTime;
            }
        }
    }

    @Inject(method = "combineItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/NBTTagCompound;equals(Ljava/lang/Object;)Z", shift = At.Shift.BEFORE), cancellable = true)
    private void mergeDecayTimers(EntityItem par1EntityItem, CallbackInfoReturnable<Boolean> cir) {
        ItemStack var2 = this.getEntityItem();
        ItemStack var3 = par1EntityItem.getEntityItem();
        if (var2.stackTagCompound.hasKey("spoilDate") && var3.stackTagCompound.hasKey("spoilDate")) {
            Utils.mergeDecayNBTs(var2, var3);
        }
    }
}
