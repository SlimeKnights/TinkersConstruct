package slimeknights.tconstruct.library.tools.nbt;

import net.minecraft.nbt.CompoundNBT;
import slimeknights.tconstruct.library.tools.SlotType;

/**
 * NBT representing extra data on the tool, including modifier slots and a wrapper around the compound for namespaced data.
 * On a typical tool, there are two copies of this class, one for persistent data, and one that rebuilds when the modifiers refresh
 * Note unlike other NBT classes, the data inside this one is mutable as most of it is directly used by the tools.
 */
public class ModDataNBT extends NamespacedNBT implements IModDataReadOnly {
  public ModDataNBT() {}

  protected ModDataNBT(CompoundNBT nbt) {
    super(nbt);
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
  public static ModDataNBT readFromNBT(CompoundNBT data) {
    return new ModDataNBT(data);
  }


  /* Deprecated, to remove */

  /** @deprecated Use {@link #setSlots(SlotType, int)} */
  @Deprecated
  public void setUpgrades(int value) {
    setSlots(SlotType.UPGRADE, value);
  }

  /** @deprecated Use {@link #addSlots(SlotType, int)} (SlotType, int)} */
  @Deprecated
  public void addUpgrades(int add) {
    addSlots(SlotType.UPGRADE, add);
  }

  /** @deprecated Use {@link #setSlots(SlotType, int)} */
  @Deprecated
  public void setAbilities(int value) {
    setSlots(SlotType.ABILITY, value);
  }

  /** @deprecated Use {@link #addSlots(SlotType, int)} (SlotType, int)} */
  @Deprecated
  public void addAbilities(int add) {
    addSlots(SlotType.ABILITY, add);
  }

  /** @deprecated Use {@link #setSlots(SlotType, int)} */
  @Deprecated
  public void setTraits(int value) {
    setSlots(SlotType.SOUL, value);
  }

  /** @deprecated Use {@link #addSlots(SlotType, int)} (SlotType, int)} */
  @Deprecated
  public void addTraits(int add) {
    addSlots(SlotType.SOUL, add);
  }

  /** @deprecated Use {@link #getSlots(SlotType)} */
  @Deprecated
  public int getTraits() {
    return getSlots(SlotType.SOUL);
  }
}
