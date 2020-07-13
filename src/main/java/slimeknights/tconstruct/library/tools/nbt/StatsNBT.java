package slimeknights.tconstruct.library.tools.nbt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.With;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraftforge.common.util.Constants;
import slimeknights.tconstruct.library.utils.Tags;

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

  final static StatsNBT EMPTY = new StatsNBT(10, 0, 1, 1,
    0,1f, 1f, 0,1f,
    DEFAULT_MOD_SLOTS, DEFAULT_ABILITY_SLOTS, DEFAULT_ARMOR_SLOTS, DEFAULT_TRAIT_SLOTS, false);

  public final int durability;
  public final int harvestLevel;
  public final float attack;
  public final float miningSpeed;

  public final int repairCount;
  public final float miningSpeedMultiplier;
  public final float attackSpeedMultiplier;
  public final int bonusDurability;
  public final float bonusDurabilityMultiplier;

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
    int durability = Tags.getIntFromTagOrDefault(nbt, Tags.DURABILITY, EMPTY.durability);
    int harvestLevel = Tags.getIntFromTagOrDefault(nbt, Tags.HARVEST_LEVEL, EMPTY.harvestLevel);
    float attack = Tags.getFloatFromTagOrDefault(nbt, Tags.ATTACK, EMPTY.attack);
    float miningSpeed = Tags.getFloatFromTagOrDefault(nbt, Tags.MINING_SPEED, EMPTY.miningSpeed);

    int repairCount = Tags.getIntFromTagOrDefault(nbt, Tags.REPAIR_COUNT, EMPTY.repairCount);
    float miningSpeedMultiplier = Tags.getFloatFromTagOrDefault(nbt, Tags.MINING_SPEED_MULTIPLIER, EMPTY.miningSpeedMultiplier);
    float attackSpeedMultiplier = Tags.getFloatFromTagOrDefault(nbt, Tags.ATTACK_SPEED_MULTIPLIER, EMPTY.attackSpeedMultiplier);
    int bonusDurability = Tags.getIntFromTagOrDefault(nbt, Tags.BONUS_DURABILITY, EMPTY.bonusDurability);
    float bonusDurabilityMultiplier = Tags.getFloatFromTagOrDefault(nbt, Tags.BONUS_DURABILITY_MULTIPLIER, EMPTY.bonusDurabilityMultiplier);

    int upgradeSlots = Tags.getIntFromTagOrDefault(nbt, Tags.FREE_UPGRADE_SLOTS, EMPTY.freeUpgradeSlots);
    int abilitySlots = Tags.getIntFromTagOrDefault(nbt, Tags.FREE_ABILITY_SLOTS, EMPTY.freeAbilitySlots);
    int armorSlots = Tags.getIntFromTagOrDefault(nbt, Tags.FREE_ARMOR_SLOTS, EMPTY.freeArmorSlots);
    int traitSlots = Tags.getIntFromTagOrDefault(nbt, Tags.FREE_TRAIT_SLOTS, EMPTY.freeTraitSlots);

    boolean isBroken = Tags.getBoolFromTagOrDefault(nbt, Tags.BROKEN, EMPTY.broken);

    return new StatsNBT(durability, harvestLevel, attack, miningSpeed,
      repairCount, miningSpeedMultiplier, attackSpeedMultiplier, bonusDurability, bonusDurabilityMultiplier,
      upgradeSlots, abilitySlots, armorSlots, traitSlots,
      isBroken);
  }

  public CompoundNBT serializeToNBT() {
    CompoundNBT nbt = new CompoundNBT();
    nbt.putInt(Tags.DURABILITY, durability);
    nbt.putInt(Tags.HARVEST_LEVEL, harvestLevel);
    nbt.putFloat(Tags.ATTACK, attack);
    nbt.putFloat(Tags.MINING_SPEED, miningSpeed);
    nbt.putFloat(Tags.ATTACK_SPEED_MULTIPLIER, attackSpeedMultiplier);

    nbt.putInt(Tags.REPAIR_COUNT, repairCount);
    nbt.putFloat(Tags.MINING_SPEED_MULTIPLIER, miningSpeedMultiplier);
    nbt.putFloat(Tags.ATTACK_SPEED_MULTIPLIER, attackSpeedMultiplier);
    nbt.putInt(Tags.BONUS_DURABILITY, bonusDurability);
    nbt.putFloat(Tags.BONUS_DURABILITY_MULTIPLIER, bonusDurabilityMultiplier);

    nbt.putInt(Tags.FREE_UPGRADE_SLOTS, freeUpgradeSlots);
    nbt.putInt(Tags.FREE_ABILITY_SLOTS, freeAbilitySlots);
    nbt.putInt(Tags.FREE_ARMOR_SLOTS, freeArmorSlots);
    nbt.putInt(Tags.FREE_TRAIT_SLOTS, freeTraitSlots);

    nbt.putBoolean(Tags.BROKEN, broken);

    return nbt;
  }
}
