package tconstruct.items.tools;

import java.util.List;

import tconstruct.common.TContent;
import tconstruct.library.tools.Weapon;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class Cutlass extends Weapon
{
    public Cutlass(int itemID)
    {
        super(itemID, 4);
        this.setUnlocalizedName("InfiTool.Cutlass");
    }

    @Override
    public Item getHeadItem ()
    {
        return TContent.swordBlade;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TContent.fullGuard;
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
        case 0:
            return "_cutlass_blade";
        case 1:
            return "_cutlass_blade_broken";
        case 2:
            return "_cutlass_handle";
        case 3:
            return "_cutlass_guard";
        default:
            return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_cutlass_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "cutlass";
    }

    @Override
    public int durabilityTypeAccessory ()
    {
        return 1;
    }

    public void getSubItems (int id, CreativeTabs tab, List list)
    {

    }
}
