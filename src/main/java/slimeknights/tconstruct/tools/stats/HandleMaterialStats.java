package slimeknights.tconstruct.tools.stats;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class HandleMaterialStats extends BaseMaterialStats {
  public static final MaterialStatsId ID = new MaterialStatsId(Util.getResource("handle"));
  public static final HandleMaterialStats DEFAULT = new HandleMaterialStats(1f, 1f, 1f);
  // tooltip prefixes
  private static final String DURABILITY_PREFIX = makeTooltipKey("handle.durability");
  private static final String ATTACK_SPEED_PREFIX = makeTooltipKey("handle.attack_speed");
  private static final String MINING_SPEED_PREFIX = makeTooltipKey("handle.mining_speed");
  // tooltip descriptions
  private static final ITextComponent DURABILITY_DESCRIPTION = makeTooltip("handle.durability.description");
  private static final ITextComponent ATTACK_SPEED_DESCRIPTION = makeTooltip("handle.durability.attack_speed");
  private static final ITextComponent MINING_SPEED_DESCRIPTION = makeTooltip("handle.durability.mining_speed");
  private static final List<ITextComponent> DESCRIPTION = ImmutableList.of(DURABILITY_DESCRIPTION, ATTACK_SPEED_DESCRIPTION, MINING_SPEED_DESCRIPTION);
  // colors
  public final static Color DURABILITY_COLOR = HeadMaterialStats.DURABILITY_COLOR;
  public final static Color ATTACK_SPEED_COLOR = Color.fromInt(0xFFB9B95A);
  public final static Color MINING_SPEED_COLOR = HeadMaterialStats.MINING_SPEED_COLOR;

  // multipliers
  private float durability;
  private float attackSpeed;
  private float miningSpeed;

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeFloat(this.durability);
    buffer.writeFloat(this.attackSpeed);
    buffer.writeFloat(this.miningSpeed);
  }

  @Override
  public void decode(PacketBuffer buffer) {
    this.durability = buffer.readFloat();
    this.attackSpeed = buffer.readFloat();
    this.miningSpeed = buffer.readFloat();
  }

  @Override
  public MaterialStatsId getIdentifier() {
    return ID;
  }

  @Override
  public List<ITextComponent> getLocalizedInfo() {
    return ImmutableList.of(formatDurability(this.durability), formatAttackSpeed(this.attackSpeed), formatMiningSpeed(this.miningSpeed));
  }

  @Override
  public List<ITextComponent> getLocalizedDescriptions() {
    return DESCRIPTION;
  }

  /** Applies formatting for durability */
  public static ITextComponent formatDurability(float quality) {
    return formatNumberPercent(DURABILITY_PREFIX, DURABILITY_COLOR, quality);
  }

  /** Applies formatting for attack speed */
  public static ITextComponent formatAttackSpeed(float quality) {
    return formatNumberPercent(ATTACK_SPEED_PREFIX, ATTACK_SPEED_COLOR, quality);
  }

  /** Applies formatting for mining speed */
  public static ITextComponent formatMiningSpeed(float quality) {
    return formatNumberPercent(MINING_SPEED_PREFIX, MINING_SPEED_COLOR, quality);
  }
}
