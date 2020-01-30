package slimeknights.tconstruct.tools.stats;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.network.PacketBuffer;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CommonMaterialStats extends BaseMaterialStats {

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
}
