package tconstruct.items;

import java.util.List;

import mantle.items.abstracts.CraftingItem;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import tconstruct.common.TContent;
import tconstruct.common.TRepo;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.util.IToolPart;

public class ToolPart extends CraftingItem implements IToolPart
{
    public String partName;

    public ToolPart(String textureType, String name)
    {
        super(toolMaterialNames, buildTextureNames(textureType), "parts/", "tinker", TConstructRegistry.materialTab);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.partName = name;
    }

    @Override
    public String func_150896_i (ItemStack par1ItemStack)
    {
        String material = StatCollector.translateToLocal("parttype." + toolTextureNames[par1ItemStack.getItemDamage()]);
        String name = StatCollector.translateToLocal("toolpart." + partName);
        name = name.replaceAll("%%material", material);

        return name;

        /*StringBuilder builder = new StringBuilder();
        builder.append(StatCollector.translateToLocal("parttype." + toolTextureNames[par1ItemStack.getItemDamage()])).append(" ").append(StatCollector.translateToLocal("toolpart." + partName));
        return builder.toString();
        --
        String s = ("" + StatCollector.translateToLocal("toolpart."+partName)).trim();
        String s1 = toolMaterialNames[par1ItemStack.getItemDamage()];

        if (s1 != null)
        {
            s = s + " " + StatCollector.translateToLocal(s1);
        }

        return s;*/
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

    public static final String[] toolMaterialNames = new String[] { "Wood", "Stone", "Iron", "Flint", "Cactus", "Bone", "Obsidian", "Netherrack", "Slime", "Paper", "Cobalt", "Ardite", "Manyullyn",
            "Copper", "Bronze", "Alumite", "Steel", "Blue Slime", "Pig Iron", "", "", "", "", "", "", "", "", "", "", "", "", "Thaumium" };

    public static final String[] toolTextureNames = new String[] { "wood", "stone", "iron", "flint", "cactus", "bone", "obsidian", "netherrack", "slime", "paper", "cobalt", "ardite", "manyullyn",
            "copper", "bronze", "alumite", "steel", "blueslime", "pigiron", "", "", "", "", "", "", "", "", "", "", "", "", "thaumium" };

    @Override
    public void getSubItems (Block b, CreativeTabs tab, List list)
    {
        for (int i = 0; i < 19; i++)
            list.add(new ItemStack(b, 1, i));

        if (TRepo.thaumcraftAvailable)
            list.add(new ItemStack(b, 1, 31));
    }

    @Override
    public int getMaterialID (ItemStack stack)
    {
        return stack.getItemDamage();
    }
}
