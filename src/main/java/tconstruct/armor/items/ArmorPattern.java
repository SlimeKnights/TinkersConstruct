package tconstruct.armor.items;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import mantle.items.abstracts.CraftingItem;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import net.minecraft.util.IIcon;
import tconstruct.library.*;

public class ArmorPattern extends CraftingItem implements ItemBlocklike
{

    private IIcon baseIcon;

    public ArmorPattern(int id, String patternType, String folder)
    {
        super(patternName, getPatternNames(patternType), folder, "tinker", TConstructRegistry.materialTab);
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

    public IIcon getBaseIcon ()
    {
        return baseIcon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons (IIconRegister iconRegister)
    {
        super.registerIcons(iconRegister);
        baseIcon = iconRegister.registerIcon("tinker:" + folder + "armor_cast");
    }

    private static final String[] patternName = new String[] { "helmet", "chestplate", "leggings", "boots" };

    @Override
    public void getSubItems (Item block, CreativeTabs tab, List list)
    {
        for (int i = 0; i < patternName.length; i++)
        {
            list.add(new ItemStack(block, 1, i));
        }
    }

}