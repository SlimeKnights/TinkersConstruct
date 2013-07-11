package mods.tinker.tconstruct.items;

import java.util.List;

import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.library.util.IToolPart;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ToolPart extends CraftingItem implements IToolPart
{
    public ToolPart(int id, String textureType)
    {
        super(id, toolMaterialNames, buildTextureNames(textureType), "parts/");
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

    public static final String[] toolMaterialNames = new String[] { "Wood", "Stone", "Iron", "Flint", "Cactus", "Bone", "Obsidian", "Netherrack", "Slime", "Paper", "Cobalt", "Ardite", "Manyullyn",
            "Copper", "Bronze", "Alumite", "Steel", "Blue Slime", "", "", "", "", "", "", "", "", "", "", "", "", "", "Thaumium" };

    public static final String[] toolTextureNames = new String[] { "wood", "stone", "iron", "flint", "cactus", "bone", "obsidian", "netherrack", "slime", "paper", "cobalt", "ardite", "manyullyn",
            "copper", "bronze", "alumite", "steel", "blueslime", "", "", "", "", "", "", "", "", "", "", "", "", "", "thaumium" };

    public void getSubItems (int id, CreativeTabs tab, List list)
    {
        for (int i = 0; i < 17; i++)
            list.add(new ItemStack(id, 1, i));

        if (TContent.thaumcraftAvailable)
            list.add(new ItemStack(id, 1, 31));
    }

    @Override
    public int getMaterialID (ItemStack stack)
    {
        return stack.getItemDamage();
    }
}
