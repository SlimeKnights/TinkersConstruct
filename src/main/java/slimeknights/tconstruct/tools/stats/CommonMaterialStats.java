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
public class CommonMaterialStats extends BaseMaterialStats {

  public static final MaterialStatsId ID = new MaterialStatsId(Util.getResource("common"));
  public static final CommonMaterialStats DEFAULT = new CommonMaterialStats(1, 1f);

  private int durability;
  private float attack;

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeInt(durability);
    buffer.writeFloat(attack);
  }

  @Override
  public void decode(PacketBuffer buffer) {
    durability = buffer.readInt();
    attack = buffer.readFloat();
  }

  @Override
  public MaterialStatsId getIdentifier() {
    return ID;
  }
}
