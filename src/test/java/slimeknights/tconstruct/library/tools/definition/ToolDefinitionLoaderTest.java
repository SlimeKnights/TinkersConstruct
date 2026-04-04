package slimeknights.tconstruct.library.tools.definition;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.fixture.MaterialItemFixture;
import slimeknights.tconstruct.fixture.RegistrationFixture;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierFixture;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.ToolModule;
import slimeknights.tconstruct.library.tools.definition.module.aoe.AreaOfEffectIterator;
import slimeknights.tconstruct.library.tools.definition.module.aoe.CircleAOEIterator;
import slimeknights.tconstruct.library.tools.definition.module.build.MultiplyStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.SetStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolActionToolHook;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolActionsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolSlotsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.ToolTraitsModule;
import slimeknights.tconstruct.library.tools.definition.module.build.VolatileDataToolHook;
import slimeknights.tconstruct.library.tools.definition.module.material.PartStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolPartsHook;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveModule;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveToolHook;
import slimeknights.tconstruct.library.tools.definition.module.weapon.MeleeHitToolHook;
import slimeknights.tconstruct.library.tools.definition.module.weapon.SweepWeaponAttack;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.test.BaseMcTest;
import slimeknights.tconstruct.test.JsonFileLoader;
import slimeknights.tconstruct.test.TestHelper;
import slimeknights.tconstruct.test.TestHelper.ToolDefinitionStats;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ToolDefinitionLoaderTest extends BaseMcTest {
  private static final ToolDefinitionData WRONG_DATA = ToolDefinitionDataBuilder.builder().module(new SetStatsModule(StatsNBT.builder().set(ToolStats.DURABILITY, 100).build())).build();
  private static final JsonFileLoader fileLoader = new JsonFileLoader(JsonHelper.DEFAULT_GSON, ToolDefinitionLoader.FOLDER);
  private static final ToolDefinition NO_PARTS_MINIMAL = ToolDefinition.create(TConstruct.getResource("minimal_no_parts"));
  private static final ToolDefinition NO_PARTS_FULL = ToolDefinition.create(TConstruct.getResource("full_no_parts"));
  private static final ToolDefinition MELEE_HARVEST_MINIMAL = ToolDefinition.create(TConstruct.getResource("minimal_with_parts"));
  private static final ToolDefinition MELEE_HARVEST_FULL = ToolDefinition.create(TConstruct.getResource("full_with_parts"));
  private static final ToolDefinition NEED_PARTS_HAS_NONE = ToolDefinition.create(TConstruct.getResource("need_parts_has_none"));
  private static final ToolDefinition WRONG_PART_TYPE = ToolDefinition.create(TConstruct.getResource("wrong_part_type"));

  @BeforeAll
  static void beforeAll() {
    MaterialItemFixture.init();
    RegistrationFixture.register(ToolModule.LOADER, "base_stats", SetStatsModule.LOADER);
    RegistrationFixture.register(ToolModule.LOADER, "multiply_stats", MultiplyStatsModule.LOADER);
    RegistrationFixture.register(ToolModule.LOADER, "part_stats", PartStatsModule.LOADER);
    RegistrationFixture.register(ToolModule.LOADER, "modifier_slots", ToolSlotsModule.LOADER);
    RegistrationFixture.register(ToolModule.LOADER, "traits", ToolTraitsModule.LOADER);
    RegistrationFixture.register(ToolModule.LOADER, "actions", ToolActionsModule.LOADER);
    RegistrationFixture.register(ToolModule.LOADER, "is_effective", IsEffectiveModule.LOADER);
    RegistrationFixture.register(ToolModule.LOADER, "circle", CircleAOEIterator.LOADER);
    RegistrationFixture.register(ToolModule.LOADER, "sweep", SweepWeaponAttack.LOADER);
  }

  /** Helper to do all the stats checks */
  private static void checkFullNonParts(ToolDefinitionData data) {
    // base stats
    ToolDefinitionStats stats = TestHelper.buildStats(data);
    assertThat(stats.base().getContainedStats()).hasSize(4);
    assertThat(stats.base().getContainedStats()).contains(ToolStats.DURABILITY);
    assertThat(stats.base().getContainedStats()).contains(ToolStats.ATTACK_DAMAGE);
    assertThat(stats.base().getContainedStats()).contains(ToolStats.ATTACK_SPEED);
    assertThat(stats.base().getContainedStats()).contains(ToolStats.MINING_SPEED);
    assertThat(stats.base().get(ToolStats.DURABILITY)).isEqualTo(100f);
    assertThat(stats.base().get(ToolStats.ATTACK_DAMAGE)).isEqualTo(2.5f);
    assertThat(stats.base().get(ToolStats.ATTACK_SPEED)).isEqualTo(3.75f);
    assertThat(stats.base().get(ToolStats.MINING_SPEED)).isEqualTo(4f);
    // multiplier stats
    assertThat(stats.multipliers().getContainedStats()).hasSize(3);
    assertThat(stats.multipliers().getContainedStats()).contains(ToolStats.DURABILITY);
    assertThat(stats.multipliers().getContainedStats()).contains(ToolStats.ATTACK_DAMAGE);
    assertThat(stats.multipliers().getContainedStats()).contains(ToolStats.MINING_SPEED);
    assertThat(stats.multipliers().get(ToolStats.DURABILITY)).isEqualTo(1.5f);
    assertThat(stats.multipliers().get(ToolStats.ATTACK_DAMAGE)).isEqualTo(2f);
    assertThat(stats.multipliers().get(ToolStats.MINING_SPEED)).isEqualTo(0.5f);
    // slots
    VolatileDataToolHook volatileHook = data.getHook(ToolHooks.VOLATILE_DATA);
    assertThat(volatileHook).isInstanceOf(ToolSlotsModule.class);
    Map<SlotType,Integer> slots = ((ToolSlotsModule) volatileHook).slots();
    assertThat(slots).hasSize(3);
    assertThat(slots).containsEntry(SlotType.UPGRADE, 3);
    assertThat(slots).containsEntry(SlotType.DEFENSE, 2);
    assertThat(slots).containsEntry(SlotType.ABILITY, 1);
    // traits
    List<ModifierEntry> traits = TestHelper.getTraits(data);
    assertThat(traits).hasSize(2);
    assertThat(traits.get(0).getId()).isEqualTo(ModifierFixture.TEST_1);
    assertThat(traits.get(0).getLevel()).isEqualTo(1);
    assertThat(traits.get(1).getId()).isEqualTo(ModifierFixture.TEST_2);
    assertThat(traits.get(1).getLevel()).isEqualTo(3);
    // actions
    ToolActionToolHook actions = data.getHook(ToolHooks.TOOL_ACTION);
    assertThat(actions).isInstanceOf(ToolActionsModule.class);
    assertThat(((ToolActionsModule) actions).actions()).hasSize(2);
    IToolStackView tool = mock(IToolStackView.class);
    assertThat(data.getHook(ToolHooks.TOOL_ACTION).canPerformAction(tool, ToolActions.AXE_DIG)).isTrue();
    assertThat(data.getHook(ToolHooks.TOOL_ACTION).canPerformAction(tool, ToolAction.get("custom_action"))).isTrue();
    // harvest
    IsEffectiveToolHook harvestLogic = data.getHook(ToolHooks.IS_EFFECTIVE);
    assertThat(harvestLogic).isInstanceOf(IsEffectiveModule.class);
    assertThat(harvestLogic.isToolEffective(tool, Blocks.DIAMOND_BLOCK.defaultBlockState())).isTrue();
    // aoe
    AreaOfEffectIterator aoe = data.getHook(ToolHooks.AOE_ITERATOR);
    assertThat(aoe).isInstanceOf(CircleAOEIterator.class);
    assertThat(((CircleAOEIterator)aoe).diameter()).isEqualTo(3);
    assertThat(((CircleAOEIterator)aoe).is3D()).isTrue();
    // weapon
    MeleeHitToolHook attack = data.getHook(ToolHooks.MELEE_HIT);
    assertThat(attack).isInstanceOf(SweepWeaponAttack.class);
    LevelingValue range = ((SweepWeaponAttack)attack).range();
    assertThat(range.flat()).isEqualTo(5);
    assertThat(range.eachLevel()).isEqualTo(1);
  }

  @BeforeAll
  static void setup() {
    SlotType.init();
    MaterialItemFixture.init();
    ModifierFixture.init();
  }

  @Test
  void noParts_minimal() {
    Map<ResourceLocation,JsonElement> splashList = fileLoader.loadFilesAsSplashlist(NO_PARTS_MINIMAL.getId().getPath());
    ToolDefinitionLoader.getInstance().apply(splashList, mock(ResourceManager.class), mock(ProfilerFiller.class));

    ToolDefinitionData data = NO_PARTS_MINIMAL.getData();
    assertThat(data).isNotNull();
    // will not be the empty instance, but will be filled with empty data
    assertThat(data).isNotSameAs(ToolDefinitionData.EMPTY);
    ToolDefinitionDataTest.checkToolDataEmpty(data);
  }

  @Test
  void noParts_full() {
    Map<ResourceLocation,JsonElement> splashList = fileLoader.loadFilesAsSplashlist(NO_PARTS_FULL.getId().getPath());
    ToolDefinitionLoader.getInstance().apply(splashList, mock(ResourceManager.class), mock(ProfilerFiller.class));

    ToolDefinitionData data = NO_PARTS_FULL.getData();
    assertThat(data).isNotNull();
    assertThat(data).isNotSameAs(ToolDefinitionData.EMPTY);
    assertThat(data.getHook(ToolHooks.TOOL_PARTS).getParts(ToolDefinition.EMPTY)).isEmpty();
    checkFullNonParts(data);
  }

  @Test
  void missingStats_defaults() {
    NO_PARTS_FULL.setData(WRONG_DATA); // set to wrong data to ensure something changes
    // next line is intentionally loading a different file, to make it missing
    Map<ResourceLocation,JsonElement> splashList = fileLoader.loadFilesAsSplashlist(MELEE_HARVEST_MINIMAL.getId().getPath());
    ToolDefinitionLoader.getInstance().apply(splashList, mock(ResourceManager.class), mock(ProfilerFiller.class));
    assertThat(NO_PARTS_FULL.getData()).isSameAs(ToolDefinitionData.EMPTY);
  }

  @Test
  void meleeHarvest_minimal() {
    Map<ResourceLocation,JsonElement> splashList = fileLoader.loadFilesAsSplashlist(MELEE_HARVEST_MINIMAL.getId().getPath());
    ToolDefinitionLoader.getInstance().apply(splashList, mock(ResourceManager.class), mock(ProfilerFiller.class));

    ToolDefinitionData data = MELEE_HARVEST_MINIMAL.getData();
    assertThat(data).isNotNull();
    // will not be the empty instance, but will be filled with empty data
    assertThat(data).isNotSameAs(ToolDefinitionData.EMPTY);
    ToolPartsHook toolPartsHook = data.getHook(ToolHooks.TOOL_PARTS);
    assertThat(toolPartsHook).isInstanceOf(PartStatsModule.class);
    PartStatsModule module = (PartStatsModule) toolPartsHook;
    List<IToolPart> parts = module.getParts(ToolDefinition.EMPTY);
    float[] scales = module.getScales();
    assertThat(parts).isNotNull();
    assertThat(parts).hasSize(1);
    assertThat(scales).isNotNull();
    assertThat(scales).hasSize(1);
    assertThat(parts.get(0)).isEqualTo(MaterialItemFixture.MATERIAL_ITEM_HEAD);
    assertThat(scales[0]).isEqualTo(1);
    ToolDefinitionDataTest.checkToolDataNonPartsEmpty(data);
  }

  @Test
  void meleeHarvest_full() {
    Map<ResourceLocation,JsonElement> splashList = fileLoader.loadFilesAsSplashlist(MELEE_HARVEST_FULL.getId().getPath());
    ToolDefinitionLoader.getInstance().apply(splashList, mock(ResourceManager.class), mock(ProfilerFiller.class));

    ToolDefinitionData data = MELEE_HARVEST_FULL.getData();
    assertThat(data).isNotNull();
    assertThat(data).isNotSameAs(ToolDefinitionData.EMPTY);
    ToolPartsHook toolPartsHook = data.getHook(ToolHooks.TOOL_PARTS);
    assertThat(toolPartsHook).isInstanceOf(PartStatsModule.class);
    PartStatsModule module = (PartStatsModule) toolPartsHook;
    List<IToolPart> parts = module.getParts(ToolDefinition.EMPTY);
    float[] scales = module.getScales();
    assertThat(parts).hasSize(3);
    assertThat(scales).hasSize(3);
    assertThat(parts.get(0)).isEqualTo(MaterialItemFixture.MATERIAL_ITEM_EXTRA);
    assertThat(scales[0]).isEqualTo(2);
    assertThat(parts.get(1)).isEqualTo(MaterialItemFixture.MATERIAL_ITEM_HEAD);
    assertThat(scales[1]).isEqualTo(1);
    assertThat(parts.get(2)).isEqualTo(MaterialItemFixture.MATERIAL_ITEM_HANDLE);
    assertThat(scales[2]).isEqualTo(3);
    checkFullNonParts(data);
  }

  @Test
  void meleeHarvest_missingParts_defaults() {
    NEED_PARTS_HAS_NONE.setData(WRONG_DATA); // set to wrong data to ensure something changes
    Map<ResourceLocation,JsonElement> splashList = fileLoader.loadFilesAsSplashlist(NEED_PARTS_HAS_NONE.getId().getPath());
    ToolDefinitionLoader.getInstance().apply(splashList, mock(ResourceManager.class), mock(ProfilerFiller.class));
    assertThat(NEED_PARTS_HAS_NONE.getData()).isSameAs(ToolDefinitionData.EMPTY);
  }
}
