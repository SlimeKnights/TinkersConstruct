package slimeknights.tconstruct.tools.stats;

import com.google.common.collect.ImmutableList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import java.util.ArrayList;
import java.util.List;

public class HandleMaterialStats extends BaseMaterialStats {
  public static final MaterialStatsId ID = new MaterialStatsId(Util.getResource("handle"));
  public static final HandleMaterialStats DEFAULT = new HandleMaterialStats(1f, 1f, 1f, 1f);
  // tooltip prefixes
  private static final String DURABILITY_PREFIX = makeTooltipKey("handle.durability");
  private static final String ATTACK_DAMAGE_PREFIX = makeTooltipKey("handle.attack_damage");
  private static final String ATTACK_SPEED_PREFIX = makeTooltipKey("handle.attack_speed");
  private static final String MINING_SPEED_PREFIX = makeTooltipKey("handle.mining_speed");
  // tooltip descriptions
  private static final Text DURABILITY_DESCRIPTION = makeTooltip("handle.durability.description");
  private static final Text ATTACK_DAMAGE_DESCRIPTION = makeTooltip("handle.attack_damage.description");
  private static final Text ATTACK_SPEED_DESCRIPTION = makeTooltip("handle.attack_speed.description");
  private static final Text MINING_SPEED_DESCRIPTION = makeTooltip("handle.mining_speed.description");
  private static final List<Text> DESCRIPTION = ImmutableList.of(DURABILITY_DESCRIPTION, ATTACK_DAMAGE_DESCRIPTION, ATTACK_SPEED_DESCRIPTION, MINING_SPEED_DESCRIPTION);

  // multipliers
  private float durability;
  private float miningSpeed;
  private float attackSpeed;
  private float attackDamage;

    public HandleMaterialStats(float durability, float miningSpeed, float attackSpeed, float attackDamage) {
        this.durability = durability;
        this.miningSpeed = miningSpeed;
        this.attackSpeed = attackSpeed;
        this.attackDamage = attackDamage;
    }

    public HandleMaterialStats() {
    }

    @Override
  public void encode(PacketByteBuf buffer) {
    buffer.writeFloat(this.durability);
    buffer.writeFloat(this.attackDamage);
    buffer.writeFloat(this.attackSpeed);
    buffer.writeFloat(this.miningSpeed);
  }

  @Override
  public void decode(PacketByteBuf buffer) {
    this.durability = buffer.readFloat();
    this.attackDamage = buffer.readFloat();
    this.attackSpeed = buffer.readFloat();
    this.miningSpeed = buffer.readFloat();
  }

  @Override
  public MaterialStatsId getIdentifier() {
    return ID;
  }

  @Override
  public List<Text> getLocalizedInfo() {
    List<Text> list = new ArrayList<>();
    list.add(formatDurability(this.durability));
    list.add(formatAttackDamage(this.attackDamage));
    list.add(formatAttackSpeed(this.attackSpeed));
    list.add(formatMiningSpeed(this.miningSpeed));
    return list;
  }

  @Override
  public List<Text> getLocalizedDescriptions() {
    return DESCRIPTION;
  }

  /** Applies formatting for durability */
  public static Text formatDurability(float quality) {
    return formatColoredMultiplier(DURABILITY_PREFIX, quality);
  }

  /** Applies formatting for attack speed */
  public static Text formatAttackDamage(float quality) {
    return formatColoredMultiplier(ATTACK_DAMAGE_PREFIX, quality);
  }

  /** Applies formatting for attack speed */
  public static Text formatAttackSpeed(float quality) {
    return formatColoredMultiplier(ATTACK_SPEED_PREFIX, quality);
  }

  /** Applies formatting for mining speed */
  public static Text formatMiningSpeed(float quality) {
    return formatColoredMultiplier(MINING_SPEED_PREFIX, quality);
  }

    public float getDurability() {
        return this.durability;
    }

    public float getMiningSpeed() {
        return this.miningSpeed;
    }

    public float getAttackSpeed() {
        return this.attackSpeed;
    }

    public float getAttackDamage() {
        return this.attackDamage;
    }

    public HandleMaterialStats withDurability(float durability) {
        return this.durability == durability ? this : new HandleMaterialStats(durability, this.miningSpeed, this.attackSpeed, this.attackDamage);
    }

    public HandleMaterialStats withMiningSpeed(float miningSpeed) {
        return this.miningSpeed == miningSpeed ? this : new HandleMaterialStats(this.durability, miningSpeed, this.attackSpeed, this.attackDamage);
    }

    public HandleMaterialStats withAttackSpeed(float attackSpeed) {
        return this.attackSpeed == attackSpeed ? this : new HandleMaterialStats(this.durability, this.miningSpeed, attackSpeed, this.attackDamage);
    }

    public HandleMaterialStats withAttackDamage(float attackDamage) {
        return this.attackDamage == attackDamage ? this : new HandleMaterialStats(this.durability, this.miningSpeed, this.attackSpeed, attackDamage);
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof HandleMaterialStats)) return false;
        final HandleMaterialStats other = (HandleMaterialStats) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        if (Float.compare(this.getDurability(), other.getDurability()) != 0) return false;
        if (Float.compare(this.getMiningSpeed(), other.getMiningSpeed()) != 0) return false;
        if (Float.compare(this.getAttackSpeed(), other.getAttackSpeed()) != 0) return false;
        if (Float.compare(this.getAttackDamage(), other.getAttackDamage()) != 0) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof HandleMaterialStats;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        result = result * PRIME + Float.floatToIntBits(this.getDurability());
        result = result * PRIME + Float.floatToIntBits(this.getMiningSpeed());
        result = result * PRIME + Float.floatToIntBits(this.getAttackSpeed());
        result = result * PRIME + Float.floatToIntBits(this.getAttackDamage());
        return result;
    }

    public String toString() {
        return "HandleMaterialStats(durability=" + this.getDurability() + ", miningSpeed=" + this.getMiningSpeed() + ", attackSpeed=" + this.getAttackSpeed() + ", attackDamage=" + this.getAttackDamage() + ")";
    }
}
