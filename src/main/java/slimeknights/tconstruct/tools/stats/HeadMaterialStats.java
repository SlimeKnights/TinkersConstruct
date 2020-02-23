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
public class HeadMaterialStats extends BaseMaterialStats {

  public static final MaterialStatsId ID = new MaterialStatsId(Util.getResource("head"));
  public static final HeadMaterialStats DEFAULT = new HeadMaterialStats(1, 1f, 0, 1f);

  private int durability;
  private float miningSpeed;
  private int harvestLevel;
  private float attack;

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeInt(durability);
    buffer.writeFloat(miningSpeed);
    buffer.writeInt(harvestLevel);
    buffer.writeFloat(attack);
  }

  @Override
  public void decode(PacketBuffer buffer) {
    durability = buffer.readInt();
    miningSpeed = buffer.readFloat();
    harvestLevel = buffer.readInt();
    attack = buffer.readFloat();
  }

  @Override
  public MaterialStatsId getIdentifier() {
    return ID;
  }
}
