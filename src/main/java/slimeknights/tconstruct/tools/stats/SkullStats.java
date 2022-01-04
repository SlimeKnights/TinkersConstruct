package slimeknights.tconstruct.tools.stats;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.IRepairableMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.Arrays;
import java.util.List;

/** Stats for slimeskull skulls */
@Getter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class SkullStats extends BaseMaterialStats implements IRepairableMaterialStats {
  public static final MaterialStatsId ID = new MaterialStatsId(TConstruct.getResource("skull"));
  public static final SkullStats DEFAULT = new SkullStats(1, 0);
  // tooltip descriptions
  private static final List<Component> DESCRIPTION = ImmutableList.of(ToolStats.DURABILITY.getDescription(), ToolStats.ARMOR.getDescription());

  private final int durability;
  private final int armor;

  public SkullStats(FriendlyByteBuf buffer) {
    this.durability = buffer.readInt();
    this.armor = buffer.readInt();
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeInt(this.durability);
    buffer.writeInt(this.armor);
  }

  @Override
  public MaterialStatsId getIdentifier() {
    return ID;
  }

  @Override
  public List<Component> getLocalizedInfo() {
    return Arrays.asList(ToolStats.DURABILITY.formatValue(this.durability),
                         ToolStats.ARMOR.formatValue(this.armor));
  }

  @Override
  public List<Component> getLocalizedDescriptions() {
    return DESCRIPTION;
  }
}
