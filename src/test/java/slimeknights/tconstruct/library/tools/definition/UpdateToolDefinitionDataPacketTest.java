package slimeknights.tconstruct.library.tools.definition;

import com.google.common.collect.ImmutableMap;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ToolActions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.fixture.MaterialItemFixture;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierFixture;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.aoe.CircleAOEIterator;
import slimeknights.tconstruct.library.tools.definition.aoe.IAreaOfEffectIterator;
import slimeknights.tconstruct.library.tools.definition.harvest.IHarvestLogic;
import slimeknights.tconstruct.library.tools.definition.weapon.IWeaponAttack;
import slimeknights.tconstruct.library.tools.definition.weapon.SweepWeaponAttack;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.MultiplierNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.test.BaseMcTest;
import slimeknights.tconstruct.test.BlockHarvestLogic;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class UpdateToolDefinitionDataPacketTest extends BaseMcTest {
  private static final ResourceLocation EMPTY_ID = new ResourceLocation("test", "empty");
  private static final ResourceLocation FILLED_ID = new ResourceLocation("test", "filled");

  @BeforeAll
  static void initialize() {
    MaterialItemFixture.init();
    ModifierFixture.init();
    try {
      IHarvestLogic.LOADER.register(new ResourceLocation("test", "block"), BlockHarvestLogic.LOADER);
      IAreaOfEffectIterator.LOADER.register(new ResourceLocation("test", "circle"), CircleAOEIterator.LOADER);
      IWeaponAttack.LOADER.register(new ResourceLocation("test", "sweep"), SweepWeaponAttack.LOADER);
    } catch (IllegalArgumentException e) {
      // no-op
    }
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
      .trait(ModifierFixture.TEST_1, 10)
      .action(ToolActions.AXE_DIG)
      .action(ToolActions.SHOVEL_FLATTEN)
      // behavior
      .harvestLogic(new BlockHarvestLogic(Blocks.GRANITE))
      .aoe(new CircleAOEIterator(7, true))
      .attack(new SweepWeaponAttack(4))
      .build();

    // send a packet over the buffer
    FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
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
    assertThat(parsed.getStats().getBase().getContainedStats()).isEmpty();
    assertThat(parsed.getStats().getMultipliers().getContainedStats()).isEmpty();
    // no slots
    assertThat(parsed.getSlots().containedTypes()).isEmpty();
    // no traits
    assertThat(parsed.getTraits()).isEmpty();
    // no actions
    assertThat(parsed.actions).isNullOrEmpty();

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
    StatsNBT stats = parsed.getStats().getBase();
    assertThat(stats.getContainedStats()).hasSize(2);
    assertThat(stats.getContainedStats()).contains(ToolStats.DURABILITY);
    assertThat(stats.getContainedStats()).contains(ToolStats.ATTACK_DAMAGE);
    assertThat(stats.get(ToolStats.DURABILITY)).isEqualTo(1000);
    assertThat(stats.get(ToolStats.ATTACK_DAMAGE)).isEqualTo(152.5f);
    assertThat(stats.get(ToolStats.ATTACK_SPEED)).isEqualTo(ToolStats.ATTACK_SPEED.getDefaultValue());

    MultiplierNBT multipliers = parsed.getStats().getMultipliers();
    assertThat(multipliers.getContainedStats()).hasSize(2); // attack damage is 1, so its skipped
    assertThat(multipliers.getContainedStats()).contains(ToolStats.ATTACK_SPEED);
    assertThat(multipliers.getContainedStats()).contains(ToolStats.MINING_SPEED);
    assertThat(multipliers.get(ToolStats.MINING_SPEED)).isEqualTo(10);
    assertThat(multipliers.get(ToolStats.ATTACK_SPEED)).isEqualTo(0.5f);
    assertThat(multipliers.get(ToolStats.ATTACK_DAMAGE)).isEqualTo(1);
    assertThat(multipliers.get(ToolStats.DURABILITY)).isEqualTo(1);

    // slots
    DefinitionModifierSlots slots = parsed.getSlots();
    assertThat(slots.containedTypes()).hasSize(2);
    assertThat(slots.containedTypes()).contains(SlotType.UPGRADE);
    assertThat(slots.containedTypes()).contains(SlotType.ABILITY);
    assertThat(slots.getSlots(SlotType.UPGRADE)).isEqualTo(5);
    assertThat(slots.getSlots(SlotType.ABILITY)).isEqualTo(8);

    // traits
    List<ModifierEntry> traits = parsed.getTraits();
    assertThat(traits).hasSize(1);
    assertThat(traits.get(0).getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_1);
    assertThat(traits.get(0).getLevel()).isEqualTo(10);

    // actions
    assertThat(parsed.actions).isNotNull();
    assertThat(parsed.actions).hasSize(2);
    assertThat(parsed.canPerformAction(ToolActions.AXE_DIG)).isTrue();
    assertThat(parsed.canPerformAction(ToolActions.SHOVEL_FLATTEN)).isTrue();

    // harvest
    IHarvestLogic harvestLogic = parsed.getHarvestLogic();
    assertThat(harvestLogic).isInstanceOf(BlockHarvestLogic.class);
    assertThat(harvestLogic.isEffective(mock(IToolStackView.class), Blocks.GRANITE.defaultBlockState())).isTrue();

    // aoe
    IAreaOfEffectIterator aoe = parsed.getAOE();
    assertThat(aoe).isInstanceOf(CircleAOEIterator.class);
    assertThat(((CircleAOEIterator)aoe).getDiameter()).isEqualTo(7);
    assertThat(((CircleAOEIterator)aoe).is3D()).isTrue();

    // weapon
    IWeaponAttack attack = parsed.getAttack();
    assertThat(attack).isInstanceOf(SweepWeaponAttack.class);
    assertThat(((SweepWeaponAttack)attack).getRange()).isEqualTo(4);
  }
}
