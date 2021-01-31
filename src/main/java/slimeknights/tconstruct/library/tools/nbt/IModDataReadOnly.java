package slimeknights.tconstruct.library.tools.nbt;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;

import java.util.function.BiFunction;

/**
 * Read only view of {@link ModDataNBT}
 */
public interface IModDataReadOnly {
  /** Empty variant of mod data */
  IModDataReadOnly EMPTY = new IModDataReadOnly() {
    @Override
    public int getModifiers() {
      return 0;
    }

    @Override
    public int getAbilities() {
      return 0;
    }

    @Override
    public <T> T getNBT(ResourceLocation name, BiFunction<CompoundNBT,String,T> function) {
      return function.apply(new CompoundNBT(), name.toString());
    }
  };

  /** Gets the number of modifiers provided by this data */
  int getModifiers();

  /** Gets the number of ability slots provided by this data */
  int getAbilities();


  /**
   * Gets a namespaced key from NBT
   * @param name      Namedspaced key
   * @param function  Function to get data using the key
   * @param <T>  NBT type of output
   * @return  Data based on the function
   */
  <T> T getNBT(ResourceLocation name, BiFunction<CompoundNBT,String,T> function);


  /* Helpers */

  /**
   * Reads an generic NBT value from the mod data
   * @param name  Name
   * @return  Integer value
   */
  default INBT get(ResourceLocation name) {
    return getNBT(name, CompoundNBT::get);
  }

  /**
   * Reads an integer from the mod data
   * @param name  Name
   * @return  Integer value
   */
  default int getInt(ResourceLocation name) {
    return getNBT(name, CompoundNBT::getInt);
  }

  /**
   * Reads an boolean from the mod data
   * @param name  Name
   * @return  Boolean value
   */
  default boolean getBoolean(ResourceLocation name) {
    return getNBT(name, CompoundNBT::getBoolean);
  }

  /**
   * Reads an float from the mod data
   * @param name  Name
   * @return  Float value
   */
  default float getFloat(ResourceLocation name) {
    return getNBT(name, CompoundNBT::getFloat);
  }

  /**
   * Reads a string from the mod data
   * @param name  Name
   * @return  String value
   */
  default String getString(ResourceLocation name) {
    return getNBT(name, CompoundNBT::getString);
  }

  /**
   * Reads a compound from the mod data
   * @param name  Name
   * @return  Compound value
   */
  default CompoundNBT getCompound(ResourceLocation name) {
    return getNBT(name, CompoundNBT::getCompound);
  }
}
