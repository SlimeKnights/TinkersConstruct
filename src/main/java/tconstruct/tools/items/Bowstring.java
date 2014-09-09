package tconstruct.tools.items;

import cpw.mods.fml.common.Loader;
import java.util.List;
import mantle.items.abstracts.CraftingItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.util.IToolPart;
import tconstruct.tools.TinkerTools;

public class Bowstring extends CraftingItem implements IToolPart
{
    public Bowstring()
    {
        super(toolMaterialNames, buildTextureNames("_bowstring"), "parts/", "tinker", TConstructRegistry.materialTab);
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

    public static final String[] toolMaterialNames = new String[] { "string", "enchantedfabric", "flamestring" };

    public static final String[] toolTextureNames = new String[] { "string", "magicfabric", "flamestring" };

    @Override
    public int getMaterialID (ItemStack stack)
    {
        if (stack.getItemDamage() >= toolMaterialNames.length)
            return -1;
        return stack.getItemDamage();
    }

    @Override
    public void getSubItems (Item b, CreativeTabs tab, List list)
    {
        list.add(new ItemStack(b, 1, 0));
        if (TinkerTools.thaumcraftAvailable)
            list.add(new ItemStack(b, 1, 1));
        if (Loader.isModLoaded("Natura"))
            list.add(new ItemStack(b, 1, 2));
    }
}
