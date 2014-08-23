package tconstruct.library;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TConstructCreativeTab extends CreativeTabs
{
    ItemStack display;

    public TConstructCreativeTab(String label)
    {
        super(label);
    }

    public void init (ItemStack stack)
    {
        display = stack;
    }

    public ItemStack getIconItemStack ()
    {
        return display;
    }

    public Item getTabIconItem ()
    {
        return display.getItem();
    }
}