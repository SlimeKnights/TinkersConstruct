package slimeknights.tconstruct.library.tools.definition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.test.BaseMcTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DefinitionModifierSlotsTest extends BaseMcTest {
  @Test
  void emptyModifierSlots_ensureEmpty() {
    // ensure no stats present
    assertThat(DefinitionModifierSlots.EMPTY.containedTypes()).isEmpty();
    // ensure defaults
    assertThat(DefinitionModifierSlots.EMPTY.getSlots(SlotType.UPGRADE)).isEqualTo(0);
  }

  @Test
  void getterAndDefault() {
    DefinitionModifierSlots slots = DefinitionModifierSlots
      .builder()
      .setSlots(SlotType.UPGRADE, 4)
      .setSlots(SlotType.ABILITY, 2)
      .build();

    // first check the expected stats are present
    assertThat(slots.containedTypes()).hasSize(2);
    assertThat(slots.containedTypes()).contains(SlotType.UPGRADE);
    assertThat(slots.containedTypes()).contains(SlotType.ABILITY);

    // next, try getters with values
    assertThat(slots.getSlots(SlotType.UPGRADE)).isEqualTo(4);
    assertThat(slots.getSlots(SlotType.ABILITY)).isEqualTo(2);

    // finally, defaults
    assertThat(slots.getSlots(SlotType.DEFENSE)).isEqualTo(0);
  }

  @Test
  void bufferReadWrite() {
    DefinitionModifierSlots slots = DefinitionModifierSlots
      .builder()
      .setSlots(SlotType.UPGRADE, 4)
      .setSlots(SlotType.DEFENSE, 1)
      .build();

    FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
    slots.write(buffer);
    DefinitionModifierSlots decoded = DefinitionModifierSlots.read(buffer);

    // validate stats parsed
    assertThat(decoded.containedTypes()).hasSize(2);
    assertThat(decoded.containedTypes()).contains(SlotType.UPGRADE);
    assertThat(decoded.containedTypes()).contains(SlotType.DEFENSE);
    assertThat(decoded.getSlots(SlotType.UPGRADE)).isEqualTo(4);
    assertThat(decoded.getSlots(SlotType.DEFENSE)).isEqualTo(1);
  }

  @Test
  void jsonSerializeTest() {
    DefinitionModifierSlots slots = DefinitionModifierSlots
      .builder()
      .setSlots(SlotType.UPGRADE, 4)
      .setSlots(SlotType.DEFENSE, 1)
      .build();

    JsonElement serialized = DefinitionModifierSlots.SERIALIZER.serialize(slots, DefinitionModifierSlots.class, mock(JsonSerializationContext.class));
    assertThat(serialized.isJsonObject()).isTrue();
    JsonObject object = serialized.getAsJsonObject();
    assertThat(GsonHelper.getAsInt(object, SlotType.UPGRADE.getName())).isEqualTo(4);
    assertThat(GsonHelper.getAsInt(object, SlotType.DEFENSE.getName())).isEqualTo(1);
  }

  @Test
  void jsonDeserializeTest() {
    JsonObject object = new JsonObject();
    object.addProperty(SlotType.UPGRADE.getName(), 5);
    object.addProperty(SlotType.DEFENSE.getName(), 7);

    DefinitionModifierSlots slots = DefinitionModifierSlots.SERIALIZER.deserialize(object, DefinitionModifierSlots.class, mock(JsonDeserializationContext.class));
    assertThat(slots.containedTypes()).hasSize(2);
    assertThat(slots.containedTypes()).contains(SlotType.UPGRADE);
    assertThat(slots.containedTypes()).contains(SlotType.DEFENSE);
    assertThat(slots.getSlots(SlotType.UPGRADE)).isEqualTo(5);
    assertThat(slots.getSlots(SlotType.DEFENSE)).isEqualTo(7);
  }
}
