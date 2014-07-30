package tconstruct.tools.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import tconstruct.tools.TinkerTools;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ToolShard extends ToolPart
{

    public ToolShard(String tex)
    {
        super(tex, "ToolShard");
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IIconRegister iconRegister)
    {
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < 5; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:" + folder + textureNames[i]);
        }
        icons[5] = icons[4];
        for (int i = 6; i < 9; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:" + folder + textureNames[i]);
        }
        icons[9] = icons[8];
        for (int i = 10; i < icons.length; ++i)
        {
            if (!toolTextureNames[i].equals(""))
                this.icons[i] = iconRegister.registerIcon("tinker:" + folder + textureNames[i]);
        }
    }

    @Override
    public void getSubItems (Item b, CreativeTabs tab, List list)
    {
        for (int i = 1; i < 5; i++)
            list.add(new ItemStack(b, 1, i));
        for (int i = 6; i < 9; i++)
            list.add(new ItemStack(b, 1, i));
        for (int i = 10; i < 19; i++)
            list.add(new ItemStack(b, 1, i));

        if (TinkerTools.thaumcraftAvailable)
            list.add(new ItemStack(b, 1, 31));
    }
}
