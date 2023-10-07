package btw.community.arminias.foodspoil;

import btw.item.items.PotionItem;
import net.minecraft.src.*;

import java.util.List;

public class SleepingPotionItem extends PotionItem {

    public final PotionEffect sleepingEffect = new PotionEffect(FoodSpoilAddon.sleeping.id, 3000, 0);
    public final PotionEffect sleepingEffect2 = new PotionEffect(FoodSpoilAddon.sleeping.id, 18000, 1);

    public SleepingPotionItem(int itemID) {
        super(itemID);
        this.setUnlocalizedName("sleepingpotionitem");
    }

    @Override
    public List getEffects(ItemStack par1ItemStack) {
        return super.getEffects(par1ItemStack);
    }

    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
        //super.getSubItems(par1, par2CreativeTabs, par3List);
        ItemStack itemStack = new ItemStack(par1, 1, 27);
        ItemStack itemStack4 = new ItemStack(par1, 1, 27);
        ItemStack itemStack2 = new ItemStack(par1, 1, 0x4000 + 27);
        ItemStack itemStack3 = new ItemStack(par1, 1, 0x4000 + 27);

        initNBT(itemStack);
        NBTTagList list = new NBTTagList();
        list.appendTag(sleepingEffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
        itemStack.getTagCompound().setTag("CustomPotionEffects", list);

        initNBT(itemStack2);
        list = new NBTTagList();
        list.appendTag(sleepingEffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
        itemStack2.getTagCompound().setTag("CustomPotionEffects", list);

        initNBT(itemStack3);
        list = new NBTTagList();
        list.appendTag(sleepingEffect2.writeCustomPotionEffectToNBT(new NBTTagCompound()));
        itemStack3.getTagCompound().setTag("CustomPotionEffects", list);

        initNBT(itemStack4);
        list = new NBTTagList();
        list.appendTag(sleepingEffect2.writeCustomPotionEffectToNBT(new NBTTagCompound()));
        itemStack4.getTagCompound().setTag("CustomPotionEffects", list);

        par3List.add(itemStack);
        par3List.add(itemStack2);
        par3List.add(itemStack3);
        par3List.add(itemStack4);
    }

    private static void initNBT(ItemStack itemStack) {
        if (itemStack.getTagCompound() == null) {
            itemStack.setTagCompound(new NBTTagCompound("tag"));
        }
    }

    public ItemStack createNormalPotion() {

        ItemStack itemStack = new ItemStack(this, 1, FoodSpoilAddon.sleeping.getId());
        initNBT(itemStack);
        NBTTagList list = new NBTTagList();
        list.appendTag(sleepingEffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
        itemStack.getTagCompound().setTag("CustomPotionEffects", list);
        return itemStack;
    }

    /*@Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 120;
    }*/
}