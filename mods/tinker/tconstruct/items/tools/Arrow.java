package mods.tinker.tconstruct.items.tools;

import java.util.List;

import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.library.crafting.ToolBuilder;
import mods.tinker.tconstruct.library.tools.ToolCore;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Arrow extends ToolCore
{

    public Arrow(int id)
    {
        super(id, 1);
        this.setUnlocalizedName("InfiTool.Arrow");
        this.setMaxStackSize(64);
        this.setMaxDamage(0);
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
        case 0:
            return "_arrow_head";
        case 1:
            return ""; //Doesn't break
        case 2:
            return "_arrow_shaft";
        case 3:
            return "_arrow_fletching";
        default:
            return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_arrow_effect";
    }
    @Override
    public String getDefaultFolder ()
    {
        return "arrow";
    }
    
    @Override
    public void registerPartPaths (int index, String[] location)
    {
        headStrings.put(index, location[0]);
        handleStrings.put(index, location[2]);
    }

    @Override
    public void registerAlternatePartPaths (int index, String[] location)
    {
        accessoryStrings.put(index, location[3]);
    }

    @Override
    public Item getHeadItem ()
    {
        return TContent.arrowhead;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TContent.fletching;
    }

    @Override
    public String[] toolCategories ()
    {
        return new String[] {"ammo"};
    }

    @Override
    public void buildTool (int id, String name, List list)
    {
        Item accessory = getAccessoryItem();
        ItemStack accessoryStack = accessory != null ? new ItemStack(getAccessoryItem(), 1, 0) : null;
        Item extra = getExtraItem();
        ItemStack extraStack = extra != null ? new ItemStack(getExtraItem(), 1, id) : null;
        ItemStack tool = ToolBuilder.instance.buildTool(new ItemStack(getHeadItem(), 1, id), new ItemStack(getHandleItem(), 1, id), accessoryStack, extraStack, name + getToolName());
        if (tool == null)
        {
            System.out.println("Creative builder failed tool for " + name + this.getToolName());
            System.out.println("Make sure you do not have item ID conflicts");
        }
        else
        {
            tool.stackSize = 1;
            tool.getTagCompound().getCompoundTag("InfiTool").setBoolean("Built", true);
            list.add(tool);
        }
    }
}
