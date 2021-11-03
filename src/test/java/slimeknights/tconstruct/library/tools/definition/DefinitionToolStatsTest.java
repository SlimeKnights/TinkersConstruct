package slimeknights.tconstruct.library.tools.definition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.test.BaseMcTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DefinitionToolStatsTest extends BaseMcTest {
  @Test
  void emptyToolStats_ensureEmpty() {
    // ensure no stats present
    assertThat(DefinitionToolStats.EMPTY.containedStats()).isEmpty();
    // ensure defaults
    assertThat(DefinitionToolStats.EMPTY.getStat(ToolStats.DURABILITY, -1)).isEqualTo(-1);
    assertThat(DefinitionToolStats.EMPTY.getStat(ToolStats.ATTACK_DAMAGE, 500)).isEqualTo(500);
  }

  @Test
  void getterAndDefault() {
    DefinitionToolStats stats = DefinitionToolStats
      .builder()
      .addStat(ToolStats.DURABILITY, 100)
      .addStat(ToolStats.MINING_SPEED, 5)
      .build();

    // first check the expected stats are present
    assertThat(stats.containedStats()).hasSize(2);
    assertThat(stats.containedStats()).contains(ToolStats.DURABILITY);
    assertThat(stats.containedStats()).contains(ToolStats.MINING_SPEED);

    // next, try getters with values
    assertThat(stats.getStat(ToolStats.DURABILITY, -1)).isEqualTo(100);
    assertThat(stats.getStat(ToolStats.MINING_SPEED, -1)).isEqualTo(5);

    // finally, defaults
    assertThat(stats.getStat(ToolStats.ATTACK_SPEED, -1)).isEqualTo(-1);
    assertThat(stats.getStat(ToolStats.ATTACK_DAMAGE, 500)).isEqualTo(500);
  }

  @Test
  void bufferReadWrite() {
    DefinitionToolStats stats = DefinitionToolStats
      .builder()
      .addStat(ToolStats.DURABILITY, 10)
      .addStat(ToolStats.ATTACK_SPEED, 2.5f)
      .build();

    PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
    stats.write(buffer);
    DefinitionToolStats decoded = DefinitionToolStats.read(buffer);

    // validate stats parsed
    assertThat(decoded.containedStats()).hasSize(2);
    assertThat(decoded.containedStats()).contains(ToolStats.DURABILITY);
    assertThat(decoded.containedStats()).contains(ToolStats.ATTACK_SPEED);
    assertThat(decoded.getStat(ToolStats.DURABILITY, -1)).isEqualTo(10);
    assertThat(decoded.getStat(ToolStats.ATTACK_SPEED, -1)).isEqualTo(2.5f);
  }

  @Test
  void jsonSerializeTest() {
    DefinitionToolStats stats = DefinitionToolStats
      .builder()
      .addStat(ToolStats.DURABILITY, 10)
      .addStat(ToolStats.ATTACK_SPEED, 2.5f)
      .build();

    JsonElement serialized = DefinitionToolStats.SERIALIZER.serialize(stats, DefinitionToolStats.class, mock(JsonSerializationContext.class));
    assertThat(serialized.isJsonObject()).isTrue();
    JsonObject object = serialized.getAsJsonObject();
    assertThat(JSONUtils.getFloat(object, ToolStats.DURABILITY.getName().toString())).isEqualTo(10);
    assertThat(JSONUtils.getFloat(object, ToolStats.ATTACK_SPEED.getName().toString())).isEqualTo(2.5f);
  }

  @Test
  void jsonDeserializeTest() {
    JsonObject object = new JsonObject();
    object.addProperty(ToolStats.DURABILITY.getName().toString(), 100);
    object.addProperty(ToolStats.ATTACK_SPEED.getName().toString(), 5.5f);

    DefinitionToolStats stats = DefinitionToolStats.SERIALIZER.deserialize(object, DefinitionToolStats.class, mock(JsonDeserializationContext.class));
    assertThat(stats.containedStats()).hasSize(2);
    assertThat(stats.containedStats()).contains(ToolStats.DURABILITY);
    assertThat(stats.containedStats()).contains(ToolStats.ATTACK_SPEED);
    assertThat(stats.getStat(ToolStats.DURABILITY, -1)).isEqualTo(100);
    assertThat(stats.getStat(ToolStats.ATTACK_SPEED, -1)).isEqualTo(5.5f);
  }
}
