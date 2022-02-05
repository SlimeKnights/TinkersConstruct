package slimeknights.tconstruct.library.tools.definition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.fixture.MaterialItemFixture;
import slimeknights.tconstruct.test.BaseMcTest;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PartRequirementTest extends BaseMcTest {

  @BeforeAll
  static void registerParts() {
    MaterialItemFixture.init();
  }

  @Test
  void bufferReadWrite_part() {
    PartRequirement requirement = PartRequirement.ofPart(MaterialItemFixture.MATERIAL_ITEM, 5);
    FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
    requirement.write(buffer);

    PartRequirement decoded = PartRequirement.read(buffer);
    assertThat(decoded.getPart()).isEqualTo(MaterialItemFixture.MATERIAL_ITEM);
    assertThat(decoded.getWeight()).isEqualTo(5);
  }

  @Test
  void serializeJson_part() {
    PartRequirement requirement = PartRequirement.ofPart(MaterialItemFixture.MATERIAL_ITEM_2, 5);
    JsonElement json = PartRequirement.SERIALIZER.serialize(requirement, PartRequirement.class, mock(JsonSerializationContext.class));
    assertThat(json.isJsonObject()).isTrue();
    JsonObject object = json.getAsJsonObject();
    assertThat(GsonHelper.getAsString(object, "item")).isEqualTo(Objects.requireNonNull(MaterialItemFixture.MATERIAL_ITEM_2.getRegistryName()).toString());
    assertThat(object.has("stat")).isFalse();
    assertThat(GsonHelper.getAsInt(object, "weight")).isEqualTo(5);

    // weight is optional if 1
    requirement = PartRequirement.ofPart(MaterialItemFixture.MATERIAL_ITEM, 1);
    json = PartRequirement.SERIALIZER.serialize(requirement, PartRequirement.class, mock(JsonSerializationContext.class));
    assertThat(json.isJsonObject()).isTrue();
    object = json.getAsJsonObject();
    assertThat(GsonHelper.getAsString(object, "item")).isEqualTo(Objects.requireNonNull(MaterialItemFixture.MATERIAL_ITEM.getRegistryName()).toString());
    assertThat(object.has("stat")).isFalse();
    assertThat(object.has("weight")).isFalse();
  }

  @Test
  void deserializeJson_part() {
    JsonObject json = new JsonObject();
    json.addProperty("item", Objects.requireNonNull(MaterialItemFixture.MATERIAL_ITEM_HEAD.getRegistryName()).toString());
    json.addProperty("weight", 4);
    PartRequirement requirement = PartRequirement.SERIALIZER.deserialize(json, PartRequirement.class, mock(JsonDeserializationContext.class));
    assertThat(requirement.getPart()).isEqualTo(MaterialItemFixture.MATERIAL_ITEM_HEAD);
    assertThat(requirement.getWeight()).isEqualTo(4);

    // no weight defaults to 1
    json = new JsonObject();
    json.addProperty("item", Objects.requireNonNull(MaterialItemFixture.MATERIAL_ITEM_HANDLE.getRegistryName()).toString());
    requirement = PartRequirement.SERIALIZER.deserialize(json, PartRequirement.class, mock(JsonDeserializationContext.class));
    assertThat(requirement.getPart()).isEqualTo(MaterialItemFixture.MATERIAL_ITEM_HANDLE);
    assertThat(requirement.getWeight()).isEqualTo(1);
  }

  @Test
  void bufferReadWrite_stat() {
    PartRequirement requirement = PartRequirement.ofStat(HeadMaterialStats.ID, 5);
    FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
    requirement.write(buffer);

    PartRequirement decoded = PartRequirement.read(buffer);
    assertThat(decoded.getPart()).isNull();
    assertThat(decoded.getStatType()).isEqualTo(HeadMaterialStats.ID);
    assertThat(decoded.getWeight()).isEqualTo(5);
  }

  @Test
  void serializeJson_stat() {
    PartRequirement requirement = PartRequirement.ofStat(HandleMaterialStats.ID, 5);
    JsonElement json = PartRequirement.SERIALIZER.serialize(requirement, PartRequirement.class, mock(JsonSerializationContext.class));
    assertThat(json.isJsonObject()).isTrue();
    JsonObject object = json.getAsJsonObject();
    assertThat(GsonHelper.getAsString(object, "stat")).isEqualTo(HandleMaterialStats.ID.toString());
    assertThat(object.has("item")).isFalse();
    assertThat(GsonHelper.getAsInt(object, "weight")).isEqualTo(5);

    // weight is optional if 1
    requirement = PartRequirement.ofStat(HeadMaterialStats.ID, 1);
    json = PartRequirement.SERIALIZER.serialize(requirement, PartRequirement.class, mock(JsonSerializationContext.class));
    assertThat(json.isJsonObject()).isTrue();
    object = json.getAsJsonObject();
    assertThat(GsonHelper.getAsString(object, "stat")).isEqualTo(HeadMaterialStats.ID.toString());
    assertThat(object.has("item")).isFalse();
    assertThat(object.has("weight")).isFalse();
  }

  @Test
  void deserializeJson_stat() {
    JsonObject json = new JsonObject();
    json.addProperty("stat", HeadMaterialStats.ID.toString());
    json.addProperty("weight", 4);
    PartRequirement requirement = PartRequirement.SERIALIZER.deserialize(json, PartRequirement.class, mock(JsonDeserializationContext.class));
    assertThat(requirement.getPart()).isNull();
    assertThat(requirement.getStatType()).isEqualTo(HeadMaterialStats.ID);
    assertThat(requirement.getWeight()).isEqualTo(4);

    // no weight defaults to 1
    json = new JsonObject();
    json.addProperty("stat", HandleMaterialStats.ID.toString());
    requirement = PartRequirement.SERIALIZER.deserialize(json, PartRequirement.class, mock(JsonDeserializationContext.class));
    assertThat(requirement.getPart()).isNull();
    assertThat(requirement.getStatType()).isEqualTo(HandleMaterialStats.ID);
    assertThat(requirement.getWeight()).isEqualTo(1);
  }
}
