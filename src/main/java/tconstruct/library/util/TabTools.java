package tconstruct.library.util;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TabTools extends CreativeTabs
{
    ItemStack display;

    public TabTools(String label)
    {
        super(label);
    }

    public void init (ItemStack stack)
    {
        display = stack;
    }

    @Override
    public ItemStack getIconItemStack ()
    {
        return display;
    }

    @Override
    public Item getTabIconItem ()
    {
        return display.getItem();
    }
}