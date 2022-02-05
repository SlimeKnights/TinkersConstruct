package slimeknights.tconstruct.library.tools.nbt;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;

/**
 * Read only view of {@link NamespacedNBT}
 */
public interface INamespacedNBTView {
  /**
   * Gets a namespaced key from NBT
   * @param name      Namedspaced key
   * @param function  Function to get data using the key
   * @param <T>  NBT type of output
   * @return  Data based on the function
   */
  <T> T get(ResourceLocation name, BiFunction<CompoundTag,String,T> function);

  /**
   * Checks if the data contains the given tag
   * @param name  Namespaced key
   * @param type  Tag type, see {@link Tag} for values
   * @return  True if the tag is contained
   */
  boolean contains(ResourceLocation name, int type);


  /* Helpers */

  /**
   * Reads an generic NBT value from the mod data
   * @param name  Name
   * @return  Integer value
   */
  default Tag get(ResourceLocation name) {
    return get(name, CompoundTag::get);
  }

  /**
   * Reads an integer from the mod data
   * @param name  Name
   * @return  Integer value
   */
  default int getInt(ResourceLocation name) {
    return get(name, CompoundTag::getInt);
  }

  /**
   * Reads an boolean from the mod data
   * @param name  Name
   * @return  Boolean value
   */
  default boolean getBoolean(ResourceLocation name) {
    return get(name, CompoundTag::getBoolean);
  }

  /**
   * Reads an float from the mod data
   * @param name  Name
   * @return  Float value
   */
  default float getFloat(ResourceLocation name) {
    return get(name, CompoundTag::getFloat);
  }

  /**
   * Reads a string from the mod data
   * @param name  Name
   * @return  String value
   */
  default String getString(ResourceLocation name) {
    return get(name, CompoundTag::getString);
  }

  /**
   * Reads a compound from the mod data
   * @param name  Name
   * @return  Compound value
   */
  default CompoundTag getCompound(ResourceLocation name) {
    return get(name, CompoundTag::getCompound);
  }
}
