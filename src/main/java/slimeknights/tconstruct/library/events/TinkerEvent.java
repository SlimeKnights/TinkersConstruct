package slimeknights.tconstruct.library.events;

import com.google.common.collect.ImmutableList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

import slimeknights.tconstruct.library.materials.Material;

/**
 * Base class for all tinkers events
 */
public abstract class TinkerEvent extends Event {

  /**
   * Fired when a tool is built.
   * This happens every time a tool is loaded as well as when the player actually builds the tool.
   * You can make changes to the tag compound and it'll land on the resulting tool, but its itemstack is not available.
   */
  public static class OnItemBuilding extends TinkerEvent {

    public NBTTagCompound tag;
    public final ImmutableList<Material> materials;

    public OnItemBuilding(NBTTagCompound tag, ImmutableList<Material> materials) {
      this.tag = tag;
      this.materials = materials;
    }

    public static OnItemBuilding fireEvent(NBTTagCompound tag, ImmutableList<Material> materials) {
      OnItemBuilding event = new OnItemBuilding(tag, materials);
      MinecraftForge.EVENT_BUS.post(event);
      return event;
    }
  }
}
