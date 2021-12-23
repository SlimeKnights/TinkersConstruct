package slimeknights.tconstruct.library.tools.nbt;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;

import java.util.function.BiFunction;

/**
 * Read only view of {@link NamespacedNBT}
 */
public interface INamespacedNBTReadOnly {
  /**
   * Gets a namespaced key from NBT
   * @param name      Namedspaced key
   * @param function  Function to get data using the key
   * @param <T>  NBT type of output
   * @return  Data based on the function
   */
  <T> T get(ResourceLocation name, BiFunction<CompoundNBT,String,T> function);

  /**
   * Checks if the data contains the given tag
   * @param name  Namespaced key
   * @param type  Tag type, see {@link net.minecraftforge.common.util.Constants.NBT} for values
   * @return  True if the tag is contained
   */
  boolean contains(ResourceLocation name, int type);


  /* Helpers */

  /**
   * Reads an generic NBT value from the mod data
   * @param name  Name
   * @return  Integer value
   */
  default INBT get(ResourceLocation name) {
    return get(name, CompoundNBT::get);
  }

  /**
   * Reads an integer from the mod data
   * @param name  Name
   * @return  Integer value
   */
  default int getInt(ResourceLocation name) {
    return get(name, CompoundNBT::getInt);
  }

  /**
   * Reads an boolean from the mod data
   * @param name  Name
   * @return  Boolean value
   */
  default boolean getBoolean(ResourceLocation name) {
    return get(name, CompoundNBT::getBoolean);
  }

  /**
   * Reads an float from the mod data
   * @param name  Name
   * @return  Float value
   */
  default float getFloat(ResourceLocation name) {
    return get(name, CompoundNBT::getFloat);
  }

  /**
   * Reads a string from the mod data
   * @param name  Name
   * @return  String value
   */
  default String getString(ResourceLocation name) {
    return get(name, CompoundNBT::getString);
  }

  /**
   * Reads a compound from the mod data
   * @param name  Name
   * @return  Compound value
   */
  default CompoundNBT getCompound(ResourceLocation name) {
    return get(name, CompoundNBT::getCompound);
  }
}
