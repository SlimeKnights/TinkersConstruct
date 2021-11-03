package slimeknights.tconstruct.library.tools.definition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.fixture.MaterialItemFixture;
import slimeknights.tconstruct.test.BaseMcTest;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PartRequirementTest extends BaseMcTest {

  @BeforeAll
  static void registerParts() {
    MaterialItemFixture.init();
  }

  @Test
  void bufferReadWrite() {
    PartRequirement requirement = new PartRequirement(MaterialItemFixture.MATERIAL_ITEM, 5);
    PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
    requirement.write(buffer);

    PartRequirement decoded = PartRequirement.read(buffer);
    assertThat(decoded.getPart()).isEqualTo(MaterialItemFixture.MATERIAL_ITEM);
    assertThat(decoded.getWeight()).isEqualTo(5);
  }

  @Test
  void serializeJson() {
    PartRequirement requirement = new PartRequirement(MaterialItemFixture.MATERIAL_ITEM_2, 5);
    JsonElement json = PartRequirement.SERIALIZER.serialize(requirement, PartRequirement.class, null);
    assertThat(json.isJsonObject()).isTrue();
    JsonObject object = json.getAsJsonObject();
    assertThat(JSONUtils.getString(object, "item")).isEqualTo(Objects.requireNonNull(MaterialItemFixture.MATERIAL_ITEM_2.getRegistryName()).toString());
    assertThat(JSONUtils.getInt(object, "weight")).isEqualTo(5);

    // weight is optional if 1
    requirement = new PartRequirement(MaterialItemFixture.MATERIAL_ITEM, 1);
    json = PartRequirement.SERIALIZER.serialize(requirement, PartRequirement.class, mock(JsonSerializationContext.class));
    assertThat(json.isJsonObject()).isTrue();
    object = json.getAsJsonObject();
    assertThat(JSONUtils.getString(object, "item")).isEqualTo(Objects.requireNonNull(MaterialItemFixture.MATERIAL_ITEM.getRegistryName()).toString());
    assertThat(object.has("weight")).isFalse();
  }

  @Test
  void deserializeJson() {
    JsonObject json = new JsonObject();
    json.addProperty("item", Objects.requireNonNull(MaterialItemFixture.MATERIAL_ITEM_HEAD.getRegistryName()).toString());
    json.addProperty("weight", 4);
    PartRequirement requirement = PartRequirement.SERIALIZER.deserialize(json, PartRequirement.class, mock(JsonDeserializationContext.class));
    assertThat(requirement.getPart()).isEqualTo(MaterialItemFixture.MATERIAL_ITEM_HEAD);
    assertThat(requirement.getWeight()).isEqualTo(4);

    // no weight defaults to 1
    json = new JsonObject();
    json.addProperty("item", Objects.requireNonNull(MaterialItemFixture.MATERIAL_ITEM_HANDLE.getRegistryName()).toString());
    requirement = PartRequirement.SERIALIZER.deserialize(json, PartRequirement.class, null);
    assertThat(requirement.getPart()).isEqualTo(MaterialItemFixture.MATERIAL_ITEM_HANDLE);
    assertThat(requirement.getWeight()).isEqualTo(1);
  }
}
