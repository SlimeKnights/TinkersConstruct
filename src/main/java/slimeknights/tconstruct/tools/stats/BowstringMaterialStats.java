package slimeknights.tconstruct.tools.stats;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class BowstringMaterialStats extends BaseMaterialStats {
  public static final MaterialStatsId ID = new MaterialStatsId(TConstruct.getResource("bowstring"));
  public static final BowstringMaterialStats DEFAULT = new BowstringMaterialStats();
  private static final List<Component> LOCALIZED = Collections.singletonList(makeTooltip(TConstruct.getResource("extra.no_stats")));
  private static final List<Component> DESCRIPTION = Collections.singletonList(TextComponent.EMPTY);

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
