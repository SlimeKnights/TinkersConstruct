package tconstruct.library.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.tools.*;

/* This event fires after all of the other construction. The resulting nbttag is added to the tool 
 * Note: The tag is the base tag. toolTag.getCompoundTag("InfiTool") will have all of the tool's data.
 */

public class ToolCraftEvent extends Event
{
    public final ToolCore tool;
    public final NBTTagCompound toolTag;
    public final ToolMaterial[] materials;
    protected ItemStack resultStack;

    public ToolCraftEvent(ToolCore tool, NBTTagCompound toolTag, ToolMaterial[] materials)
    {
        this.tool = tool;
        this.toolTag = toolTag;
        this.materials = materials;
    }

    @HasResult
    public static class NormalTool extends ToolCraftEvent
    {
        public NormalTool(ToolCore tool, NBTTagCompound toolTag, ToolMaterial[] materials)
        {
            super(tool, toolTag, materials);
        }

        /**
         * Fires just before the tool is put together
         * 
         * Result is significant: DEFAULT: Allows tool to be crafted normally
         * ALLOW: Uses resultStack instead DENY: Stops tool crafting altogether
         */

        public void overrideResult (ItemStack result)
        {
            resultStack = result;
            this.setResult(Result.ALLOW);
        }

        public ItemStack getResultStack ()
        {
            return resultStack;
        }
    }
}
