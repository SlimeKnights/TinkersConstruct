package tconstruct.tools.items;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import mantle.items.abstracts.CraftingItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.StatCollector;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.PatternBuilder.MaterialSet;
import tconstruct.library.util.IPattern;

public class Pattern extends CraftingItem implements IPattern
{
    public Pattern(String patternType, String folder)
    {
        this(patternName, getPatternNames(patternType), folder);
    }

    public Pattern(String[] names, String[] patternTypes, String folder)
    {
        super(names, patternTypes, folder, "tinker", TConstructRegistry.materialTab);
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

    private static final String[] patternName = new String[] { "ingot", "rod", "pickaxe", "shovel", "axe", "swordblade", "largeguard", "mediumguard", "crossbar", "binding", "frypan", "sign", "knifeblade", "chisel", "largerod", "toughbinding", "largeplate", "broadaxe", "scythe", "excavator", "largeblade", "hammerhead", "fullguard", "bowstring", "fletching", "arrowhead" };

    @Override
    public void getSubItems (Item b, CreativeTabs tab, List list)
    {
        for (int i = 1; i < patternName.length; i++)
        {
            // if (i != 23)
            list.add(new ItemStack(b, 1, i));
        }
    }

    @Override
    public ItemStack getContainerItem (ItemStack stack)
    {
        if (stack.stackSize <= 0)
            return null;
        return stack;
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid (ItemStack stack)
    {
        return false;
    }

    /* Tags and information about the pattern */
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        float cost = getPatternCost(stack) / 2f;
        if (cost > 0)
        {
            if (cost - (int) cost < 0.1)
                list.add(StatCollector.translateToLocal("pattern1.tooltip") + (int) cost);
            else
                list.add(StatCollector.translateToLocal("pattern2.tooltip") + cost);
        }
    }

    // 2 for full material, 1 for half.
    @Override
    public int getPatternCost (ItemStack pattern)
    {
        switch (pattern.getItemDamage())
        {
        case 0:
            return 2;
        case 1:
            return 1;
        case 2:
            return 2;
        case 3:
            return 2;
        case 4:
            return 2;
        case 5:
            return 2;
        case 6:
            return 1;
        case 7:
            return 1;
        case 8:
            return 1;
        case 9:
            return 1;
        case 10:
            return 2;
        case 11:
            return 2;
        case 12:
            return 1;
        case 13:
            return 1;
        case 14:
            return 6;
        case 15:
            return 6;
        case 16:
            return 16;
        case 17:
            return 16;
        case 18:
            return 16;
        case 19:
            return 16;
        case 20:
            return 16;
        case 21:
            return 16;
        case 22:
            return 6;
        case 23:
            return 6;
        case 24:
            return 2;
        case 25:
            return 2;
        default:
            return 0;
        }
    }

    @Override
    public ItemStack getPatternOutput (ItemStack stack, ItemStack input, MaterialSet set)
    {
        return TConstructRegistry.getPartMapping((Item) this, stack.getItemDamage(), set.materialID);
    }
}
