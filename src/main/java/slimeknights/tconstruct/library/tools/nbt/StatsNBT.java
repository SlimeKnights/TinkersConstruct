package slimeknights.tconstruct.library.tools.nbt;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraftforge.common.util.Constants;
import slimeknights.tconstruct.library.utils.NBTUtil;

import javax.annotation.Nullable;

/**
 * All the stats that every tool must have.
 * Some may not be used explicitly by all tools (e.g. weapons and harvest  level)
 */
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class StatsNBT {
  static final StatsNBT EMPTY = new StatsNBT(1, 0, 1, 1, 1);

  protected static final String TAG_DURABILITY = "durability";
  protected static final String TAG_ATTACK_DAMAGE = "attack";
  protected static final String TAG_ATTACK_SPEED = "attack_speed_multiplier";
  protected static final String TAG_MINING_SPEED = "mining_speed";
  protected static final String TAG_HARVEST_LEVEL = "harvest_level";

  /** Total durability for the tool */
  @Getter
  private final int durability;
  /** Harvest level when mining on the tool */
  @Getter
  private final int harvestLevel;
  /** Base dealt by the tool */
  @Getter
  private final float attackDamage;
  /** Base mining speed */
  @Getter
  private final float miningSpeed;
  /** Value to multiply by attack speed, larger values are faster */
  @Getter
  private final float attackSpeed;

  /** Parses the stats from NBT */
  public static StatsNBT readFromNBT(@Nullable INBT inbt) {
    if (inbt == null || inbt.getId() != Constants.NBT.TAG_COMPOUND) {
      return EMPTY;
    }

    CompoundNBT nbt = (CompoundNBT)inbt;
    int durability = NBTUtil.getInt(nbt, TAG_DURABILITY, EMPTY.durability);
    int harvestLevel = NBTUtil.getInt(nbt, TAG_HARVEST_LEVEL, EMPTY.harvestLevel);
    float attack = NBTUtil.getFloat(nbt, TAG_ATTACK_DAMAGE, EMPTY.attackDamage);
    float miningSpeed = NBTUtil.getFloat(nbt, TAG_MINING_SPEED, EMPTY.miningSpeed);
    float attackSpeedMultiplier = NBTUtil.getFloat(nbt, TAG_ATTACK_SPEED, EMPTY.attackSpeed);

    return new StatsNBT(durability, harvestLevel, attack, miningSpeed, attackSpeedMultiplier);
  }

  /** Writes these stats to NBT */
  public CompoundNBT serializeToNBT() {
    CompoundNBT nbt = new CompoundNBT();
    nbt.putInt(TAG_DURABILITY, durability);
    nbt.putInt(TAG_HARVEST_LEVEL, harvestLevel);
    nbt.putFloat(TAG_ATTACK_DAMAGE, attackDamage);
    nbt.putFloat(TAG_MINING_SPEED, miningSpeed);
    nbt.putFloat(TAG_ATTACK_SPEED, attackSpeed);

    return nbt;
  }
}
