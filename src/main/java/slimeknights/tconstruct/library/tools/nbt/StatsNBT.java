package slimeknights.tconstruct.library.tools.nbt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.With;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

/**
 * All the stats that every tool must have.
 * Some may not be used explicitly by all tools (e.g. weapons and harvest  level)
 */
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class StatsNBT {

  public static final int DEFAULT_MOD_SLOTS = 3;
  public static final int DEFAULT_ABILITY_SLOTS = 1;
  public static final int DEFAULT_ARMOR_SLOTS = 0;
  public static final int DEFAULT_TRAIT_SLOTS = 1;
  final static StatsNBT EMPTY = new StatsNBT(1, 0, 1, 1, 1, DEFAULT_MOD_SLOTS, DEFAULT_ABILITY_SLOTS, DEFAULT_ARMOR_SLOTS, DEFAULT_TRAIT_SLOTS,false);

  public static final String TAG_DURABILITY = "durability";
  public static final String TAG_ATTACK = "attack";
  public static final String TAG_ATTACKSPEEDMULTIPLIER = "attack_speed_multiplier";
  public static final String TAG_MININGSPEED = "mining_speed";
  public static final String TAG_HARVESTLEVEL = "harvest_level";
  public static final String TAG_FREE_UPGRADE_SLOTS = "upgrade_slots";
  public static final String TAG_FREE_ABILITY_SLOTS = "ability_slots";
  public static final String TAG_FREE_ARMOR_SLOTS = "armor_slots";
  public static final String TAG_FREE_TRAIT_SLOTS = "trait_slots";
  public static final String TAG_BROKEN = "is_broken";

  public final int durability;
  public final int harvestLevel;
  public final float attack;
  public final float miningSpeed;
  public final float attackSpeedMultiplier;
  public final int freeUpgradeSlots;
  public final int freeAbilitySlots;
  public final int freeArmorSlots;
  public final int freeTraitSlots;

  // this is placed in the stats since it has a direct impact on durability
  @With(AccessLevel.PACKAGE)
  public final boolean broken;

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
    int upgradeSlots = getIntFromTagOrDefault(nbt, TAG_FREE_UPGRADE_SLOTS, EMPTY.freeUpgradeSlots);
    int abilitySlots = getIntFromTagOrDefault(nbt, TAG_FREE_ABILITY_SLOTS, EMPTY.freeAbilitySlots);
    int armorSlots = getIntFromTagOrDefault(nbt, TAG_FREE_ARMOR_SLOTS, EMPTY.freeArmorSlots);
    int traitSlots = getIntFromTagOrDefault(nbt, TAG_FREE_TRAIT_SLOTS, EMPTY.freeTraitSlots);
    boolean isBroken = getBoolFromTagOrDefault(nbt, TAG_BROKEN, EMPTY.broken);

    return new StatsNBT(durability, harvestLevel, attack, miningSpeed, attackSpeedMultiplier, upgradeSlots, abilitySlots, armorSlots, traitSlots, isBroken);
  }

  private static int getIntFromTagOrDefault(CompoundNBT nbt, String key, int defaultValue) {
    return getFromTagOrDefault(nbt, key, defaultValue, CompoundNBT::getInt);
  }

  private static float getFloatFromTagOrDefault(CompoundNBT nbt, String key, float defaultValue) {
    return getFromTagOrDefault(nbt, key, defaultValue, CompoundNBT::getFloat);
  }

  private static boolean getBoolFromTagOrDefault(CompoundNBT nbt, String key, boolean defaultValue) {
    return getFromTagOrDefault(nbt, key, defaultValue, CompoundNBT::getBoolean);
  }

  private static <T> T getFromTagOrDefault(CompoundNBT nbt, String key, T defaultValue, BiFunction<CompoundNBT, String, T> valueGetter) {
    if(nbt.contains(key, Constants.NBT.TAG_ANY_NUMERIC)) {
      return valueGetter.apply(nbt, key);
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
    nbt.putInt(TAG_FREE_UPGRADE_SLOTS, freeUpgradeSlots);
    nbt.putInt(TAG_FREE_ABILITY_SLOTS, freeAbilitySlots);
    nbt.putInt(TAG_FREE_ARMOR_SLOTS, freeArmorSlots);
    nbt.putInt(TAG_FREE_TRAIT_SLOTS, freeTraitSlots);
    nbt.putBoolean(TAG_BROKEN, broken);

    return nbt;
  }
}
