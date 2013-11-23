package tconstruct.items;

import java.util.List;

import cpw.mods.fml.relauncher.*;

import tconstruct.library.ItemBlocklike;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class ArmorPattern extends CraftingItem implements ItemBlocklike
{

    private Icon baseIcon;

    public ArmorPattern(int id, String patternType, String folder)
    {
        super(id, patternName, getPatternNames(patternType), folder);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setContainerItem(this);
        this.setMaxStackSize(1);
    }

    protected static String[] getPatternNames (String partType)
    {
        String[] names = new String[patternName.length];
        for (int i = 0; i < patternName.length; i++)
            names[i] = partType + patternName[i];
        return names;
    }

    public Icon getBaseIcon ()
    {
        return baseIcon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons (IconRegister iconRegister)
    {
        super.registerIcons(iconRegister);
        baseIcon = iconRegister.registerIcon("tinker:" + folder + "armor_cast");
    }

    private static final String[] patternName = new String[] { "helmet", "chestplate", "leggings", "boots" };

    public void getSubItems (int id, CreativeTabs tab, List list)
    {
        /*for (int i = 0; i < patternName.length; i++)
        {
            list.add(new ItemStack(id, 1, i));
        }*/
    }

}