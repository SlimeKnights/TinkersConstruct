package tconstruct.library.armor;

import java.util.EnumSet;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/* Adds an integer NBTTag */

public class AModInteger extends ArmorMod
{
    public final int modifyCount;
    public final int amount;
    public final String color;
    public final String tooltipName;

    public AModInteger(int effect, String dataKey, EnumSet<EnumArmorPart> armorTypes, ItemStack[] items, int count, int increase, String c, String tip)
    {
        super(effect, dataKey, armorTypes, items);
        this.modifyCount = count;
        this.amount = increase;
        color = c;
        tooltipName = tip;
    }

    @Override
    protected boolean canModify (ItemStack armor, ItemStack[] input)
    {
        ArmorCore item = (ArmorCore) armor.getItem();
        if (armorTypes.contains(item.armorPart))
        {
            NBTTagCompound tags = armor.getTagCompound().getCompoundTag(getTagName());
            return tags.getInteger("Modifiers") >= modifyCount;
        }
        return false;
    }

    @Override
    public void modify (ItemStack[] input, ItemStack tool)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag(getTagName());
        if (tags.hasKey(key))
        {
            int increase = tags.getInteger(key);
            increase += this.amount;
            tags.setInteger(key, increase);
        }
        else
        {
            tags.setInteger(key, amount);
        }

        int modifiers = tags.getInteger("Modifiers");
        modifiers -= modifyCount;
        tags.setInteger("Modifiers", modifiers);

        addToolTip(tool, color + tooltipName, color + key);
    }

}