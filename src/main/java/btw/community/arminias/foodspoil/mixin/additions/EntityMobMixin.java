package btw.community.arminias.foodspoil.mixin.additions;

import btw.community.arminias.foodspoil.FoodSpoilAddon;
import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityMob;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityMob.class)
public abstract class EntityMobMixin extends EntityLiving {
    public EntityMobMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "entityMobAttackEntityFrom", at = @At("HEAD"))
    private void entityMobAttackEntityFrom(DamageSource par1DamageSource, int par2, CallbackInfoReturnable<Boolean> cir) {
        if (this.isPotionActive(FoodSpoilAddon.sleeping) && this.getActivePotionEffect(FoodSpoilAddon.sleeping).getAmplifier() < 1) {
            this.removePotionEffect(FoodSpoilAddon.sleeping.id);
        }
    }
}
