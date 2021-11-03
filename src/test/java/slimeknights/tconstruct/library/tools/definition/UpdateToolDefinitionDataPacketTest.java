package slimeknights.tconstruct.library.tools.definition;

import com.google.common.collect.ImmutableMap;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.fixture.MaterialItemFixture;
import slimeknights.tconstruct.fixture.ModifierFixture;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.test.BaseMcTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateToolDefinitionDataPacketTest extends BaseMcTest {
  private static final ResourceLocation EMPTY_ID = new ResourceLocation("test", "empty");
  private static final ResourceLocation FILLED_ID = new ResourceLocation("test", "filled");

  @BeforeAll
  static void initialize() {
    MaterialItemFixture.init();
    ModifierFixture.init();
  }

  @Test
  void testGenericEncodeDecode() {
    ToolDefinitionData empty = ToolDefinitionData.EMPTY;
    ToolDefinitionData filled = ToolDefinitionDataBuilder
      .builder()
      // parts
      .part(MaterialItemFixture.MATERIAL_ITEM_HEAD, 10)
      .part(MaterialItemFixture.MATERIAL_ITEM_HANDLE)
      // stats
      .stat(ToolStats.DURABILITY, 1000)
      .stat(ToolStats.ATTACK_DAMAGE, 152.5f)
      .multiplier(ToolStats.MINING_SPEED, 10)
      .multiplier(ToolStats.ATTACK_SPEED, 0.5f)
      .multiplier(ToolStats.ATTACK_DAMAGE, 1)
      .startingSlots(SlotType.UPGRADE, 5)
      .startingSlots(SlotType.ABILITY, 8)
      // traits
      .trait(ModifierFixture.TEST_MODIFIER_1, 10)
      .build();

    // send a packet over the buffer
    PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
    UpdateToolDefinitionDataPacket packetToEncode = new UpdateToolDefinitionDataPacket(ImmutableMap.of(EMPTY_ID, empty, FILLED_ID, filled));
    packetToEncode.encode(buffer);
    UpdateToolDefinitionDataPacket decoded = new UpdateToolDefinitionDataPacket(buffer);

    // parse results
    Map<ResourceLocation,ToolDefinitionData> parsedMap = decoded.getDataMap();
    assertThat(parsedMap).hasSize(2);

    // first validate empty
    ToolDefinitionData parsed = parsedMap.get(EMPTY_ID);
    assertThat(parsed).isNotNull();
    // no parts
    assertThat(parsed.getParts()).isEmpty();
    // no stats
    assertThat(parsed.getStats().getBase().containedStats()).isEmpty();
    assertThat(parsed.getStats().getMultipliers().containedStats()).isEmpty();
    // no slots
    assertThat(parsed.getSlots().containedTypes()).isEmpty();
    // no traits
    assertThat(parsed.getTraits()).isEmpty();

    // next, validate the filled one
    parsed = parsedMap.get(FILLED_ID);
    assertThat(parsed).isNotNull();

    // parts
    List<PartRequirement> parts = parsed.getParts();
    assertThat(parts).hasSize(2);
    assertThat(parts.get(0).getPart()).isEqualTo(MaterialItemFixture.MATERIAL_ITEM_HEAD);
    assertThat(parts.get(0).getWeight()).isEqualTo(10);
    assertThat(parts.get(1).getPart()).isEqualTo(MaterialItemFixture.MATERIAL_ITEM_HANDLE);
    assertThat(parts.get(1).getWeight()).isEqualTo(1);

    // stats
    DefinitionToolStats stats = parsed.getStats().getBase();
    assertThat(stats.containedStats()).hasSize(2);
    assertThat(stats.containedStats()).contains(ToolStats.DURABILITY);
    assertThat(stats.containedStats()).contains(ToolStats.ATTACK_DAMAGE);
    assertThat(stats.getStat(ToolStats.DURABILITY, -1)).isEqualTo(1000);
    assertThat(stats.getStat(ToolStats.ATTACK_DAMAGE, -1)).isEqualTo(152.5f);
    assertThat(stats.getStat(ToolStats.ATTACK_SPEED, -1)).isEqualTo(-1);

    stats = parsed.getStats().getMultipliers();
    assertThat(stats.containedStats()).hasSize(3);
    assertThat(stats.containedStats()).contains(ToolStats.ATTACK_DAMAGE);
    assertThat(stats.containedStats()).contains(ToolStats.ATTACK_SPEED);
    assertThat(stats.containedStats()).contains(ToolStats.MINING_SPEED);
    assertThat(stats.getStat(ToolStats.MINING_SPEED, -1)).isEqualTo(10);
    assertThat(stats.getStat(ToolStats.ATTACK_SPEED, -1)).isEqualTo(0.5f);
    assertThat(stats.getStat(ToolStats.ATTACK_DAMAGE, -1)).isEqualTo(1);
    assertThat(stats.getStat(ToolStats.DURABILITY, -1)).isEqualTo(-1);

    // slots
    DefinitionModifierSlots slots = parsed.getSlots();
    assertThat(slots.containedTypes()).hasSize(2);
    assertThat(slots.containedTypes()).contains(SlotType.UPGRADE);
    assertThat(slots.containedTypes()).contains(SlotType.ABILITY);
    assertThat(slots.getSlots(SlotType.UPGRADE)).isEqualTo(5);
    assertThat(slots.getSlots(SlotType.ABILITY)).isEqualTo(8);

    // no traits
    List<ModifierEntry> traits = parsed.getTraits();
    assertThat(traits).hasSize(1);
    assertThat(traits.get(0).getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_1);
    assertThat(traits.get(0).getLevel()).isEqualTo(10);
  }
}
