package tconstruct.items;

import java.util.List;

import cpw.mods.fml.common.Loader;

import tconstruct.common.TContent;
import tconstruct.library.util.IToolPart;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class Bowstring extends CraftingItem implements IToolPart
{
    public Bowstring(int id)
    {
        super(id, toolMaterialNames, buildTextureNames("_bowstring"), "parts/");
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
        return stack.getItemDamage();
    }

    public void getSubItems (int id, CreativeTabs tab, List list)
    {
        list.add(new ItemStack(id, 1, 0));
        if (TContent.thaumcraftAvailable)
            list.add(new ItemStack(id, 1, 1));
        if (Loader.isModLoaded("Natura"))
            list.add(new ItemStack(id, 1, 2));
    }
}
