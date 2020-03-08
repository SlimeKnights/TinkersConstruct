package slimeknights.tconstruct.library.tools.nbt;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

/**
 * All the stats that every tool must have.
 * Some may not be used explicitly by all tools (e.g. weapons and harvest  level)
 */
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class StatsNBT {

  public static final int DEFAULT_MODIFIERS = 3;
  final static StatsNBT EMPTY = new StatsNBT(1, 0, 1, 1, 1, DEFAULT_MODIFIERS);

  public static final String TAG_DURABILITY = "durability";
  public static final String TAG_ATTACK = "attack";
  public static final String TAG_ATTACKSPEEDMULTIPLIER = "attack_speed_multiplier";
  public static final String TAG_MININGSPEED = "mining_speed";
  public static final String TAG_HARVESTLEVEL = "harvest_level";
  public static final String TAG_FREE_MODIFIERS = "free_modifiers";

  public final int durability;
  public final int harvestLevel;
  public final float attack;
  public final float miningSpeed;
  public final float attackSpeedMultiplier;
  public final int freeModifiers;

  public static StatsNBT readFromNBT(@Nullable INBT inbt) {
    if (inbt == null || inbt.getId() != Constants.NBT.TAG_COMPOUND) {
      return EMPTY;
    }

    CompoundNBT nbt = (CompoundNBT)inbt;
    int durability = getIntFromTagOrDefault(nbt, TAG_DURABILITY, EMPTY.durability);
    int harvestLevel = getIntFromTagOrDefault(nbt, TAG_HARVESTLEVEL, EMPTY.harvestLevel);
    float attack = getFloatFromTagOrDefault(nbt, TAG_ATTACK, EMPTY.attack);
    float miningSpeed = getFloatFromTagOrDefault(nbt, TAG_MININGSPEED, EMPTY.miningSpeed);
    float attackSpeedMultiplier = getFloatFromTagOrDefault(nbt, TAG_ATTACKSPEEDMULTIPLIER, EMPTY.attackSpeedMultiplier);
    int modifiers = getIntFromTagOrDefault(nbt, TAG_FREE_MODIFIERS, EMPTY.freeModifiers);

    return new StatsNBT(durability, harvestLevel, attack, miningSpeed, attackSpeedMultiplier, modifiers);
  }

  private static int getIntFromTagOrDefault(CompoundNBT nbt, String key, int defaultValue) {
    if(nbt.contains(TAG_DURABILITY, Constants.NBT.TAG_ANY_NUMERIC)) {
      return nbt.getInt(key);
    }
    return defaultValue;
  }

  private static float getFloatFromTagOrDefault(CompoundNBT nbt, String key, float defaultValue) {
    if(nbt.contains(TAG_DURABILITY, Constants.NBT.TAG_ANY_NUMERIC)) {
      return nbt.getFloat(key);
    }
    return defaultValue;
  }

  public CompoundNBT serializeToNBT() {
    CompoundNBT nbt = new CompoundNBT();
    nbt.putInt(TAG_DURABILITY, durability);
    nbt.putInt(TAG_HARVESTLEVEL, harvestLevel);
    nbt.putFloat(TAG_ATTACK, attack);
    nbt.putFloat(TAG_MININGSPEED, miningSpeed);
    nbt.putFloat(TAG_ATTACKSPEEDMULTIPLIER, attackSpeedMultiplier);
    nbt.putInt(TAG_FREE_MODIFIERS, freeModifiers);

    return nbt;
  }
}
