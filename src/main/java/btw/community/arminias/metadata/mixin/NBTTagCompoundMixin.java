package btw.community.arminias.metadata.mixin;

import btw.community.arminias.metadata.NBTTagLongArray;
import btw.community.arminias.metadata.extension.NBTTagCompoundExtension;
import net.minecraft.src.CrashReport;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.ReportedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(NBTTagCompound.class)
public abstract class NBTTagCompoundMixin implements NBTTagCompoundExtension {
    @Shadow private Map tagMap;

    @Shadow protected abstract CrashReport createCrashReport(String par1Str, int par2, ClassCastException par3ClassCastException);

    @Override
    public void setLongArray(String par1Str, long[] par2ArrayOfLong) {
        this.tagMap.put(par1Str, new NBTTagLongArray(par1Str, par2ArrayOfLong));
    }

    @Override
    public long[] getLongArray(String par1Str) {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? new long[0] : ((NBTTagLongArray)this.tagMap.get(par1Str)).longArray;
        }
        catch (ClassCastException var3)
        {
            throw new ReportedException(this.createCrashReport(par1Str, 12, var3));
        }
    }
}
