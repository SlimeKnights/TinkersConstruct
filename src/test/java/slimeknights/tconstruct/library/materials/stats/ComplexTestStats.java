package slimeknights.tconstruct.library.materials.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.network.PacketBuffer;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ComplexTestStats extends BaseMaterialStats {

  private MaterialStatsId identifier;
  private int num;
  private float floating;
  private String text;

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeInt(num);
    buffer.writeFloat(floating);
    buffer.writeString(text);
  }

  @Override
  public void decode(PacketBuffer buffer) {
    num = buffer.readInt();
    floating = buffer.readFloat();
    text = buffer.readString();
  }
}
