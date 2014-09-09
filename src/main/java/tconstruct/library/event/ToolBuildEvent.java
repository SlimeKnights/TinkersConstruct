package tconstruct.library.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.item.ItemStack;

/**
 * Called when the ToolBuilder tries to piece together the Parts of a tool.
 *
 */
public class ToolBuildEvent extends Event
{
    public ItemStack headStack;
    public ItemStack handleStack;
    public ItemStack accessoryStack;
    public ItemStack extraStack;
    public String name; // to allow shenanigans

    public ToolBuildEvent(ItemStack headStack, ItemStack handleStack, ItemStack accessoryStack, ItemStack extraStack, String name)
    {
        this.headStack = headStack;
        this.handleStack = handleStack;
        this.accessoryStack = accessoryStack;
        this.extraStack = extraStack;
        this.name = name;
    }
}
