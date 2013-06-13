package mods.tinker.tconstruct.event;

import mods.tinker.tconstruct.library.tools.ToolCore;
import mods.tinker.tconstruct.library.tools.ToolMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.Event;

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
