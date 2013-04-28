package mods.tinker.tconstruct.items.tools;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.library.tools.ToolCore;
import net.minecraft.item.Item;

public class Chisel extends ToolCore
{
    public Chisel(int id)
    {
        super(id, 0);
        this.setUnlocalizedName("InfiTool.Chisel");
    }

    @Override
    public int getHeadType ()
    {
        return 1;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderPasses (int metadata)
    {
        return 8;
    }
    
    @Override
    public int getPartAmount()
    {
        return 2;
    }
    
    @Override
    public void registerPartPaths (int index, String[] location)
    {
        headStrings.put(index, location[0]);
        brokenHeadStrings.put(index, location[1]);
        handleStrings.put(index, location[2]);
    }
    
    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
        case 0:
            return "_chisel_head";
        case 1:
            return "_chisel_head_broken";
        case 2:
            return "_chisel_handle";
        default:
            return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_chisel_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "chisel";
    }

    @Override
    protected Item getHeadItem ()
    {
        return TContent.chiselHead;
    }

    @Override
    protected Item getAccessoryItem ()
    {
        return null;
    }

    @Override
    public String[] toolCategories ()
    {
        return new String[] { "utility" };
    }

}
