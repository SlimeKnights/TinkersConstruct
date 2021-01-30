package slimeknights.tconstruct.library.tools.nbt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;

import java.util.function.BiFunction;

/**
 * NBT representing extra data on the tool, including modifier slots and a wrapper around the compound for namespaced data.
 * On a typical tool, there are two copies of this class, one for persistent data, and one that rebuilds when the modifiers refresh
 * Note unlike other NBT classes, the data inside this one is mutable as most of it is directly used by the tools.
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ModDataNBT implements IModDataReadOnly {
  private static final String TAG_MODIFIERS = "modifiers";
  private static final String TAG_ABILITIES = "abilities";

  /** Compound representing modifier data */
  @Getter(AccessLevel.PROTECTED)
  private final CompoundNBT data;

  /** Modifiers remaining in this data */
  @Getter @Setter
  private int modifiers;
  /** Abilities remaining in this data */
  @Getter @Setter
  private int abilities;

  @Override
  public <T> T getNBT(ResourceLocation name, BiFunction<CompoundNBT,String,T> function) {
    return function.apply(data, name.toString());
  }

  /**
   * Sets the given NBT into the data
   * @param name  Key name
   * @param nbt   NBT value
   */
  public void set(ResourceLocation name, INBT nbt) {
    data.put(name.toString(), nbt);
  }

  /**
   * Sets an integer from the mod data
   * @param name  Name
   * @param value  Integer value
   */
  public void setInt(ResourceLocation name, int value) {
    data.putInt(name.toString(), value);
  }

  /**
   * Sets an boolean from the mod data
   * @param name  Name
   * @param value  Boolean value
   */
  public void setBoolean(ResourceLocation name, boolean value) {
    data.putBoolean(name.toString(), value);
  }

  /**
   * Sets an float from the mod data
   * @param name  Name
   * @param value  Float value
   */
  public void setFloat(ResourceLocation name, float value) {
    data.putFloat(name.toString(), value);
  }

  /**
   * Reads a string from the mod data
   * @param name  Name
   * @param value  String value
   */
  public void getString(ResourceLocation name, String value) {
    data.putString(name.toString(), value);
  }

  /**
   * Parses the mod data from NBT
   * @param data  data
   * @return  Parsed mod data
   */
  public static ModDataNBT fromNBT(CompoundNBT data) {
    int modifiers = data.getInt(TAG_MODIFIERS);
    int abilities = data.getInt(TAG_ABILITIES);
    return new ModDataNBT(data, modifiers, abilities);
  }
}
