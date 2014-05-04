package tconstruct.modifiers.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.tools.ItemModifier;

public class ModExtraModifier extends ItemModifier
{

    public ModExtraModifier(ItemStack[] items, String dataKey)
    {
        super(items, 0, dataKey);
    }

    @Override
    protected boolean canModify (ItemStack tool, ItemStack[] input)
    {
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
        modifiers += 1;
        tags.setInteger("Modifiers", modifiers);
    }

    @Override
    public void addMatchingEffect (ItemStack tool)
    {
    }
}
