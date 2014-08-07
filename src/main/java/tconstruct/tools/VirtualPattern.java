package tconstruct.tools;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.util.IPattern;

import java.util.List;

//A "pattern" in the abstract: For creating tool parts without an explicit pattern item.
public class VirtualPattern implements IPattern
{
    protected final int ourID;
    protected static VirtualPattern[] allPatterns;

    /**
     *
     * @param ID
     * The pattern ID that this specific instance of a pattern object represents.
     */
    VirtualPattern(int ID)
    {
        ourID = ID;
        textureNames = getPatternNames("pattern_");
        folder = "materials/";
        modTexPrefix = "tinker";
    }

    public int getPatternID() { return ourID; };

    public static void InitAll()
    {
        allPatterns = new VirtualPattern[patternName.length];
        for(int i = 0; i < allPatterns.length; ++i)
        {
            allPatterns[i] = new VirtualPattern(i);
        }
    }

    public static VirtualPattern[] getAll()
    {
        return allPatterns;
    }

    private static final String[] patternName = new String[] { "ingot", "rod", "pickaxe", "shovel", "axe", "swordblade", "largeguard", "mediumguard", "crossbar", "binding", "frypan", "sign",
            "knifeblade", "chisel", "largerod", "toughbinding", "largeplate", "broadaxe", "scythe", "excavator", "largeblade", "hammerhead", "fullguard", "bowstring", "fletching", "arrowhead" };

    public String getName()
    {
        return getNameForID(ourID);
    }
    public static String getNameForID(int ID)
    {
        return patternName[ID];
    }

    /* Tags and information about the pattern */
    @SideOnly(Side.CLIENT)
    public String getTooltip (int ID)
    {
        float cost = getPatternCost(ID) / 2f;
        if (cost > 0)
        {
            if (cost - (int) cost < 0.1)
                return StatCollector.translateToLocal("pattern1.tooltip") + (int) cost;
            else
                return StatCollector.translateToLocal("pattern2.tooltip") + cost;
        }
        return null;
    }

    @Override
    public ItemStack getPatternOutput (int patternID, ItemStack input, PatternBuilder.MaterialSet set)
    {
        return TConstructRegistry.getPartMapping(patternID, set.materialID);
    }

    public int getPatternCost() { return getPatternCost(ourID); };
    // This could use a little refactoring, I think.
    // 2 for full material, 1 for half.
    @Override
    public int getPatternCost (int patternID)
    {
        switch (patternID)
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

    public static String[] getPatternNames (String partType)
    {
        String[] names = new String[patternName.length];
        for (int i = 0; i < patternName.length; i++)
            names[i] = partType + patternName[i];
        return names;
    }



    //** Icon stuff here **//

    public String modTexPrefix;
    public String[] textureNames;
    public String[] unlocalizedNames;
    public String folder;
    public IIcon[] icons;

    public void updateData (String[] names, String[] tex, String folder, String modTexturePrefix)
    {
        this.modTexPrefix = modTexturePrefix;
        this.textureNames = tex;
        this.unlocalizedNames = names;
        this.folder = folder;
    }
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromID (int meta)
    {
        int arr = MathHelper.clamp_int(meta, 0, unlocalizedNames.length);
        if(arr > icons.length)
            return icons[0];
        return icons[arr];
    }
    @SideOnly(Side.CLIENT)
    public IIcon getIcon ()
    {
        return getIconFromID(ourID);
    }

        //textureName = modTexPrefix + ":" + folder + textureNames[ID]
}
