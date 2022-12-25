package slimeknights.tconstruct.library.tools.nbt;

import net.minecraft.nbt.CompoundTag;
import slimeknights.tconstruct.library.tools.SlotType;

/**
 * NBT representing extra data on the tool, including modifier slots and a wrapper around the compound for namespaced data.
 * On a typical tool, there are two copies of this class, one for persistent data, and one that rebuilds when the modifiers refresh
 * Note unlike other NBT classes, the data inside this one is mutable as most of it is directly used by the tools.
 */
public class ModDataNBT extends NamespacedNBT implements IModDataView {
  public ModDataNBT() {}

  protected ModDataNBT(CompoundTag nbt) {
    super(nbt);
  }

  /** Constructor to clone from another instance, needed to deal with an API conflict */
  public ModDataNBT(NamespacedNBT nbt) {
    super(nbt.getData());
  }

  @Override
  public int getSlots(SlotType type) {
    return getData().getInt(type.getName());
  }

  /**
   * Sets the slots for the given type
   * @param type   Slot type
   * @param value  New value
   */
  public void setSlots(SlotType type, int value) {
    if (value == 0) {
      getData().remove(type.getName());
    } else {
      getData().putInt(type.getName(), value);
    }
  }

  /**
   * Adds the given number of slots
   * @param type   Slot type
   * @param add    Value to add, use negative to remove
   */
  public void addSlots(SlotType type, int add) {
    if (add != 0) {
      setSlots(type, getSlots(type) + add);
    }
  }


  /**
   * Parses the mod data from NBT
   * @param data  data
   * @return  Parsed mod data
   */
  public static ModDataNBT readFromNBT(CompoundTag data) {
    return new ModDataNBT(data);
  }
}
