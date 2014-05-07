package tconstruct.modifiers.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.modifier.ItemModifier;
import tconstruct.library.tools.ToolCore;

public class ModExtraModifier extends ItemModifier
{
    public ModExtraModifier(ItemStack[] items, String dataKey)
    {
        super(items, 0, dataKey);
    }

    @Override
    protected boolean canModify (ItemStack tool, ItemStack[] input)
    {
        if (tool != null && tool.getItem() instanceof ToolCore)
        {
            NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
            if (tags.getBoolean(key))
            {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void modify (ItemStack[] input, ItemStack tool)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        tags.setBoolean(key, true);
        int modifiers = tags.getInteger("Modifiers");
        modifiers += 1;
        tags.setInteger("Modifiers", modifiers);
    }

    public void addMatchingEffect (ItemStack tool)
    {
    }
}
