package tconstruct.modifiers.tools;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.modifier.ItemModifier;

public class ModPotion extends ItemModifier
{

    public ModPotion(ItemStack[] items, int effect, String dataKey)
    {
        super(new ItemStack[] { new ItemStack(Items.potionitem, 1, Short.MAX_VALUE) }, 0, "");
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
