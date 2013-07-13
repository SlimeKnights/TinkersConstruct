package mods.tinker.tconstruct.items;

import mods.tinker.tconstruct.library.util.IToolPart;
import net.minecraft.item.ItemStack;

public class Fletching extends CraftingItem implements IToolPart
{
    public Fletching(int id)
    {
        super(id, toolMaterialNames, buildTextureNames("_fletching"), "parts/");
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

    public static final String[] toolMaterialNames = new String[] { "feather", "leaf", "slime", "blueslime" };

    public static final String[] toolTextureNames = new String[] { "feather", "leaf", "slime", "blueslime" };

    @Override
    public int getMaterialID (ItemStack stack)
    {
        return stack.getItemDamage();
    }
}
