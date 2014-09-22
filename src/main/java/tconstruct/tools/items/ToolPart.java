package tconstruct.tools.items;

import java.util.List;
import mantle.items.abstracts.CraftingItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import net.minecraft.util.StatCollector;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.util.IToolPart;
import tconstruct.tools.TinkerTools;

@Deprecated
public class ToolPart extends CraftingItem implements IToolPart
{
    public String partName;

    public ToolPart(String textureType, String name)
    {
        super(toolMaterialNames, buildTextureNames(textureType), "parts/", "tinker", TConstructRegistry.partTab);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.partName = name;
    }

    @Override
    public String getItemStackDisplayName (ItemStack par1ItemStack)
    {
        String material = toolTextureNames[par1ItemStack.getItemDamage()];
        String name = "";
        
        if (StatCollector.canTranslate("toolpart." + partName + "." + material))
        {
            name = StatCollector.translateToLocal("toolpart." + partName + "." + material);
        }
        else
        {
            material = StatCollector.translateToLocal("material." + material);
            name = StatCollector.translateToLocal("toolpart." + partName);
            name = name.replaceAll("%%material", material);
        }

        return name;
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

    public static final String[] toolMaterialNames = new String[] { "Wood", "Stone", "Iron", "Flint", "Cactus", "Bone", "Obsidian", "Netherrack", "Slime", "Paper", "Cobalt", "Ardite", "Manyullyn", "Copper", "Bronze", "Zarcon", "Steel", "Blue Slime", "Pig Iron", "", "", "", "", "", "", "", "", "", "", "", "", "Thaumium" };

    public static final String[] toolTextureNames = new String[] { "wood", "stone", "iron", "flint", "cactus", "bone", "obsidian", "netherrack", "slime", "paper", "cobalt", "ardite", "manyullyn", "copper", "bronze", "zarcon", "steel", "blueslime", "pigiron", "", "", "", "", "", "", "", "", "", "", "", "", "thaumium" };

    @Override
    public void getSubItems (Item b, CreativeTabs tab, List list)
    {
        for (int i = 0; i < 19; i++)
            list.add(new ItemStack(b, 1, i));

        if (TinkerTools.thaumcraftAvailable)
            list.add(new ItemStack(b, 1, 31));
    }

    @Override
    public int getMaterialID (ItemStack stack)
    {
        if (TConstructRegistry.toolMaterials.keySet().contains(stack.getItemDamage()))
            return stack.getItemDamage();

        return -1;
    }
}
