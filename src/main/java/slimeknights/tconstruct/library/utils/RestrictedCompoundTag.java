package slimeknights.tconstruct.library.utils;

import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Wrapper around a compound tag to restrict access
 */
@RequiredArgsConstructor
public class RestrictedCompoundTag {
  /** Base NBT compound */
  private final CompoundTag tag;
  /** List of tags with restricted access */
  private final Set<String> restrictedKeys;

  /**
   * Checks if the data contains the given tag
   * @param name  Namespaced key
   * @param type  Tag type, see {@link Tag} for values
   * @return  True if the tag is contained
   */
  public boolean contains(String name, int type) {
    return !restrictedKeys.contains(name) && tag.contains(name, type);
  }


  /* Get functions */

  /**
   * Gets a namespaced key from NBT
   * @param name      Name
   * @param function  Function to get data using the key
   * @param <T>  NBT type of output
   * @return  Data based on the function
   */
  protected <T> T get(String name, BiFunction<CompoundTag,String,T> function, T defaultValue) {
    if (restrictedKeys.contains(name)) {
      return defaultValue;
    }
    return function.apply(tag, name);
  }


  /**
   * Reads an generic NBT value from the mod data
   * @param name  Name
   * @return  Integer value
   */
  @SuppressWarnings("ConstantConditions")
  @Nullable
  public Tag get(String name) {
    return get(name, CompoundTag::get, null);
  }

  /**
   * Reads an integer from the tag
   * @param name  Name
   * @return  Integer value
   */
  public int getInt(String name) {
    return get(name, CompoundTag::getInt, 0);
  }

  /**
   * Reads an boolean from the tag
   * @param name  Name
   * @return  Boolean value
   */
  public boolean getBoolean(String name) {
    return get(name, CompoundTag::getBoolean, false);
  }

  /**
   * Reads an float from the tag
   * @param name  Name
   * @return  Float value
   */
  public float getFloat(String name) {
    return get(name, CompoundTag::getFloat, 0f);
  }

  /**
   * Reads a string from the tag
   * @param name  Name
   * @return  String value
   */
  public String getString(String name) {
    return get(name, CompoundTag::getString, "");
  }

  /**
   * Reads a compound from the tag
   * @param name  Name
   * @return  Compound value
   */
  public CompoundTag getCompound(String name) {
    if (restrictedKeys.contains(name)) {
      return new CompoundTag();
    }
    return tag.getCompound(name);
  }

  /**
   * Reads a list from the tag
   * @param name  Name
   * @return  Compound value
   */
  public ListTag getList(String name, int type) {
    if (restrictedKeys.contains(name)) {
      return new ListTag();
    }
    return tag.getList(name, type);
  }


  /* Put methods */

  /**
   * Sets the given NBT into tag
   * @param name  Key name
   * @param nbt   NBT value
   */
  public void put(String name, Tag nbt) {
    if (!restrictedKeys.contains(name)) {
      tag.put(name, nbt);
    }
  }

  /**
   * Sets an integer from the tag
   * @param name  Name
   * @param value  Integer value
   */
  public void putInt(String name, int value) {
    if (!restrictedKeys.contains(name)) {
      tag.putInt(name, value);
    }
  }

  /**
   * Sets an boolean from the tag
   * @param name  Name
   * @param value  Boolean value
   */
  public void putBoolean(String name, boolean value) {
    if (!restrictedKeys.contains(name)) {
      tag.putBoolean(name, value);
    }
  }

  /**
   * Sets an float from the tag
   * @param name  Name
   * @param value  Float value
   */
  public void putFloat(String name, float value) {
    if (!restrictedKeys.contains(name)) {
      tag.putFloat(name, value);
    }
  }

  /**
   * Reads a string from the tag
   * @param name  Name
   * @param value  String value
   */
  public void putString(String name, String value) {
    if (!restrictedKeys.contains(name)) {
      tag.putString(name, value);
    }
  }

  /**
   * Removes the given key from tag
   * @param name  Key to remove
   */
  public void remove(String name) {
    if (!restrictedKeys.contains(name)) {
      tag.remove(name);
    }
  }
}
