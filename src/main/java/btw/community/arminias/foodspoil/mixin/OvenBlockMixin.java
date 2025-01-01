package btw.community.arminias.foodspoil.mixin;
import btw.block.tileentity.OvenTileEntity;
import btw.community.arminias.foodspoil.Utils;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Redirect;
import btw.block.blocks.OvenBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(OvenBlock.class)
public abstract class OvenBlockMixin {
    @Shadow public abstract boolean isValidCookItem(ItemStack stack);

    @Redirect(method = "onBlockActivated", at = @At(value = "INVOKE", target = "Lbtw/block/tileentity/OvenTileEntity;addCookStack(Lnet/minecraft/src/ItemStack;)V"))
    private void onAddCookStack(OvenTileEntity ovenTileEntity, ItemStack itemStack, World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick) {
        ItemStack item = player.getCurrentEquippedItem();
        if (item != null && this.isValidCookItem(item)) { // Safety in case other addons do something in between
            ItemStack newStack = new ItemStack(item.itemID, 1, item.getItemDamage());
            if (Utils.getPercentageSpoilTimeLeft(newStack, world.getTotalWorldTime()) >= 0.0F) {  // It is a spoiling item and has the tag
                if (newStack.stackTagCompound == null) {
                    newStack.setTagCompound(new NBTTagCompound());
                }
                newStack.stackTagCompound.setLong("spoilDate", item.stackTagCompound.getLong("spoilDate"));
                if (item.stackTagCompound.hasKey("creationDate")) {
                    newStack.stackTagCompound.setLong("creationDate", item.stackTagCompound.getLong("creationDate"));
                }
            }
            ovenTileEntity.addCookStack(newStack);
        }
    }
}
