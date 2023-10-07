package btw.community.arminias.metadata;

import net.minecraft.src.NBTBase;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagLongArray extends NBTBase
{
    /** The array of saved longs */
    public long[] longArray;

    public NBTTagLongArray(String par1Str)
    {
        super(par1Str);
    }

    public NBTTagLongArray(String par1Str, long[] par2ArrayOfLong)
    {
        super(par1Str);
        this.longArray = par2ArrayOfLong;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    protected void write(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.longArray.length);

        for (int var2 = 0; var2 < this.longArray.length; ++var2)
        {
            par1DataOutput.writeLong(this.longArray[var2]);
        }
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes
     */
    protected void load(DataInput par1DataInput) throws IOException
    {
        int var2 = par1DataInput.readInt();
        this.longArray = new long[var2];

        for (int var3 = 0; var3 < var2; ++var3)
        {
            this.longArray[var3] = par1DataInput.readLong();
        }
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return (byte)12;
    }

    public String toString()
    {
        return "[" + this.longArray.length + " bytes]";
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy()
    {
        long[] var1 = new long[this.longArray.length];
        System.arraycopy(this.longArray, 0, var1, 0, this.longArray.length);
        return new NBTTagLongArray(this.getName(), var1);
    }

    public boolean equals(Object par1Obj)
    {
        if (!super.equals(par1Obj))
        {
            return false;
        }
        else
        {
            NBTTagLongArray var2 = (NBTTagLongArray)par1Obj;
            return this.longArray == null && var2.longArray == null || this.longArray != null && Arrays.equals(this.longArray, var2.longArray);
        }
    }

    public int hashCode()
    {
        return super.hashCode() ^ Arrays.hashCode(this.longArray);
    }
}
