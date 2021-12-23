package slimeknights.tconstruct.tools.stats;


import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.IRepairableMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.Collections;
import java.util.List;

/** Internal stat type to make a material repairable without making it a head material. Only required if you use no other repairable material stat type */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class RepairKitStats extends BaseMaterialStats implements IRepairableMaterialStats {
  public static final MaterialStatsId ID = new MaterialStatsId(TConstruct.getResource("repair_kit"));
  private static final List<ITextComponent> DESCRIPTION = ImmutableList.of(ToolStats.DURABILITY.getDescription());
  public static final RepairKitStats DEFAULT = new RepairKitStats(1);

  @Getter
  private int durability;

  @SuppressWarnings("unused") // used by reflection
  public RepairKitStats() {
    this(1);
  }

  @Override
  public MaterialStatsId getIdentifier() {
    return ID;
  }

  @Override
  public List<ITextComponent> getLocalizedInfo() {
    return Collections.singletonList(ToolStats.DURABILITY.formatValue(this.durability));
  }

  @Override
  public List<ITextComponent> getLocalizedDescriptions() {
    return DESCRIPTION;
  }

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeInt(this.durability);
  }

  @Override
  public void decode(PacketBuffer buffer) {
    this.durability = buffer.readInt();
  }
}
