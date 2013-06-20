package mods.tinker.tconstruct.library.event;

import mods.tinker.tconstruct.library.tools.ToolCore;
import mods.tinker.tconstruct.library.tools.ToolMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.Event;

/* This event fires after all of the other construction. The resulting nbttag is added to the tool 
 * Note: The tag is the base tag. toolTag.getCompoundTag("InfiTool") will have all of the tool's data.
 */

public class ToolCraftEvent extends Event
{
    public final ToolCore tool;
    public final NBTTagCompound toolTag;
    public final ToolMaterial[] materials;
    
    public ToolCraftEvent(ToolCore tool, NBTTagCompound toolTag, ToolMaterial[] materials)
    {
        this.tool = tool;
        this.toolTag = toolTag;
        this.materials = materials;
    }
}
