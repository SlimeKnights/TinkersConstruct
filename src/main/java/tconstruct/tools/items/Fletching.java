package tconstruct.tools.items;

import mantle.items.abstracts.CraftingItem;
import net.minecraft.item.ItemStack;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.util.IToolPart;

public class Fletching extends CraftingItem implements IToolPart
{
    public Fletching()
    {
        super(toolMaterialNames, buildTextureNames("_fletching"), "parts/", "tinker", TConstructRegistry.materialTab);
        this.setHasSubtypes(true);
        this.setMaxDurability(0);
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

    public static final String[] toolMaterialNames = new String[] { "feather", "leaf", "slime", "blueslime", "slimeleaf" };

    public static final String[] toolTextureNames = new String[] { "feather", "leaf", "slime", "blueslime", "slimeleaf" };

    @Override
    public int getMaterialID (ItemStack stack)
    {
        if (stack.getMetadata() >= toolMaterialNames.length)
            return -1;
        return stack.getMetadata();
    }
}
