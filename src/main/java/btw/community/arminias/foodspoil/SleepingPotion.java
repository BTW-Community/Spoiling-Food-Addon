package btw.community.arminias.foodspoil;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.Potion;

public class SleepingPotion extends Potion {
    public SleepingPotion(int par1, boolean par2, int par3) {
        super(par1, par2, par3);
    }

    @Override
    public boolean isReady(int par1, int par2) {
        return false;
    }

    @Override
    public void affectEntity(EntityLiving par1EntityLiving, EntityLiving par2EntityLiving, int par3, double par4) {
        super.affectEntity(par1EntityLiving, par2EntityLiving, par3, par4);
    }

    @Override
    public void performEffect(EntityLiving par1EntityLiving, int par2) {
        super.performEffect(par1EntityLiving, par2);
    }

    @Override
    public int getLiquidColor() {
        return super.getLiquidColor();
    }
}
