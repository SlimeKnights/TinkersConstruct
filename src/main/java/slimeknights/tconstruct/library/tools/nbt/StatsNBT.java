package slimeknights.tconstruct.library.tools.nbt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraftforge.common.util.Constants;
import slimeknights.tconstruct.library.utils.NBTUtil;

import javax.annotation.Nullable;

/**
 * All the stats that every tool must have.
 * Some may not be used explicitly by all tools (e.g. weapons and harvest  level)
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode
@ToString
@Getter
public class StatsNBT {
  static final StatsNBT EMPTY = new StatsNBT(1, 0, 1, 1, 1, 5.0f);

  protected static final String TAG_DURABILITY = "durability";
  protected static final String TAG_ATTACK_DAMAGE = "attack";
  protected static final String TAG_ATTACK_SPEED = "attack_speed";
  protected static final String TAG_MINING_SPEED = "mining_speed";
  protected static final String TAG_HARVEST_LEVEL = "harvest_level";
  protected static final String TAG_REACH = "reach";

  /** Total durability for the tool */
  private final int durability;
  /** Harvest level when mining on the tool */
  private final int harvestLevel;
  /** Base dealt by the tool */
  private final float attackDamage;
  /** Base mining speed */
  private final float miningSpeed;
  /** Value to multiply by attack speed, larger values are faster */
  private final float attackSpeed;
  /** Number of blocks you can reach holding this tool, base is 5 blocks */
  private final float reach;

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
    float reach = NBTUtil.getFloat(nbt, TAG_REACH, EMPTY.reach);
    return new StatsNBT(durability, harvestLevel, attack, miningSpeed, attackSpeedMultiplier, reach);
  }

  /** Writes these stats to NBT */
  public CompoundNBT serializeToNBT() {
    CompoundNBT nbt = new CompoundNBT();
    nbt.putInt(TAG_DURABILITY, durability);
    nbt.putInt(TAG_HARVEST_LEVEL, harvestLevel);
    nbt.putFloat(TAG_ATTACK_DAMAGE, attackDamage);
    nbt.putFloat(TAG_MINING_SPEED, miningSpeed);
    nbt.putFloat(TAG_ATTACK_SPEED, attackSpeed);
    nbt.putFloat(TAG_REACH, reach);
    return nbt;
  }

  /** Creates a new stats builder */
  public static Builder builder() {
    return new Builder();
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @Accessors(chain = true, fluent = true)
  @Setter
  public static class Builder {
    private int durability = EMPTY.durability;
    private int harvestLevel = EMPTY.harvestLevel;
    private float attackDamage = EMPTY.attackDamage;
    private float miningSpeed = EMPTY.miningSpeed;
    private float attackSpeed = EMPTY.attackSpeed;
    private float reach = EMPTY.reach;

    /** Builds the stats from the given values */
    public StatsNBT build() {
      return new StatsNBT(durability, harvestLevel, attackDamage, miningSpeed, attackSpeed, reach);
    }
  }
}
