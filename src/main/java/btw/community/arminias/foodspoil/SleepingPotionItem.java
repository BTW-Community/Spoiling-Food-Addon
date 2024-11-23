package btw.community.arminias.foodspoil;

import net.minecraft.src.*;

import java.util.Collections;
import java.util.List;

import static net.minecraft.src.PotionHelper.getPotionEffects;

public class SleepingPotionItem extends ItemPotion {

    public final PotionEffect sleepingEffect = new PotionEffect(FoodSpoilAddon.sleeping.id, 3000, 0);
    public final PotionEffect sleepingEffect2 = new PotionEffect(FoodSpoilAddon.sleeping.id, 18000, 1);

    public SleepingPotionItem(int itemID) {
        super(itemID);
        this.setUnlocalizedName("sleepingpotionitem");
        this.setTextureName("potion");
    }

    @Override
    public List getEffects(ItemStack par1ItemStack) {
        List original = super.getEffects(par1ItemStack);
        for (int i = 0; i < original.size(); i++) {
            PotionEffect effect = (PotionEffect) original.get(i);
            if (effect.getPotionID() == FoodSpoilAddon.sleeping.id) {
                original.set(i, doubleDuration(effect));
            }
        }
        return original;
    }

    @Override
    public List getEffects(int par1) {
        List original = super.getEffects(par1);
        for (int i = 0; i < original.size(); i++) {
            PotionEffect effect = (PotionEffect) original.get(i);
            if (effect.getPotionID() == FoodSpoilAddon.sleeping.id) {
                original.set(i, doubleDuration(effect));
            }
        }
        return original;
    }

    private PotionEffect doubleDuration(PotionEffect effect) {
        int duration = effect.getDuration();
        switch (duration) {
            case 3199:
                return new PotionEffect(effect.getPotionID(), 9600 * (1 + effect.getAmplifier()), effect.getAmplifier());
            case 1200:
                return new PotionEffect(effect.getPotionID(), 3600 * (1 + effect.getAmplifier()), effect.getAmplifier());
            default:
                return new PotionEffect(effect.getPotionID(), duration, effect.getAmplifier());
        }
    }

    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
        //super.getSubItems(par1, par2CreativeTabs, par3List);
        ItemStack itemStack = new ItemStack(par1, 1, 27 + 64);
        ItemStack itemStack4 = new ItemStack(par1, 1, 27);
        ItemStack itemStack2 = new ItemStack(par1, 1, 0x4000 + 27 + 64);
        ItemStack itemStack3 = new ItemStack(par1, 1, 0x4000 + 27);

        /*initNBT(itemStack);
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
        itemStack4.getTagCompound().setTag("CustomPotionEffects", list);*/

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
        return itemStack;
    }

    /*@Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 120;
    }*/
}