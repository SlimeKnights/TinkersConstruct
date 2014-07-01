package tconstruct.modifiers.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.modifier.ItemModifier;

/* Adds an integer NBTTag */

public class ModInteger extends ItemModifier
{
    String color;
    String tooltipName;
    int initialIncrease;
    int secondaryIncrease;

    public ModInteger(ItemStack[] items, int effect, String dataKey, int increase, String c, String tip)
    {
        super(items, effect, dataKey);
        initialIncrease = secondaryIncrease = increase;
        color = c;
        tooltipName = tip;
    }

    public ModInteger(ItemStack[] items, int effect, String dataKey, int increase1, int increase2, String c, String tip)
    {
        super(items, effect, dataKey);
        initialIncrease = increase1;
        secondaryIncrease = increase2;
        color = c;
        tooltipName = tip;
    }

    @Override
    public void modify (ItemStack[] input, ItemStack tool)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        if (tags.hasKey(key))
        {
            int increase = tags.getInteger(key);
            increase += secondaryIncrease;
            tags.setInteger(key, increase);
        }
        else
        {
            tags.setInteger(key, initialIncrease);
        }

        int modifiers = tags.getInteger("Modifiers");
        modifiers -= 1;
        tags.setInteger("Modifiers", modifiers);

        addToolTip(tool, color + tooltipName, color + key);
    }

}
