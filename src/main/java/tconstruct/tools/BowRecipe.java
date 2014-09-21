package tconstruct.tools;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tconstruct.library.crafting.ToolRecipe;
import tconstruct.library.tools.ToolCore;

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

    @Override
    public boolean validHead (ItemStack input)
    {
        for (Item part : headList)
        {
            if (part == input.getItem())
                return true;
            if (toolRod != null && part == toolRod && (input.getItem() == Items.stick || input.getItem() == Items.bone))
                return true;
        }
        return false;
    }
}
