package common.darkknight.jewelrycraft.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMolds extends Item
{
    /** List of molds color names */
    public static final String[] moldsItemNames = new String[] { "ingot", "ring" };
    @SideOnly(Side.CLIENT)
    private Icon[]               moldsIcons;

    public ItemMolds(int par1)
    {
        super(par1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
    }

    @SideOnly(Side.CLIENT)
    /**
     * Gets an icon index based on an item's damage value
     */
    public Icon getIconFromDamage(int par1)
    {
        int j = MathHelper.clamp_int(par1, 0, moldsItemNames.length - 1);
        return this.moldsIcons[j];
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have different names based on
     * their damage or NBT.
     */
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, moldsItemNames.length - 1);
        return super.getUnlocalizedName() + "." + moldsItemNames[i];
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SideOnly(Side.CLIENT)
    /**
     * returns a list of items with the same ID, but different meta (eg: molds returns 16 items)
     */
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int j = 0; j < moldsItemNames.length; ++j)
        {
            par3List.add(new ItemStack(par1, 1, j));
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.moldsIcons = new Icon[moldsItemNames.length];

        for (int i = 0; i < moldsItemNames.length; ++i)
        {
            this.moldsIcons[i] = par1IconRegister.registerIcon("jewelrycraft:" + moldsItemNames[i] + this.getIconString());
        }
    }
}
