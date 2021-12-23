package slimeknights.tconstruct.library.tools.nbt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;

import java.util.function.BiFunction;

/**
 * NBT wrapper enforcing namespaces on compound keys
 */
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class NamespacedNBT implements INamespacedNBTReadOnly {
  /** Compound representing modifier data */
  @Getter(AccessLevel.PROTECTED)
  private final CompoundNBT data;

  /**
   * Creates a new mod data containing empty data
   */
  public NamespacedNBT() {
    this(new CompoundNBT());
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


  /* Networking */

  /** Gets a copy of the internal data, generally should only be used for syncing, no reason to call directly */
  public CompoundNBT getCopy() {
    return data.copy();
  }

  /**
   * Called to merge this NBT data from another
   * @param data  data
   */
  public void copyFrom(CompoundNBT data) {
    this.data.keySet().clear();
    this.data.merge(data);
  }

  /**
   * Parses the data from NBT
   * @param data  data
   * @return  Parsed mod data
   */
  public static NamespacedNBT readFromNBT(CompoundNBT data) {
    return new NamespacedNBT(data);
  }
}
