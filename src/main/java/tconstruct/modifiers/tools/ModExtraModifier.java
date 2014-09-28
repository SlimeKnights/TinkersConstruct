package tconstruct.modifiers.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.modifier.*;

public class ModExtraModifier extends ItemModifier
{
    public ModExtraModifier(ItemStack[] items, String dataKey)
    {
        super(items, 0, dataKey);
    }

    @Override
    protected boolean canModify (ItemStack tool, ItemStack[] recipe)
    {
        if (tool != null && tool.getItem() instanceof IModifyable)
        {
            NBTTagCompound tags = this.getModifierTag(tool);
            if (tags.getBoolean(key))
            {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void modify (ItemStack[] recipe, ItemStack input)
    {
        NBTTagCompound tags = this.getModifierTag(input);
        tags.setBoolean(key, true);
        int modifiers = tags.getInteger("Modifiers");
        modifiers += 1;
        tags.setInteger("Modifiers", modifiers);
    }

    public void addMatchingEffect (ItemStack tool)
    {
    }
}
