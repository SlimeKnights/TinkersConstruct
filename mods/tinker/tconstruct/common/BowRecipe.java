package mods.tinker.tconstruct.common;

import net.minecraft.item.Item;
import mods.tinker.tconstruct.library.crafting.ToolRecipe;
import mods.tinker.tconstruct.library.tools.ToolCore;

public class BowRecipe extends ToolRecipe
{

    public BowRecipe(Item head, Item handle, ToolCore tool)
    {
        super(head, handle, tool);
    }

    public BowRecipe(Item head, Item handle, Item accessory, ToolCore tool)
    {
        super(head, handle, accessory, tool);
    }

    public BowRecipe(Item head, Item handle, Item accessory, Item extra, ToolCore tool)
    {
        super(head, handle, accessory, extra, tool);
    }

    public boolean validHead (Item input)
    {
        for (Item part : headList)
        {
            if (part == input)
                return true;
            if (toolRod != null && part == toolRod && (input == Item.stick || input == Item.bone))
                return true;
        }
        return false;
    }
}
