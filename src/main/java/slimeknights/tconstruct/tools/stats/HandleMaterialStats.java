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
import net.minecraft.util.text.TranslationTextComponent;
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

  public final static String DURABILITY_MULTIPLIER_LOCALIZATION = "stat.handle.durability.multiplier.name";
  public final static String ATTACK_SPEED_LOCALIZATION = "stat.handle.speed.attack.name";
  public final static String MINING_SPEED_LOCALIZATION = "stat.handle.speed.mining.name";

  public final static String DURABILITY_MULTIPLIER_DESCRIPTION_LOCALIZATION = "stat.handle.durability.multiplier.description";
  public final static String ATTACK_SPEED_DESCRIPTION_LOCALIZATION = "stat.handle.speed.attack.description";
  public final static String MINING_SPEED_DESCRIPTION_LOCALIZATION = "stat.handle.speed.mining.description";

  public final static Color DURABILITY_COLOR = HeadMaterialStats.DURABILITY_COLOR;
  public final static Color MODIFIER_COLOR = Color.fromInt(0xFFB9B95A);

  private float durabilityMultiplier;
  private float miningSpeedMultiplier;
  private float attackSpeedMultiplier;

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeFloat(this.durabilityMultiplier);
    buffer.writeFloat(this.miningSpeedMultiplier);
    buffer.writeFloat(this.attackSpeedMultiplier);
  }

  @Override
  public void decode(PacketBuffer buffer) {
    this.durabilityMultiplier = buffer.readFloat();
    this.miningSpeedMultiplier = buffer.readFloat();
    this.attackSpeedMultiplier = buffer.readFloat();
  }

  @Override
  public MaterialStatsId getIdentifier() {
    return ID;
  }

  //TODO: Adjust this to be more consistent with new ingame stats
  @Override
  public List<ITextComponent> getLocalizedInfo() {
    return ImmutableList.of(formatDurabilityMultiplier(this.durabilityMultiplier),
      formatAttackSpeed(this.attackSpeedMultiplier),
      formatMiningSpeed(this.miningSpeedMultiplier));
  }

  @Override
  public List<ITextComponent> getLocalizedDescriptions() {
    return ImmutableList.of(new TranslationTextComponent(DURABILITY_MULTIPLIER_DESCRIPTION_LOCALIZATION),
      new TranslationTextComponent(ATTACK_SPEED_DESCRIPTION_LOCALIZATION),
      new TranslationTextComponent(MINING_SPEED_DESCRIPTION_LOCALIZATION));
  }

  public static ITextComponent formatDurabilityMultiplier(float quality) {
    return formatNumber(DURABILITY_MULTIPLIER_LOCALIZATION, MODIFIER_COLOR, quality);
  }

  public static ITextComponent formatAttackSpeed(float quality) {
    return formatNumber(ATTACK_SPEED_LOCALIZATION, MODIFIER_COLOR, quality);
  }

  public static ITextComponent formatMiningSpeed(float quality) {
    return formatNumber(MINING_SPEED_LOCALIZATION, MODIFIER_COLOR, quality);
  }
}
