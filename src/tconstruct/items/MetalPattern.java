package tconstruct.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class MetalPattern extends Pattern
{

    public MetalPattern(int id, String partType, String patternType, String folder)
    {
        super(id, partType, patternType, folder);
    }

    protected static String[] getPatternNames (String partType)
    {
        String[] names = new String[patternName.length];
        for (int i = 0; i < patternName.length; i++)
            if (!(patternName[i].equals("")))
                names[i] = partType + patternName[i];
        return names;
    }

    private static final String[] patternName = new String[] { "ingot", "rod", "pickaxe", "shovel", "axe", "swordblade", "largeguard", "mediumguard", "crossbar", "binding", "frypan", "sign",
            "knifeblade", "chisel", "largerod", "toughbinding", "largeplate", "broadaxe", "scythe", "excavator", "largeblade", "hammerhead", "fullguard", "", "", "arrowhead" };

    public void getSubItems (int id, CreativeTabs tab, List list)
    {
        for (int i = 0; i < patternName.length; i++)
            if (!(patternName[i].equals("")))
                list.add(new ItemStack(id, 1, i));
    }
}
