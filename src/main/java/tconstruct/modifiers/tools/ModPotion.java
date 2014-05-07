package tconstruct.modifiers.tools;

import tconstruct.library.modifier.ItemModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ModPotion extends ItemModifier
{

    public ModPotion(ItemStack[] items, int effect, String dataKey)
    {
        super(new ItemStack[] { new ItemStack(Item.potion, 1, Short.MAX_VALUE) }, 0, "");
    }

    protected boolean canModify (ItemStack tool, ItemStack[] input)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        NBTTagCompound potion = tool.getTagCompound().getCompoundTag("Potion");
        if (potion == null)
            return true;

        return false;
    }

    @Override
    public void modify (ItemStack[] input, ItemStack tool)
    {

    }

}
