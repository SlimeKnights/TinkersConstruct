package mods.tinker.tconstruct.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ModReinforced extends ModInteger
{

    public ModReinforced(ItemStack[] items, int effect, int increase)
    {
        super(items, effect, "Reinforced", 1, "\u00a75", "Reinforced");
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
        
        int reinforced = tags.getInteger("Unbreaking");
        reinforced += 1;
        tags.setInteger("Unbreaking", reinforced);
    }
}
