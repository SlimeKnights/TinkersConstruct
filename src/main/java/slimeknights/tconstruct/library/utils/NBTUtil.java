package slimeknights.tconstruct.library.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

import java.util.function.BiFunction;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NBTUtil {
  /**
   * Gets an integer from NBT, or the given default value
   * @param nbt           NBT instance
   * @param key           Key
   * @param defaultValue  Value if missing
   * @return Integer, or default if the tag is missing
   */
  public static int getInt(CompoundNBT nbt, String key, int defaultValue) {
    return getOrDefault(nbt, key, defaultValue, CompoundNBT::getInt);
  }

  /**
   * Gets an float from NBT, or the given default value
   * @param nbt           NBT instance
   * @param key           Key
   * @param defaultValue  Value if missing
   * @return Integer, or default if the tag is missing
   */
  public static float getFloat(CompoundNBT nbt, String key, float defaultValue) {
    return getOrDefault(nbt, key, defaultValue, CompoundNBT::getFloat);
  }

  /**
   * Gets an boolean from NBT, or the given default value
   * @param nbt           NBT instance
   * @param key           Key
   * @param defaultValue  Value if missing
   * @return Integer, or default if the tag is missing
   */
  public static boolean getBoolean(CompoundNBT nbt, String key, boolean defaultValue) {
    return getOrDefault(nbt, key, defaultValue, CompoundNBT::getBoolean);
  }

  /**
   * Gets a number value from NBT, or the given default value
   * @param nbt           NBT instance
   * @param key           Key
   * @param defaultValue  Value if missing
   * @return Integer, or default if the tag is missing
   */
  public static <T> T getOrDefault(CompoundNBT nbt, String key, T defaultValue, BiFunction<CompoundNBT, String, T> valueGetter) {
    if(nbt.contains(key, Constants.NBT.TAG_ANY_NUMERIC)) {
      return valueGetter.apply(nbt, key);
    }
    return defaultValue;
  }
}
