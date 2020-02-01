package slimeknights.tconstruct.tools.stats;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.network.PacketBuffer;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HarvestMaterialStats extends BaseMaterialStats {

  public static final MaterialStatsId ID = new MaterialStatsId(Util.getResource("harvest"));
  public static final HarvestMaterialStats DEFAULT = new HarvestMaterialStats(1f, 0);

  private float miningSpeed;
  private int harvestLevel;

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeFloat(miningSpeed);
    buffer.writeInt(harvestLevel);
  }

  @Override
  public void decode(PacketBuffer buffer) {
    miningSpeed = buffer.readFloat();
    harvestLevel = buffer.readInt();
  }

  @Override
  public MaterialStatsId getIdentifier() {
    return ID;
  }
}
