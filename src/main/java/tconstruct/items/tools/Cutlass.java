package tconstruct.items.tools;

import net.minecraft.item.Item;
import tconstruct.library.tools.Weapon;
import tconstruct.tools.TinkerTools;

public class Cutlass extends Weapon
{
    public Cutlass()
    {
        super(4);
        this.setUnlocalizedName("InfiTool.Cutlass");
    }

    @Override
    public Item getHeadItem ()
    {
        return TinkerTools.swordBlade;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TinkerTools.fullGuard;
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
}
