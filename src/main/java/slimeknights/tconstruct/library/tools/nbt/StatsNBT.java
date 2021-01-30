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
  final static StatsNBT EMPTY = new StatsNBT(1, 0, 1, 1, 1);

  protected static final String TAG_DURABILITY = "durability";
  protected static final String TAG_ATTACK = "attack";
  protected static final String TAG_ATTACK_SPEED_MULTIPLIER = "attack_speed_multiplier";
  protected static final String TAG_MINING_SPEED = "mining_speed";
  protected static final String TAG_HARVEST_LEVEL = "harvest_level";

  @Getter
  public final int durability;
  @Getter
  public final int harvestLevel;
  @Getter
  public final float attackDamage;
  @Getter
  public final float miningSpeed;
  @Getter
  public final float attackSpeedMultiplier;

  public static StatsNBT readFromNBT(@Nullable INBT inbt) {
    if (inbt == null || inbt.getId() != Constants.NBT.TAG_COMPOUND) {
      return EMPTY;
    }

    CompoundNBT nbt = (CompoundNBT)inbt;
    int durability = NBTUtil.getInt(nbt, TAG_DURABILITY, EMPTY.durability);
    int harvestLevel = NBTUtil.getInt(nbt, TAG_HARVEST_LEVEL, EMPTY.harvestLevel);
    float attack = NBTUtil.getFloat(nbt, TAG_ATTACK, EMPTY.attackDamage);
    float miningSpeed = NBTUtil.getFloat(nbt, TAG_MINING_SPEED, EMPTY.miningSpeed);
    float attackSpeedMultiplier = NBTUtil.getFloat(nbt, TAG_ATTACK_SPEED_MULTIPLIER, EMPTY.attackSpeedMultiplier);

    return new StatsNBT(durability, harvestLevel, attack, miningSpeed, attackSpeedMultiplier);
  }


  public CompoundNBT serializeToNBT() {
    CompoundNBT nbt = new CompoundNBT();
    nbt.putInt(TAG_DURABILITY, durability);
    nbt.putInt(TAG_HARVEST_LEVEL, harvestLevel);
    nbt.putFloat(TAG_ATTACK, attackDamage);
    nbt.putFloat(TAG_MINING_SPEED, miningSpeed);
    nbt.putFloat(TAG_ATTACK_SPEED_MULTIPLIER, attackSpeedMultiplier);

    return nbt;
  }
}
