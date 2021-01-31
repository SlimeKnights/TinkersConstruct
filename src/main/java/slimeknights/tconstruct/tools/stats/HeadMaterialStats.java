package slimeknights.tconstruct.tools.stats;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.renderer.font.CustomFontColor;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.utils.HarvestLevels;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class HeadMaterialStats extends BaseMaterialStats {
  public static final MaterialStatsId ID = new MaterialStatsId(Util.getResource("head"));
  public static final HeadMaterialStats DEFAULT = new HeadMaterialStats(1, 1f, 0, 1f);
  // tooltip prefixes
  public static final String DURABILITY_PREFIX = makeTooltipKey("head.durability");
  private static final String MINING_SPEED_PREFIX = makeTooltipKey("head.mining_speed");
  private static final String ATTACK_PREFIX = makeTooltipKey("head.attack");
  public final static String ATTACK_SPEED_LOCALIZATION = "stat.head.attack_speed.name";
  private static final String HARVEST_LEVEL_PREFIX = makeTooltipKey("head.harvest_level");
  // tooltip descriptions
  private static final ITextComponent DURABILITY_DESCRIPTION = makeTooltip("head.durability.description");
  private static final ITextComponent MINING_SPEED_DESCRIPTION = makeTooltip("head.mining_speed.description");
  private static final ITextComponent ATTACK_DESCRIPTION = makeTooltip("head.attack.description");
  public final static String ATTACK_SPEED_DESCRIPTION_LOCALIZATION = "stat.head.attack_speed.description";
  private static final ITextComponent HARVEST_LEVEL_DESCRIPTION = makeTooltip("head.harvest_level.description");
  private static final List<ITextComponent> DESCRIPTION = ImmutableList.of(DURABILITY_DESCRIPTION, MINING_SPEED_DESCRIPTION, ATTACK_DESCRIPTION, HARVEST_LEVEL_DESCRIPTION);

  public final static Color DURABILITY_COLOR = Color.fromInt(0xFF47cc47);
  public final static Color MINING_SPEED_COLOR = Color.fromInt(0xFF78A0CD);
  public final static Color ATTACK_COLOR = Color.fromInt(0xFFD76464);
  public final static Color ATTACK_SPEED_COLOR = Color.fromInt(0xFF8547CC);

  private int durability;
  private float miningSpeed;
  private int harvestLevel;
  private float attack;

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeInt(this.durability);
    buffer.writeFloat(this.miningSpeed);
    buffer.writeInt(this.harvestLevel);
    buffer.writeFloat(this.attack);
  }

  @Override
  public void decode(PacketBuffer buffer) {
    this.durability = buffer.readInt();
    this.miningSpeed = buffer.readFloat();
    this.harvestLevel = buffer.readInt();
    this.attack = buffer.readFloat();
  }

  @Override
  public MaterialStatsId getIdentifier() {
    return ID;
  }

  @Override
  public List<ITextComponent> getLocalizedInfo() {
    List<ITextComponent> info = Lists.newArrayList();
    info.add(formatDurability(this.durability));
    info.add(formatHarvestLevel(this.harvestLevel));
    info.add(formatMiningSpeed(this.miningSpeed));
    info.add(formatAttack(this.attack));
    return info;
  }

  @Override
  public List<ITextComponent> getLocalizedDescriptions() {
    return DESCRIPTION;
  }

  /** Applies formatting for durability */
  public static ITextComponent formatDurability(int durability) {
    return formatNumber(DURABILITY_PREFIX, DURABILITY_COLOR, durability);
  }

  /** Applies formatting for durability with a reference durability */
  public static ITextComponent formatDurability(int durability, int ref) {
    return new TranslationTextComponent(DURABILITY_PREFIX).append(CustomFontColor.formatPartialAmount(durability, ref));
  }

  /** Applies formatting for harvest level */
  public static ITextComponent formatHarvestLevel(int level) {
    return new TranslationTextComponent(HARVEST_LEVEL_PREFIX).append(HarvestLevels.getHarvestLevelName(level));
  }

  /** Applies formatting for mining speed */
  public static ITextComponent formatMiningSpeed(float speed) {
    return formatNumber(MINING_SPEED_PREFIX, MINING_SPEED_COLOR, speed);
  }

  /** Applies formatting for attack */
  public static ITextComponent formatAttack(float attack) {
    return formatNumber(ATTACK_PREFIX, ATTACK_COLOR, attack);
  }

  public static ITextComponent formatAttackSpeed(float attack) {
    return formatNumber(ATTACK_SPEED_LOCALIZATION, ATTACK_SPEED_COLOR, attack);
  }
}
