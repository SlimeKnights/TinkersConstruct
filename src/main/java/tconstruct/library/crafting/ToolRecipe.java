package tconstruct.library.crafting;

import java.util.LinkedList;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.ToolCore;

/*
 * Small class for checking if a particular tool combination is this one
 */

public class ToolRecipe
{
    protected LinkedList<Item> headList = new LinkedList<Item>();
    protected LinkedList<Item> handleList = new LinkedList<Item>();
    protected LinkedList<Item> accessoryList = new LinkedList<Item>();
    protected LinkedList<Item> extraList = new LinkedList<Item>();
    protected ToolCore result;
    protected Item toolRod = TConstructRegistry.getItem("toolRod");

    /*
     * public ToolRecipe(Item head, ToolCore tool) { this(head,
     * TContent.toolRod, null, null, tool); }
     */

    public ToolRecipe(Item head, Item handle, ToolCore tool)
    {
        this(head, handle, null, null, tool);
    }

    public ToolRecipe(Item head, Item handle, Item accessory, ToolCore tool)
    {
        this(head, handle, accessory, null, tool);
    }

    public ToolRecipe(Item head, Item handle, Item accessory, Item extra, ToolCore tool)
    {
        this.headList.add(head);
        this.handleList.add(handle);
        if (accessory != null)
            this.accessoryList.add(accessory);
        if (extra != null)
            this.extraList.add(extra);
        result = tool;
    }

    public void addHeadItem (ItemStack head)
    {
        this.headList.add(head.getItem());
    }

    public void addHandleItem (ItemStack handle)
    {
        this.handleList.add(handle.getItem());
    }

    public void addAccessoryItem (ItemStack accessory)
    {
        this.accessoryList.add(accessory.getItem());
    }

    public void addExtraItem (ItemStack extra)
    {
        this.extraList.add(extra.getItem());
    }

    public boolean validHead (ItemStack input)
    {
        for (Item part : headList)
        {
            if (part == input.getItem())
                return true;
        }
        return false;
    }

    public boolean validHandle (ItemStack input)
    {
        for (Item part : handleList)
        {
            if (part == input.getItem())
                return true;
            if (toolRod != null && part == toolRod && (input.getItem() == Items.stick || input.getItem() == Items.bone))
                return true;
        }
        return false;
    }

    public boolean validAccessory (ItemStack input)
    {
        if (input == null)
        {
            if (accessoryList.size() < 1)
                return true;
            return false;
        }
        for (Item part : accessoryList)
        {
            if (part == input.getItem())
                return true;
            if (toolRod != null && part == toolRod && (input.getItem() == Items.stick || input.getItem() == Items.bone))
                return true;
        }
        return false;
    }

    public boolean validExtra (ItemStack input)
    {
        if (input == null)
        {
            if (extraList.size() < 1)
                return true;
            return false;
        }
        for (Item part : extraList)
        {
            if (part == input.getItem())
                return true;
            if (toolRod != null && part == toolRod && (input.getItem() == Items.stick || input.getItem() == Items.bone))
                return true;
        }
        return false;
    }

    public ToolCore getType ()
    {
        return result;
    }
}
