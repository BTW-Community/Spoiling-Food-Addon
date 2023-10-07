package btw.community.arminias.foodspoil.mixin;

import net.minecraft.src.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntityPlayerSP.class)
public abstract class EntityPlayerSPMixin {

	@Shadow public abstract void setSprinting(boolean par1);

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerSP;setSprinting(Z)V", shift = At.Shift.AFTER, ordinal = 1), method = "onLivingUpdate", locals = LocalCapture.CAPTURE_FAILSOFT)
	private void disableWW(CallbackInfo ci, boolean var1, float var2, boolean var3, boolean wasHoldingSpecial, boolean var4, boolean activatedSprint) {
		setSprinting(false);
	}
}
