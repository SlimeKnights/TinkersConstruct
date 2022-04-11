package slimeknights.tconstruct.library.materials.traits;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.fixture.MaterialFixture;
import slimeknights.tconstruct.fixture.MaterialStatsFixture;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierFixture;
import slimeknights.tconstruct.test.BaseMcTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateMaterialTraitsPacketTest extends BaseMcTest {

  public static final MaterialId MATERIAL_ID_1 = MaterialFixture.MATERIAL_1.getIdentifier();
  public static final MaterialId MATERIAL_ID_2 = MaterialFixture.MATERIAL_2.getIdentifier();

  @BeforeAll
  static void beforeAll() {
    ModifierFixture.init();
  }

  @Test
  void testGenericEncodeDecode() {
    List<ModifierEntry> defaultTraits1 = Arrays.asList(new ModifierEntry(ModifierFixture.TEST_MODIFIER_1, 1), new ModifierEntry(ModifierFixture.TEST_MODIFIER_2, 2));
    MaterialTraits materialTraits1 = new MaterialTraits(defaultTraits1, Collections.emptyMap());

    List<ModifierEntry> defaultTraits2 = Collections.singletonList(new ModifierEntry(ModifierFixture.TEST_MODIFIER_1, 3));
    Map<MaterialStatsId, List<ModifierEntry>> statsTraits2 = new HashMap<>();
    statsTraits2.put(MaterialStatsFixture.STATS_TYPE, Arrays.asList(new ModifierEntry(ModifierFixture.TEST_MODIFIER_1, 4), new ModifierEntry(ModifierFixture.TEST_MODIFIER_2, 5)));
    statsTraits2.put(MaterialStatsFixture.STATS_TYPE_2, Collections.singletonList(new ModifierEntry(ModifierFixture.TEST_MODIFIER_2, 6)));
    MaterialTraits materialTraits2 = new MaterialTraits(defaultTraits2, statsTraits2);

    Map<MaterialId, MaterialTraits> map = new HashMap<>();
    map.put(MATERIAL_ID_1, materialTraits1);
    map.put(MATERIAL_ID_2, materialTraits2);

    // send a packet over the buffer
    FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
    UpdateMaterialTraitsPacket packetToEncode = new UpdateMaterialTraitsPacket(map);
    packetToEncode.encode(buffer);
    UpdateMaterialTraitsPacket decoded = new UpdateMaterialTraitsPacket(buffer);

    // parse results
    Map<MaterialId, MaterialTraits> parsed = decoded.getMaterialToTraits();
    assertThat(parsed).hasSize(2);

    // material traits 1
    MaterialTraits parsedTraits1 = parsed.get(MATERIAL_ID_1);
    assertThat(parsedTraits1).isNotNull();
    // default
    assertThat(parsedTraits1.getDefaultTraits()).hasSize(2);
    ModifierEntry trait1 = parsedTraits1.getDefaultTraits().get(0);
    assertThat(trait1.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_1);
    assertThat(trait1.getLevel()).isEqualTo(1);
    ModifierEntry trait2 = parsedTraits1.getDefaultTraits().get(1);
    assertThat(trait2.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_2);
    assertThat(trait2.getLevel()).isEqualTo(2);
    // per stat
    assertThat(parsedTraits1.getTraitsPerStats()).isEmpty();

    // material traits 2
    MaterialTraits parsedTraits2 = parsed.get(MATERIAL_ID_2);
    assertThat(parsedTraits2).isNotNull();
    // default
    assertThat(parsedTraits2.getDefaultTraits()).hasSize(1);
    trait1 = parsedTraits2.getDefaultTraits().get(0);
    assertThat(trait1.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_1);
    assertThat(trait1.getLevel()).isEqualTo(3);
    // per stat type
    assertThat(parsedTraits2.getTraitsPerStats()).hasSize(2);
    // stat type 1
    List<ModifierEntry> traitsForStats1 = parsedTraits2.getTraits(MaterialStatsFixture.STATS_TYPE);
    assertThat(traitsForStats1).isNotNull();
    assertThat(traitsForStats1).hasSize(2);
    trait1 = traitsForStats1.get(0);
    assertThat(trait1.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_1);
    assertThat(trait1.getLevel()).isEqualTo(4);
    trait2 = traitsForStats1.get(1);
    assertThat(trait2.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_2);
    assertThat(trait2.getLevel()).isEqualTo(5);
    // stat type 2
    List<ModifierEntry> traitsForStats2 = parsedTraits2.getTraits(MaterialStatsFixture.STATS_TYPE_2);
    assertThat(traitsForStats2).isNotNull();
    assertThat(traitsForStats2).hasSize(1);
    trait1 = traitsForStats2.get(0);
    assertThat(trait1.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_2);
    assertThat(trait1.getLevel()).isEqualTo(6);
  }
}
