package slimeknights.tconstruct.library.tools.nbt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;

/**
 * NBT representing extra data on the tool for modifiers, with a wrapper around the compound for to enforce namespacing data.
 * On a typical tool, there are two copies of this class, one for persistent data, and one that rebuilds when the modifiers refresh.
 * Note unlike other NBT classes, the data inside this one is mutable as most of it is directly used by the tools.
 */
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ModDataNBT implements IModDataView {
  /** Compound representing modifier data */
  @Getter(AccessLevel.PROTECTED)
  private final CompoundTag data;

  /**
   * Creates a new mod data containing empty data
   */
  public ModDataNBT() {
    this(new CompoundTag());
  }

  @Override
  public <T> T get(ResourceLocation name, BiFunction<CompoundTag,String,T> function) {
    return function.apply(data, name.toString());
  }

  @Override
  public boolean contains(ResourceLocation name) {
    return data.contains(name.toString());
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
  public void put(ResourceLocation name, Tag nbt) {
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


  /* Networking */

  /** Gets a copy of the internal data, generally should only be used for syncing, no reason to call directly */
  public CompoundTag getCopy() {
    return data.copy();
  }

  /**
   * Called to merge this NBT data from another
   * @param data  data
   */
  public void copyFrom(CompoundTag data) {
    this.data.getAllKeys().clear();
    this.data.merge(data);
  }

  /**
   * Parses the data from NBT
   * @param data  data
   * @return  Parsed mod data
   */
  public static ModDataNBT readFromNBT(CompoundTag data) {
    return new ModDataNBT(data);
  }
}
