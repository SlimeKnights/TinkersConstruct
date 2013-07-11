package mods.tinker.tconstruct.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.library.util.IToolPart;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;

public class Bowstring extends CraftingItem implements IToolPart
{
    public Bowstring(int id)
    {
        super(id, toolMaterialNames, buildTextureNames("_bowstring"), "parts/");
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    private static String[] buildTextureNames (String textureType)
    {
        String[] names = new String[toolMaterialNames.length];
        for (int i = 0; i < toolMaterialNames.length; i++)
        {
            if (toolTextureNames[i].equals(""))
                names[i] = "";
            else
                names[i] = toolTextureNames[i] + textureType;
        }
        return names;
    }

    public static final String[] toolMaterialNames = new String[] { "string", "enchantedfabric" };

    public static final String[] toolTextureNames = new String[] { "string", "magicfabric" };

    @Override
    public int getMaterialID (ItemStack stack)
    {
        return stack.getItemDamage();
    }

    public void getSubItems (int id, CreativeTabs tab, List list)
    {
        list.add(new ItemStack(id, 1, 0));
        if (TContent.thaumcraftAvailable)
            list.add(new ItemStack(id, 1, 1));
    }
}
