package slimeknights.tconstruct.tools.stats;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class ExtraMaterialStats extends BaseMaterialStats {
  public static final MaterialStatsId ID = new MaterialStatsId(Util.getResource("extra"));
  public static final ExtraMaterialStats DEFAULT = new ExtraMaterialStats();
  private static final Text NO_STATS = makeTooltip("extra.no_stats");
  private static final List<Text> LOCALIZED = Collections.singletonList(NO_STATS);
  private static final List<Text> DESCRIPTION = Collections.singletonList(LiteralText.EMPTY);

  // no stats

  @Override
  public void encode(PacketByteBuf buffer) {}

  @Override
  public void decode(PacketByteBuf buffer) {}

  @Override
  public MaterialStatsId getIdentifier() {
    return ID;
  }

  @Override
  public List<Text> getLocalizedInfo() {
    return LOCALIZED;
  }

  @Override
  public List<Text> getLocalizedDescriptions() {
    return DESCRIPTION;
  }
}
