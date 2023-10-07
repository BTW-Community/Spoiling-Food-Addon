package btw.community.arminias.foodspoil.mixin.additions;

import net.minecraft.src.EntityMob;
import net.minecraft.src.EntityWither;
import net.minecraft.src.PotionEffect;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityWither.class)
public abstract class EntityWitherMixin extends EntityMob {
    public EntityWitherMixin(World par1World) {
        super(par1World);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
        return false;
    }
}
