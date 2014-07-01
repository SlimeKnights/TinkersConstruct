package tconstruct.modifiers.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.modifier.ItemModifier;

/* Adds a boolean NBTTag */

public class ModBoolean extends ItemModifier
{
    String color;
    String tooltipName;

    public ModBoolean(ItemStack[] items, int effect, String tag, String c, String tip)
    {
        super(items, effect, tag);
        color = c;
        tooltipName = tip;
    }

    @Override
    protected boolean canModify (ItemStack tool, ItemStack[] input)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        return tags.getInteger("Modifiers") > 0 && !tags.getBoolean(key); //Will fail if the modifier is false or the tag doesn't exist
    }

    @Override
    public void modify (ItemStack[] input, ItemStack tool)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");

        tags.setBoolean(key, true);

        int modifiers = tags.getInteger("Modifiers");
        modifiers -= 1;
        tags.setInteger("Modifiers", modifiers);

        addToolTip(tool, color + tooltipName, color + key);
    }
}
