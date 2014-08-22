package tconstruct.tools;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.util.IPattern;

import java.util.Arrays;

//A "pattern" in the abstract: For creating tool parts without an explicit pattern item.
public class VirtualPattern implements IPattern
{
    //Somewhat of a hack, needs some cleaning up.
    protected final int ourID;
    protected static VirtualPattern[] allPatterns;
    protected String textureName;

    /**
     *
     * @param ID
     * The pattern ID that this specific instance of a pattern object represents.
     */
    VirtualPattern(int ID)
    {
        ourID = ID;
        //textureFolder = "materials/";
        textureFolder = "textures/items/materials/";
        modTexPrefix = "tinker";
    }
    VirtualPattern(int ID, String fold, String modname)
    {
        ourID = ID;
        textureFolder = fold;
        modTexPrefix = modname;
    }

    public int getPatternID() { return ourID; };

    public static void InitAll()
    {
        allPatterns = new VirtualPattern[patternNames.length];
        if(textureNames == null)
        {
            textureNames = getPatternNames("pattern_");
        }
        for(int i = 0; i < allPatterns.length; ++i)
        {
            allPatterns[i] = new VirtualPattern(i);
        }
    }

    public static VirtualPattern[] getAll()
    {
        return allPatterns;
    }

    private static String[] patternNames = new String[] { "ingot", "rod", "pickaxe", "shovel", "axe", "swordblade", "largeguard", "mediumguard", "crossbar", "binding", "frypan", "sign",
            "knifeblade", "chisel", "largerod", "toughbinding", "largeplate", "broadaxe", "scythe", "excavator", "largeblade", "hammerhead", "fullguard", "bowstring", "fletching", "arrowhead" };

    public String getName()
    {
        return getNameForID(ourID);
    }
    public static String getNameForID(int ID)
    {
        return patternNames[ID];
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
        String[] names = new String[patternNames.length];
        for (int i = 0; i < patternNames.length; i++)
            names[i] = partType + patternNames[i];
        return names;
    }



    //** Icon stuff here **//

    public String modTexPrefix;
    protected static String[] textureNames;
    public static String[] unlocalizedNames;
    public String textureFolder;
    public IIcon[] icons;

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

        //textureName = modTexPrefix + ":" + textureFolder + textureNames[ID]

    public String getTextureName ()
    {
        if(textureName != null)
        {
            return textureName;
        }
        else
        {
            return textureNames[this.getPatternID()];
        }
    }

    public void setTextureName (String tN)
    {
        this.textureName = tN;
    }

    public String getTextureFolder ()
    {
        return textureFolder;
    }

    public void setTextureFolder (String tF)
    {
        this.textureFolder = tF;
    }

    public String getModTexPrefix ()
    {
        return modTexPrefix;
    }

    public void setModTexPrefix (String mTP)
    {
        this.modTexPrefix = mTP;
    }
    public static void add (String name, VirtualPattern toAdd)
    {
        patternNames = Arrays.copyOf(patternNames, patternNames.length+1);
        patternNames[patternNames.length] = name;
        if(textureNames != null)
        {
            textureNames = Arrays.copyOf(textureNames, textureNames.length+1);
        }
        InitAll();
        textureNames[patternNames.length] = toAdd.getTextureName();
        allPatterns[allPatterns.length] = toAdd;
    }
}
