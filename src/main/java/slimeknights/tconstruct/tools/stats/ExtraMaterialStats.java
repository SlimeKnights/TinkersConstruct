package slimeknights.tconstruct.tools.stats;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minecraft.network.PacketBuffer;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class ExtraMaterialStats extends BaseMaterialStats {

  public static final MaterialStatsId ID = new MaterialStatsId(Util.getResource("extra"));
  public static final ExtraMaterialStats DEFAULT = new ExtraMaterialStats(0);

  private int durability;

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeInt(durability);
  }

  @Override
  public void decode(PacketBuffer buffer) {
    durability = buffer.readInt();
  }

  @Override
  public MaterialStatsId getIdentifier() {
    return ID;
  }
}
