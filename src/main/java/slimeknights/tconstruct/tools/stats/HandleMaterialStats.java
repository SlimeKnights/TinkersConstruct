package slimeknights.tconstruct.tools.stats;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
@With
public class HandleMaterialStats extends BaseMaterialStats {
  public static final MaterialStatsId ID = new MaterialStatsId(Util.getResource("handle"));
  public static final HandleMaterialStats DEFAULT = new HandleMaterialStats(1f, 1f, 1f, 1f);
  // tooltip prefixes
  private static final String DURABILITY_PREFIX = makeTooltipKey("handle.durability");
  private static final String ATTACK_DAMAGE_PREFIX = makeTooltipKey("handle.attack_damage");
  private static final String ATTACK_SPEED_PREFIX = makeTooltipKey("handle.attack_speed");
  private static final String MINING_SPEED_PREFIX = makeTooltipKey("handle.mining_speed");
  private static final ITextComponent DEFAULT_STATS = makeTooltip("handle.default_stats");
  // tooltip descriptions
  private static final ITextComponent DURABILITY_DESCRIPTION = makeTooltip("handle.durability.description");
  private static final ITextComponent ATTACK_DAMAGE_DESCRIPTION = makeTooltip("handle.durability.attack_damage");
  private static final ITextComponent ATTACK_SPEED_DESCRIPTION = makeTooltip("handle.durability.attack_speed");
  private static final ITextComponent MINING_SPEED_DESCRIPTION = makeTooltip("handle.durability.mining_speed");
  private static final List<ITextComponent> DESCRIPTION = ImmutableList.of(DURABILITY_DESCRIPTION, ATTACK_DAMAGE_DESCRIPTION, ATTACK_SPEED_DESCRIPTION, MINING_SPEED_DESCRIPTION);

  // multipliers
  private float durability;
  private float miningSpeed;
  private float attackSpeed;
  private float attackDamage;

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeFloat(this.durability);
    buffer.writeFloat(this.attackDamage);
    buffer.writeFloat(this.attackSpeed);
    buffer.writeFloat(this.miningSpeed);
  }

  @Override
  public void decode(PacketBuffer buffer) {
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
  public List<ITextComponent> getLocalizedInfo() {
    List<ITextComponent> list = new ArrayList<>();
    if (this.durability != 1) list.add(formatDurability(this.durability));
    if (this.attackDamage != 1) list.add(formatAttackDamage(this.attackDamage));
    if (this.attackSpeed != 1) list.add(formatAttackSpeed(this.attackSpeed));
    if (this.miningSpeed != 1) list.add(formatMiningSpeed(this.miningSpeed));
    if (list.isEmpty()) {
      list.add(DEFAULT_STATS);
    }
    return list;
  }

  @Override
  public List<ITextComponent> getLocalizedDescriptions() {
    return DESCRIPTION;
  }

  /** Applies formatting for durability */
  public static ITextComponent formatDurability(float quality) {
    return formatColoredPercent(DURABILITY_PREFIX, quality);
  }

  /** Applies formatting for attack speed */
  public static ITextComponent formatAttackDamage(float quality) {
    return formatColoredPercent(ATTACK_DAMAGE_PREFIX, quality);
  }

  /** Applies formatting for attack speed */
  public static ITextComponent formatAttackSpeed(float quality) {
    return formatColoredPercent(ATTACK_SPEED_PREFIX, quality);
  }

  /** Applies formatting for mining speed */
  public static ITextComponent formatMiningSpeed(float quality) {
    return formatColoredPercent(MINING_SPEED_PREFIX, quality);
  }
}
