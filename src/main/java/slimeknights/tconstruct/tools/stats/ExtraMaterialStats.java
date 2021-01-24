package slimeknights.tconstruct.tools.stats;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import java.util.List;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class ExtraMaterialStats extends BaseMaterialStats {
  public static final MaterialStatsId ID = new MaterialStatsId(Util.getResource("extra"));
  public static final ExtraMaterialStats DEFAULT = new ExtraMaterialStats();

  // no stats

  @Override
  public void encode(PacketBuffer buffer) {}

  @Override
  public void decode(PacketBuffer buffer) {}

  @Override
  public MaterialStatsId getIdentifier() {
    return ID;
  }

  @Override
  public List<ITextComponent> getLocalizedInfo() {
    return ImmutableList.of();
  }

  @Override
  public List<ITextComponent> getLocalizedDescriptions() {
    return ImmutableList.of();
  }
}
