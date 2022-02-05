package slimeknights.tconstruct.library.materials.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class ComplexTestStats extends BaseMaterialStats {
  private MaterialStatsId identifier;
  private final int num;
  private final float floating;
  private final String text;

  public ComplexTestStats(FriendlyByteBuf buffer) {
    num = buffer.readInt();
    floating = buffer.readFloat();
    text = buffer.readUtf();
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeInt(num);
    buffer.writeFloat(floating);
    buffer.writeUtf(text);
  }

  @Override
  public List<Component> getLocalizedInfo() {
    return new ArrayList<>();
  }

  @Override
  public List<Component> getLocalizedDescriptions() {
    return new ArrayList<>();
  }
}
