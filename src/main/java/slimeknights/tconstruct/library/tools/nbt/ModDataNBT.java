package slimeknights.tconstruct.library.tools.nbt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.tools.SlotType;

import java.util.function.BiFunction;

/**
 * NBT representing extra data on the tool, including modifier slots and a wrapper around the compound for namespaced data.
 * On a typical tool, there are two copies of this class, one for persistent data, and one that rebuilds when the modifiers refresh
 * Note unlike other NBT classes, the data inside this one is mutable as most of it is directly used by the tools.
 */
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ModDataNBT implements IModDataReadOnly {
  /** Compound representing modifier data */
  @Getter(AccessLevel.PROTECTED)
  private final CompoundNBT data;

  /**
   * Creates a new mod data containing empty data
   */
  public ModDataNBT() {
    this(new CompoundNBT());
  }

  @Override
  public int getSlots(SlotType type) {
    return data.getInt(type.getName());
  }

  /**
   * Sets the slots for the given type
   * @param type   Slot type
   * @param value  New value
   */
  public void setSlots(SlotType type, int value) {
    data.putInt(type.getName(), value);
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

  @Override
  public <T> T get(ResourceLocation name, BiFunction<CompoundNBT,String,T> function) {
    return function.apply(data, name.toString());
  }

  @Override
  public boolean contains(ResourceLocation name, int type) {
    return data.contains(name.toString(), type);
  }

  /**
   * Sets the given NBT into the data
   * @param name  Key name
   * @param nbt   NBT value
   */
  public void put(ResourceLocation name, INBT nbt) {
    data.put(name.toString(), nbt);
  }

  /**
   * Sets an integer from the mod data
   * @param name  Name
   * @param value  Integer value
   */
  public void putInt(ResourceLocation name, int value) {
    data.putInt(name.toString(), value);
  }

  /**
   * Sets an boolean from the mod data
   * @param name  Name
   * @param value  Boolean value
   */
  public void putBoolean(ResourceLocation name, boolean value) {
    data.putBoolean(name.toString(), value);
  }

  /**
   * Sets an float from the mod data
   * @param name  Name
   * @param value  Float value
   */
  public void putFloat(ResourceLocation name, float value) {
    data.putFloat(name.toString(), value);
  }

  /**
   * Reads a string from the mod data
   * @param name  Name
   * @param value  String value
   */
  public void putString(ResourceLocation name, String value) {
    data.putString(name.toString(), value);
  }

  /**
   * Removes the given key from the NBT
   * @param name  Key to remove
   */
  public void remove(ResourceLocation name) {
    data.remove(name.toString());
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
    setSlots(SlotType.TRAIT, value);
  }

  /** @deprecated Use {@link #addSlots(SlotType, int)} (SlotType, int)} */
  @Deprecated
  public void addTraits(int add) {
    addSlots(SlotType.TRAIT, add);
  }

  /** @deprecated Use {@link #getSlots(SlotType)} */
  @Deprecated
  public int getTraits() {
    return getSlots(SlotType.TRAIT);
  }
}
