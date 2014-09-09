package tconstruct.tools.items;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import net.minecraft.util.IIcon;
import tconstruct.tools.TinkerTools;

public class ToolShard extends ToolPart
{

    public ToolShard(String tex)
    {
        super(tex, "ToolShard");
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    private static String[] buildTextureNames (String textureType)
    {
        String[] names = new String[toolMaterialNames.length];
        for (int i = 0; i < toolMaterialNames.length; i++)
        {
            if (!toolTextureNames[i].equals(""))
                names[i] = toolTextureNames[i] + textureType;
        }
        return names;
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
