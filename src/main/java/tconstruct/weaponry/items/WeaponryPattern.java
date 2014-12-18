package tconstruct.weaponry.items;

import tconstruct.util.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tconstruct.tools.items.Pattern;

import java.util.List;

public class WeaponryPattern extends Pattern {
    private static final String[] patternName = new String[] { "shuriken", "crossbowlimb", "crossbowbody", "bowlimb" };

    public WeaponryPattern(String patternType, String name) {
        super(patternName, getPatternNames(patternName, patternType), "patterns/");

        this.setUnlocalizedName(Reference.prefix(name));
    }

    public static String[] getPatternNames (String[] patternName, String partType)
    {
        String[] names = new String[patternName.length];
        for (int i = 0; i < patternName.length; i++)
            names[i] = partType + patternName[i];
        return names;
    }

    @Override
    public void getSubItems (Item b, CreativeTabs tab, List list)
    {
        for (int i = 0; i < patternName.length; i++)
        {
            // if (i != 23)
            list.add(new ItemStack(b, 1, i));
        }
    }

    @Override
    public int getPatternCost(ItemStack pattern) {
        switch(pattern.getItemDamage())
        {
            case 0: return 1; // shuriken
            case 1: return 8; // crossbow limb
            case 2: return 10; // crossbow body
            case 3: return 3; // bowlimb
        }
        return 0;
    }
}
