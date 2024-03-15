package slimeknights.tconstruct.tools.stats;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class ExtraMaterialStats extends BaseMaterialStats {
  public static final MaterialStatsId ID = new MaterialStatsId(TConstruct.getResource("extra"));
  public static final ExtraMaterialStats DEFAULT = new ExtraMaterialStats();
  private static final Component NO_STATS = makeTooltip(TConstruct.getResource("extra.no_stats"));
  private static final List<Component> LOCALIZED = Collections.singletonList(NO_STATS);
  private static final List<Component> DESCRIPTION = Collections.singletonList(Component.empty());

  // no stats

  @Override
  public void encode(FriendlyByteBuf buffer) {}

  @Override
  public MaterialStatsId getIdentifier() {
    return ID;
  }

  @Override
  public List<Component> getLocalizedInfo() {
    return LOCALIZED;
  }

  @Override
  public List<Component> getLocalizedDescriptions() {
    return DESCRIPTION;
  }
}
