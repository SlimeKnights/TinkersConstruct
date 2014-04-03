package tconstruct.modifiers.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.tools.ToolMod;

public class ModCreativeToolModifier extends ToolMod
{
    public ModCreativeToolModifier(ItemStack[] items)
    {
        super(items, 0, "");
    }

    @Override
    protected boolean canModify (ItemStack tool, ItemStack[] input)
    {
        ToolCore toolItem = (ToolCore) tool.getItem();
        for (ItemStack stack : input)
        {
            if (stack != null && stack.hasTagCompound())
            {
                String targetLock = stack.getTagCompound().getString("TargetLock");
                if (!targetLock.equals("") && !targetLock.equals(toolItem.getToolName()))
                    return false;
            }
        }
        return true;
    }

    @Override
    public void modify (ItemStack[] input, ItemStack tool)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        int modifiers = tags.getInteger("Modifiers");
        modifiers += 1;
        tags.setInteger("Modifiers", modifiers);
    }

    public void addMatchingEffect (ItemStack tool)
    {
    }
}