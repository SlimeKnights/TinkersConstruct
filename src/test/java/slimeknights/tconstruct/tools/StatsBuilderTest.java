package slimeknights.tconstruct.tools;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.item.Tiers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import slimeknights.tconstruct.fixture.MaterialStatsFixture;
import slimeknights.tconstruct.fixture.ToolDefinitionFixture;
import slimeknights.tconstruct.library.materials.MaterialRegistryExtension;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionData;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionDataBuilder;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.tools.stat.ToolStatsBuilder;
import slimeknights.tconstruct.test.BaseMcTest;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static slimeknights.tconstruct.fixture.MaterialFixture.MATERIAL_WITH_ALL_STATS;
import static slimeknights.tconstruct.fixture.MaterialFixture.MATERIAL_WITH_EXTRA;
import static slimeknights.tconstruct.fixture.MaterialFixture.MATERIAL_WITH_HANDLE;
import static slimeknights.tconstruct.fixture.MaterialFixture.MATERIAL_WITH_HEAD;

@ExtendWith(MaterialRegistryExtension.class)
class StatsBuilderTest extends BaseMcTest {

  /**
   * Gets a builder for the given materials list, validating the size is correct
   * @param materials  List of materials
   * @return  Melee harvest tool stats builder
   */
  static MeleeHarvestToolStatsBuilder getBuilder(IMaterial... materials) {
    ToolStatsBuilder builder = MeleeHarvestToolStatsBuilder.from(ToolDefinitionFixture.getStandardToolDefinition(), MaterialNBT.of(materials));
    assertThat(builder).overridingErrorMessage("Given materials list is the wrong size").isInstanceOf(MeleeHarvestToolStatsBuilder.class);
    return (MeleeHarvestToolStatsBuilder) builder;
  }

  @Test
  void init_onlyHead() {
    MeleeHarvestToolStatsBuilder builder = getBuilder(MATERIAL_WITH_HEAD, MATERIAL_WITH_HEAD, MATERIAL_WITH_HEAD);

    assertThat(builder.getHeads()).containsExactly(MaterialStatsFixture.MATERIAL_STATS_HEAD);
    assertThat(builder.getHandles()).containsExactly(HandleMaterialStats.DEFAULT);
    assertThat(builder.getExtras()).containsExactly(ExtraMaterialStats.DEFAULT);
  }

  @Test
  void init_onlyHandle() {
    MeleeHarvestToolStatsBuilder builder = getBuilder(MATERIAL_WITH_HANDLE, MATERIAL_WITH_HANDLE, MATERIAL_WITH_HANDLE);

    assertThat(builder.getHeads()).containsExactly(HeadMaterialStats.DEFAULT);
    assertThat(builder.getHandles()).containsExactly(MaterialStatsFixture.MATERIAL_STATS_HANDLE);
    assertThat(builder.getExtras()).containsExactly(ExtraMaterialStats.DEFAULT);
  }

  @Test
  void init_onlyExtra() {
    MeleeHarvestToolStatsBuilder builder = getBuilder(MATERIAL_WITH_EXTRA, MATERIAL_WITH_EXTRA, MATERIAL_WITH_EXTRA);

    assertThat(builder.getHeads()).containsExactly(HeadMaterialStats.DEFAULT);
    assertThat(builder.getHandles()).containsExactly(HandleMaterialStats.DEFAULT);
    assertThat(builder.getExtras()).containsExactly(MaterialStatsFixture.MATERIAL_STATS_EXTRA);
  }

  @Test
  void init_allCorrectStats() {
    MeleeHarvestToolStatsBuilder builder = getBuilder(MATERIAL_WITH_HEAD, MATERIAL_WITH_HANDLE, MATERIAL_WITH_EXTRA);

    assertThat(builder.getHeads()).containsExactly(MaterialStatsFixture.MATERIAL_STATS_HEAD);
    assertThat(builder.getHandles()).containsExactly(MaterialStatsFixture.MATERIAL_STATS_HANDLE);
    assertThat(builder.getExtras()).containsExactly(MaterialStatsFixture.MATERIAL_STATS_EXTRA);
  }

  @Test
  void init_wrongOrder() {
    MeleeHarvestToolStatsBuilder builder = getBuilder(MATERIAL_WITH_HANDLE, MATERIAL_WITH_EXTRA, MATERIAL_WITH_HEAD);

    assertThat(builder.getHeads()).containsExactly(HeadMaterialStats.DEFAULT);
    assertThat(builder.getHandles()).containsExactly(HandleMaterialStats.DEFAULT);
    assertThat(builder.getExtras()).containsExactly(ExtraMaterialStats.DEFAULT);
  }

  @Test
  void init_singleMaterialAllStats() {
    MeleeHarvestToolStatsBuilder builder = getBuilder(MATERIAL_WITH_ALL_STATS, MATERIAL_WITH_ALL_STATS, MATERIAL_WITH_ALL_STATS);

    assertThat(builder.getHeads()).containsExactly(MaterialStatsFixture.MATERIAL_STATS_HEAD);
    assertThat(builder.getHandles()).containsExactly(MaterialStatsFixture.MATERIAL_STATS_HANDLE);
    assertThat(builder.getExtras()).containsExactly(MaterialStatsFixture.MATERIAL_STATS_EXTRA);
  }

  @Test
  void calculateValues_noStats() {
    MeleeHarvestToolStatsBuilder builder = new MeleeHarvestToolStatsBuilder(ToolDefinitionData.EMPTY, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

    assertThat(builder.buildDurability()).isEqualTo(1);
    assertThat(builder.buildHarvestLevel()).isEqualTo(Tiers.WOOD);
    assertThat(builder.buildMiningSpeed()).isGreaterThan(0);
    assertThat(builder.buildMiningSpeed()).isLessThanOrEqualTo(1);
    assertThat(builder.buildAttackDamage()).isEqualTo(0);
    assertThat(builder.buildAttackSpeed()).isEqualTo(1);
  }

  @Test
  void buildDurability_ensureAverage_head() {
    HeadMaterialStats stats1 = new HeadMaterialStats(100, 0, Tiers.WOOD, 0);
    HeadMaterialStats stats2 = new HeadMaterialStats(50, 0, Tiers.WOOD, 0);

    MeleeHarvestToolStatsBuilder builder = new MeleeHarvestToolStatsBuilder(ToolDefinitionDataBuilder.builder().stat(ToolStats.DURABILITY, 100).build(), ImmutableList.of(stats1, stats2), Collections.emptyList(), Collections.emptyList());

    assertThat(builder.buildDurability()).isEqualTo(175);
  }

  @Test
  void buildDurability_testHandleDurability() {
    HeadMaterialStats statsHead = new HeadMaterialStats(200, 0, Tiers.WOOD, 0);
    HandleMaterialStats statsHandle = new HandleMaterialStats(0.5f, 0, 0, 0);

    MeleeHarvestToolStatsBuilder builder = new MeleeHarvestToolStatsBuilder(ToolDefinitionData.EMPTY, ImmutableList.of(statsHead), ImmutableList.of(statsHandle), Collections.emptyList());

    assertThat(builder.buildDurability()).isEqualTo(100);
  }

  @Test
  void buildMiningSpeed_testHandleMiningSpeed() {
    HeadMaterialStats statsHead = new HeadMaterialStats(0, 2.0f, Tiers.WOOD, 0);
    HandleMaterialStats statsHandle = new HandleMaterialStats(0, 0.5f, 0, 0);
    ExtraMaterialStats statsExtra = ExtraMaterialStats.DEFAULT;

    MeleeHarvestToolStatsBuilder builder = new MeleeHarvestToolStatsBuilder(ToolDefinitionData.EMPTY, ImmutableList.of(statsHead), ImmutableList.of(statsHandle), ImmutableList.of(statsExtra));

    assertThat(builder.buildMiningSpeed()).isEqualTo(1.0f);
  }

  @Test
  void buildDurability_testHandleDurability_average() {
    HeadMaterialStats statsHead = new HeadMaterialStats(200, 0, Tiers.WOOD, 0);
    HandleMaterialStats statsHandle1 = new HandleMaterialStats(0.3f, 0, 0, 0);
    HandleMaterialStats statsHandle2 = new HandleMaterialStats(0.7f, 0, 0, 0);

    MeleeHarvestToolStatsBuilder builder = new MeleeHarvestToolStatsBuilder(ToolDefinitionData.EMPTY, ImmutableList.of(statsHead), ImmutableList.of(statsHandle1, statsHandle2), Collections.emptyList());

    assertThat(builder.buildDurability()).isEqualTo(100);
  }

  @Test
  void buildMiningSpeed_testHandleMiningSpeed_average() {
    HeadMaterialStats statsHead = new HeadMaterialStats(0, 2.0f, Tiers.WOOD, 0);
    HandleMaterialStats statsHandle1 = new HandleMaterialStats(0, 0.3f, 0, 0);
    HandleMaterialStats statsHandle2 = new HandleMaterialStats(0, 0.7f, 0, 0);

    MeleeHarvestToolStatsBuilder builder = new MeleeHarvestToolStatsBuilder(ToolDefinitionData.EMPTY, ImmutableList.of(statsHead), ImmutableList.of(statsHandle1, statsHandle2), Collections.emptyList());

    assertThat(builder.buildMiningSpeed()).isEqualTo(1.0f);
  }

  @Test
  void buildMiningSpeed_ensureAverage() {
    HeadMaterialStats stats1 = new HeadMaterialStats(1, 10, Tiers.WOOD, 0);
    HeadMaterialStats stats2 = new HeadMaterialStats(1, 5, Tiers.WOOD, 0);

    MeleeHarvestToolStatsBuilder builder = new MeleeHarvestToolStatsBuilder(ToolDefinitionDataBuilder.builder().stat(ToolStats.MINING_SPEED, 10).build(), ImmutableList.of(stats1, stats2), Collections.emptyList(), Collections.emptyList());

    assertThat(builder.buildMiningSpeed()).isEqualTo(17.5f);
  }

  @Test
  void buildAttack_ensureAverage() {
    HeadMaterialStats stats1 = new HeadMaterialStats(1, 0, Tiers.WOOD, 5);
    HeadMaterialStats stats2 = new HeadMaterialStats(1, 0, Tiers.WOOD, 10);

    MeleeHarvestToolStatsBuilder builder = new MeleeHarvestToolStatsBuilder(ToolDefinitionDataBuilder.builder().stat(ToolStats.ATTACK_DAMAGE, 10).build(), ImmutableList.of(stats1, stats2), Collections.emptyList(), Collections.emptyList());

    assertThat(builder.buildAttackDamage()).isEqualTo(17.5f);
  }

  @Test
  void buildHarvestLevel_ensureMax() {
    HeadMaterialStats stats1 = new HeadMaterialStats(1, 1, Tiers.IRON, 0);
    HeadMaterialStats stats2 = new HeadMaterialStats(1, 1, Tiers.STONE, 0);
    HeadMaterialStats stats3 = new HeadMaterialStats(1, 1, Tiers.DIAMOND, 0);
    HeadMaterialStats stats4 = new HeadMaterialStats(1, 1, Tiers.WOOD, 0);

    MeleeHarvestToolStatsBuilder builder = new MeleeHarvestToolStatsBuilder(ToolDefinitionData.EMPTY, ImmutableList.of(stats1, stats2, stats3, stats4), Collections.emptyList(), Collections.emptyList());

    assertThat(builder.buildHarvestLevel()).isEqualTo(Tiers.DIAMOND);
  }

  @Test
  void buildAttackSpeed_set() {
    MeleeHarvestToolStatsBuilder builder = new MeleeHarvestToolStatsBuilder(ToolDefinitionDataBuilder.builder().stat(ToolStats.ATTACK_SPEED, 1.5f).build(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    assertThat(builder.buildAttackSpeed()).isEqualTo(1.5f);
  }

  @Test
  void buildAttackSpeed_testHandleAttackDamage() {
    HeadMaterialStats head = new HeadMaterialStats(0, 0, Tiers.WOOD, 2);
    HandleMaterialStats stats = new HandleMaterialStats(0, 0, 0, 0.5f);
    MeleeHarvestToolStatsBuilder builder = new MeleeHarvestToolStatsBuilder(ToolDefinitionData.EMPTY, ImmutableList.of(head), ImmutableList.of(stats), Collections.emptyList());

    assertThat(builder.buildAttackDamage()).isEqualTo(1.0f);
  }

  @Test
  void buildAttackSpeed_testHandleAttackDamage_average() {
    HeadMaterialStats head = new HeadMaterialStats(0, 0, Tiers.WOOD, 4);
    HandleMaterialStats stats1 = new HandleMaterialStats(0, 0, 0, 1.3f);
    HandleMaterialStats stats2 = new HandleMaterialStats(0, 0, 0, 1.7f);

    MeleeHarvestToolStatsBuilder builder = new MeleeHarvestToolStatsBuilder(ToolDefinitionDataBuilder.builder().stat(ToolStats.ATTACK_DAMAGE, 2).build(), ImmutableList.of(head), ImmutableList.of(stats1, stats2), Collections.emptyList());

    assertThat(builder.buildAttackDamage()).isEqualTo(9);
  }

  @Test
  void buildAttackSpeed_testHandleAttackSpeed() {
    HandleMaterialStats stats = new HandleMaterialStats(0, 0, 1.5f, 0);
    MeleeHarvestToolStatsBuilder builder = new MeleeHarvestToolStatsBuilder(ToolDefinitionData.EMPTY, Collections.emptyList(), ImmutableList.of(stats), Collections.emptyList());

    assertThat(builder.buildAttackSpeed()).isEqualTo(1.5f);
  }

  @Test
  void buildAttackSpeed_testHandleAttackSpeed_average() {
    HandleMaterialStats stats1 = new HandleMaterialStats(0, 0, 1.3f, 0);
    HandleMaterialStats stats2 = new HandleMaterialStats(0, 0, 1.7f, 0);

    MeleeHarvestToolStatsBuilder builder = new MeleeHarvestToolStatsBuilder(ToolDefinitionData.EMPTY, Collections.emptyList(), ImmutableList.of(stats1, stats2), Collections.emptyList());

    assertThat(builder.buildAttackSpeed()).isEqualTo(1.5f);
  }
}
