package slimeknights.tconstruct.tools.stats;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
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

  public final static String DURABILITY_LOCALIZATION = "stat.head.durability.name";
  public final static String MINING_SPEED_LOCALIZATION = "stat.head.mining_speed.name";
  public final static String ATTACK_LOCALIZATION = "stat.head.attack.name";
  public final static String HARVEST_LEVEL_LOCALIZATION = "stat.head.harvest_level.name";

  public final static String DURABILITY_DESCRIPTION_LOCALIZATION = "stat.head.durability.description";
  public final static String MINING_SPEED_DESCRIPTION_LOCALIZATION = "stat.head.mining_speed.description";
  public final static String ATTACK_DESCRIPTION_LOCALIZATION = "stat.head.attack.description";
  public final static String HARVEST_LEVEL_DESCRIPTION_LOCALIZATION = "stat.head.harvest_level.description";

  public final static String DURABILITY_COLOR = CustomFontColor.valueToColorCode(1f);
  public final static String ATTACK_COLOR = CustomFontColor.encodeColor(215, 100, 100);
  public final static String SPEED_COLOR = CustomFontColor.encodeColor(120, 160, 205);

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

  public static ITextComponent formatDurability(int durability) {
    return formatNumber(DURABILITY_LOCALIZATION, DURABILITY_COLOR, durability);
  }

  public static ITextComponent formatDurability(int durability, int ref) {
    return new TranslationTextComponent(DURABILITY_LOCALIZATION)
      .appendText(CustomFontColor.formatPartialAmount(durability, ref));
  }

  public static ITextComponent formatHarvestLevel(int level) {
    return new TranslationTextComponent(HARVEST_LEVEL_LOCALIZATION)
      .appendSibling(HarvestLevels.getHarvestLevelName(level));
  }

  public static ITextComponent formatMiningSpeed(float speed) {
    return formatNumber(MINING_SPEED_LOCALIZATION, SPEED_COLOR, speed);
  }

  public static ITextComponent formatAttack(float attack) {
    return formatNumber(ATTACK_LOCALIZATION, ATTACK_COLOR, attack);
  }

  @Override
  public List<ITextComponent> getLocalizedDescriptions() {
    List<ITextComponent> info = Lists.newArrayList();

    info.add(new TranslationTextComponent(DURABILITY_DESCRIPTION_LOCALIZATION));
    info.add(new TranslationTextComponent(HARVEST_LEVEL_DESCRIPTION_LOCALIZATION));
    info.add(new TranslationTextComponent(MINING_SPEED_DESCRIPTION_LOCALIZATION));
    info.add(new TranslationTextComponent(ATTACK_DESCRIPTION_LOCALIZATION));

    return info;
  }
}
