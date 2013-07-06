package mods.tinker.tconstruct.items.tools;

import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.library.tools.BowBase;
import net.minecraft.item.Item;

public class Shortbow extends BowBase
{
    public Shortbow(int itemID)
    {
        super(itemID);
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
        case 0:
            return "_bow_top";
        case 1:
            return "_bowstring_broken";
        case 2:
            return "_bowstring";
        case 3:
            return "_bow_bottom";
        default:
            return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_bow_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "shortbow";
    }

    @Override
    public Item getHeadItem ()
    {
        return TContent.toolRod;
    }
    
    @Override
    public Item getHandleItem ()
    {
        return TContent.bowstring;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TContent.toolRod;
    }

    @Override
    public String[] toolCategories ()
    {
        return new String[] { "weapon", "ranged", "bow" };
    }

}
