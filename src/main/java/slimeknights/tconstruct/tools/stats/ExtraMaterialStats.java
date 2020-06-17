package slimeknights.tconstruct.tools.stats;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minecraft.network.PacketBuffer;
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
public class ExtraMaterialStats extends BaseMaterialStats {

  public static final MaterialStatsId ID = new MaterialStatsId(Util.getResource("extra"));
  public static final ExtraMaterialStats DEFAULT = new ExtraMaterialStats(0);

  public final static String DURABILITY_LOCALIZATION = "stat.extra.durability.description";
  public final static String DURABILITY_DESCRIPTION_LOCALIZATION = "stat.extra.durability.description";
  public final static String DURABILITY_COLOR = HeadMaterialStats.DURABILITY_COLOR;

  private int durability;

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeInt(this.durability);
  }

  @Override
  public void decode(PacketBuffer buffer) {
    this.durability = buffer.readInt();
  }

  @Override
  public MaterialStatsId getIdentifier() {
    return ID;
  }

  @Override
  public List<String> getLocalizedInfo() {
    return ImmutableList.of(formatDurability(this.durability));
  }

  @Override
  public List<String> getLocalizedDesc() {
    return ImmutableList.of(new TranslationTextComponent(DURABILITY_DESCRIPTION_LOCALIZATION).getFormattedText());
  }

  public static String formatDurability(int durability) {
    return formatNumber(DURABILITY_LOCALIZATION, DURABILITY_COLOR, durability);
  }
}
