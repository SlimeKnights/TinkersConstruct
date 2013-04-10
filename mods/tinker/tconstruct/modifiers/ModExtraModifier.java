package mods.tinker.tconstruct.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import mods.tinker.common.ToolMod;
import mods.tinker.tconstruct.crafting.PatternBuilder;

public class ModExtraModifier extends ToolMod
{

    public ModExtraModifier(ItemStack[] items, String dataKey)
    {
        super(items, 0, dataKey);
    }

    @Override
    protected boolean canModify (ItemStack tool, ItemStack[] input)
    {
        if ((input[0] == null && input[1] == null) || (input[0] != null && input[1] != null)) //Only valid for one itemstack
            return false;

        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        if (tags.getBoolean(key))
        {
            return false;
        }
        return true;
    }

    @Override
    public void modify (ItemStack[] input, ItemStack tool)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        tags.setBoolean(key, true);
        int modifiers = tags.getInteger("Modifiers");
        tags.setInteger("Modifiers", modifiers + 1);
    }

}
