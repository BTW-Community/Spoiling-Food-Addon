package btw.community.arminias.metadata.mixin;

import btw.community.arminias.metadata.NBTTagLongArray;
import net.minecraft.src.NBTBase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NBTBase.class)
public abstract class NBTBaseMixin {
    @Final
    @Shadow
    @Mutable
    public static String[] NBTTypes;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void initInject(CallbackInfo cir) {
        NBTTypes = new String[] {"END", "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE", "BYTE[]", "STRING", "LIST", "COMPOUND", "INT[]", "LONG[]"};
    }

    @Inject(method = "newTag", at = @At(value = "RETURN", ordinal = 12), cancellable = true)
    private static void newTag(byte par0, String par1Str, CallbackInfoReturnable<NBTBase> cir) {
        if (par0 == 12) {
            cir.setReturnValue(new NBTTagLongArray(par1Str));
        }
    }

    @Inject(method = "getTagName", at = @At(value = "RETURN", ordinal = 12), cancellable = true)
    private static void getTagName(byte par0, CallbackInfoReturnable<String> cir) {
        if (par0 == 12) {
            cir.setReturnValue("TAG_Long_Array");
        }
    }
}
