package boni.tinkersweaponry.ammo;

import boni.tinkersweaponry.TinkerWeaponry;
import boni.tinkersweaponry.library.weaponry.AmmoItem;
import boni.tinkersweaponry.util.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.tools.TinkerTools;

import java.util.List;

public class ArrowAmmo extends AmmoItem {
    public static ItemStack vanillaArrow;

    public ArrowAmmo() {
        super(0, "Arrows");
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
            case 0:
                return "_arrow_head";
            case 1:
                return ""; // Doesn't break
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
    public String getDefaultTexturePath() {
        return Reference.RESOURCE +  ":" + getDefaultFolder();
    }

    @Override
    public void registerPartPaths (int index, String[] location)
    {
        headStrings.put(index, location[0]);
    }

    @Override
    public void registerAlternatePartPaths (int index, String[] location)
    {
        handleStrings.put(index, location[2]);
        accessoryStrings.put(index, location[3]);
    }

    @Override
    public Item getHeadItem ()
    {
        return TinkerTools.arrowhead;
    }

    @Override
    public Item getHandleItem() {
        return TinkerWeaponry.partArrowShaft;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TinkerTools.fletching;
    }

    @Override
    public String[] getTraits ()
    {
        return new String[] { "ammo" };
    }

    @Override
    public void buildTool (int id, String name, List list)
    {
        ItemStack handleStack = new ItemStack(getHandleItem(), 1, 0); // wooden shaft
        ItemStack accessoryStack = new ItemStack(getAccessoryItem(), 1, 0); // feather fletchling

        ItemStack tool = ToolBuilder.instance.buildTool(new ItemStack(getHeadItem(), 1, id), handleStack, accessoryStack, null, "");
        if (tool != null)
        {
            tool.getTagCompound().getCompoundTag("InfiTool").setBoolean("Built", true);
            list.add(tool);
        }
    }

    @Override
    public void getSubItems(Item id, CreativeTabs tab, List list) {
        super.getSubItems(id, tab, list);

        // vanilla arrow
        ItemStack headStack = new ItemStack(getHeadItem(), 1, TinkerTools.MaterialID.Flint); // flint arrow head
        ItemStack handleStack = new ItemStack(getHandleItem(), 1, 0); // wooden shaft
        ItemStack accessoryStack = new ItemStack(getAccessoryItem(), 1, 0); // feather fletchling

        ItemStack tool = ToolBuilder.instance.buildTool(headStack, handleStack, accessoryStack, null, "");
        if (tool != null)
        {
            tool.getTagCompound().getCompoundTag("InfiTool").setBoolean("Built", true);
            vanillaArrow = tool;
        }
        else
            TinkerWeaponry.Log.error("Couldn't build vanilla equivalent of Tinker Arrow");
    }
}
