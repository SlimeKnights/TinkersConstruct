package tconstruct.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tconstruct.library.TConstructRegistry;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;

public class LengthWire extends Item
{
    public String[] textureNames = new String[] { "lengthwire" };
    public String[] unlocalizedNames = new String[] { "lengthwire" };
    public String folder = "logic/";
    public Icon[] icons;


    public LengthWire(int id)
    {
        super(id);
        this.setCreativeTab(TConstructRegistry.toolTab);
        this.maxStackSize = 64;
        this.setHasSubtypes(false);
    }


    @SideOnly(Side.CLIENT)
    public Icon getIconFromDamage (int meta)
    {
        int arr = MathHelper.clamp_int(meta, 0, unlocalizedNames.length);
        return icons[arr];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
        this.icons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            if (!(textureNames[i].equals("")))
                this.icons[i] = iconRegister.registerIcon("tinker:" + folder + textureNames[i]);
        }
    }

    public void getSubItems (int id, CreativeTabs tab, List list)
    {
        for (int i = 0; i < unlocalizedNames.length; i++)
            if (!(textureNames[i].equals("")))
                list.add(new ItemStack(id, 1, i));
    }
}
