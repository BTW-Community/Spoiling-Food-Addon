package btw.community.arminias.metadata.extension;

import net.minecraft.src.NBTTagCompound;

public interface NBTTagCompoundExtension {
    /**
     * Stores a new NBTTagLongArray with the given array as data into the map with the given string key.
     */
    void setLongArray(String par1Str, long[] par2ArrayOfLong);
    long[] getLongArray(String par1Str);

    static NBTTagCompoundExtension cast(NBTTagCompound nbtTagCompound) {
        return (NBTTagCompoundExtension) nbtTagCompound;
    }
}
