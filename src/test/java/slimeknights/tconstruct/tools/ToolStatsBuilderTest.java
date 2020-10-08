package slimeknights.tconstruct.tools;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import slimeknights.tconstruct.fixture.MaterialStatsFixture;
import slimeknights.tconstruct.fixture.ToolDefinitionFixture;
import slimeknights.tconstruct.library.MaterialRegistryExtension;
import slimeknights.tconstruct.library.materials.IMaterial;
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
class ToolStatsBuilderTest extends BaseMcTest {

  @Test
  void init_onlyHead() {
    ImmutableList<IMaterial> materials = ImmutableList.of(MATERIAL_WITH_HEAD, MATERIAL_WITH_HEAD, MATERIAL_WITH_HEAD);

    ToolStatsBuilder builder = ToolStatsBuilder.from(materials, ToolDefinitionFixture.getStandardToolDefinition());

    assertThat(builder.getHeads()).containsExactly(MaterialStatsFixture.MATERIAL_STATS_HEAD);
    assertThat(builder.getHandles()).containsExactly(HandleMaterialStats.DEFAULT);
    assertThat(builder.getExtras()).containsExactly(ExtraMaterialStats.DEFAULT);
  }

  @Test
  void init_onlyHandle() {
    ImmutableList<IMaterial> materials = ImmutableList.of(MATERIAL_WITH_HANDLE, MATERIAL_WITH_HANDLE, MATERIAL_WITH_HANDLE);

    ToolStatsBuilder builder = ToolStatsBuilder.from(materials, ToolDefinitionFixture.getStandardToolDefinition());

    assertThat(builder.getHeads()).containsExactly(HeadMaterialStats.DEFAULT);
    assertThat(builder.getHandles()).containsExactly(MaterialStatsFixture.MATERIAL_STATS_HANDLE);
    assertThat(builder.getExtras()).containsExactly(ExtraMaterialStats.DEFAULT);
  }

  @Test
  void init_onlyExtra() {
    ImmutableList<IMaterial> materials = ImmutableList.of(MATERIAL_WITH_EXTRA, MATERIAL_WITH_EXTRA, MATERIAL_WITH_EXTRA);

    ToolStatsBuilder builder = ToolStatsBuilder.from(materials, ToolDefinitionFixture.getStandardToolDefinition());

    assertThat(builder.getHeads()).containsExactly(HeadMaterialStats.DEFAULT);
    assertThat(builder.getHandles()).containsExactly(HandleMaterialStats.DEFAULT);
    assertThat(builder.getExtras()).containsExactly(MaterialStatsFixture.MATERIAL_STATS_EXTRA);
  }

  @Test
  void init_allCorrectStats() {
    ImmutableList<IMaterial> materials = ImmutableList.of(MATERIAL_WITH_HEAD, MATERIAL_WITH_HANDLE, MATERIAL_WITH_EXTRA);

    ToolStatsBuilder builder = ToolStatsBuilder.from(materials, ToolDefinitionFixture.getStandardToolDefinition());

    assertThat(builder.getHeads()).containsExactly(MaterialStatsFixture.MATERIAL_STATS_HEAD);
    assertThat(builder.getHandles()).containsExactly(MaterialStatsFixture.MATERIAL_STATS_HANDLE);
    assertThat(builder.getExtras()).containsExactly(MaterialStatsFixture.MATERIAL_STATS_EXTRA);
  }

  @Test
  void init_wrongOrder() {
    ImmutableList<IMaterial> materials = ImmutableList.of(MATERIAL_WITH_HANDLE, MATERIAL_WITH_EXTRA, MATERIAL_WITH_HEAD);

    ToolStatsBuilder builder = ToolStatsBuilder.from(materials, ToolDefinitionFixture.getStandardToolDefinition());

    assertThat(builder.getHeads()).containsExactly(HeadMaterialStats.DEFAULT);
    assertThat(builder.getHandles()).containsExactly(HandleMaterialStats.DEFAULT);
    assertThat(builder.getExtras()).containsExactly(ExtraMaterialStats.DEFAULT);
  }

  @Test
  void init_singleMaterialAllStats() {
    ImmutableList<IMaterial> materials = ImmutableList.of(MATERIAL_WITH_ALL_STATS, MATERIAL_WITH_ALL_STATS, MATERIAL_WITH_ALL_STATS);

    ToolStatsBuilder builder = ToolStatsBuilder.from(materials, ToolDefinitionFixture.getStandardToolDefinition());

    assertThat(builder.getHeads()).containsExactly(MaterialStatsFixture.MATERIAL_STATS_HEAD);
    assertThat(builder.getHandles()).containsExactly(MaterialStatsFixture.MATERIAL_STATS_HANDLE);
    assertThat(builder.getExtras()).containsExactly(MaterialStatsFixture.MATERIAL_STATS_EXTRA);
  }

  @Test
  void calculateValues_noStats() {
    ToolStatsBuilder builder = new ToolStatsBuilder(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

    assertThat(builder.buildDurability()).isEqualTo(1);
    assertThat(builder.buildHarvestLevel()).isEqualTo(0);
    assertThat(builder.buildMiningSpeed()).isGreaterThan(0);
    assertThat(builder.buildMiningSpeed()).isLessThanOrEqualTo(1);
    assertThat(builder.buildAttack()).isGreaterThan(0);
    assertThat(builder.buildAttack()).isLessThanOrEqualTo(1);
  }

  @Test
  void buildDurability_ensureAverage_head() {
    HeadMaterialStats stats1 = new HeadMaterialStats(100, 0, 0, 0);
    HeadMaterialStats stats2 = new HeadMaterialStats(50, 0, 0, 0);

    ToolStatsBuilder builder = new ToolStatsBuilder(ImmutableList.of(stats1, stats2), Collections.emptyList(), Collections.emptyList());

    assertThat(builder.buildDurability()).isEqualTo(75);
  }

  @Test
  void buildDurability_ensureAverage_extra() {
    ExtraMaterialStats stats1 = new ExtraMaterialStats(100);
    ExtraMaterialStats stats2 = new ExtraMaterialStats(50);

    ToolStatsBuilder builder = new ToolStatsBuilder(Collections.emptyList(), Collections.emptyList(), ImmutableList.of(stats1, stats2));

    assertThat(builder.buildDurability()).isEqualTo(75);
  }

  @Test
  void buildDurability_ensureAverage_handle() {
    HandleMaterialStats stats1 = new HandleMaterialStats(100, 1f, 1f, 1f);
    HandleMaterialStats stats2 = new HandleMaterialStats(50, 1f, 1f, 1f);

    ToolStatsBuilder builder = new ToolStatsBuilder(Collections.emptyList(), ImmutableList.of(stats1, stats2), Collections.emptyList());

    assertThat(builder.buildDurability()).isEqualTo(75);
  }

  @Test
  void buildDurability_testHandleModifier() {
    HeadMaterialStats statsHead = new HeadMaterialStats(200, 0, 0, 0);
    HandleMaterialStats statsHandle = new HandleMaterialStats(0, 0.5f, 0.5f, 0.5f);
    ExtraMaterialStats statsExtra = new ExtraMaterialStats(100);

    ToolStatsBuilder builder = new ToolStatsBuilder(ImmutableList.of(statsHead), ImmutableList.of(statsHandle), ImmutableList.of(statsExtra));

    assertThat(builder.buildDurability()).isEqualTo(150);
  }

  @Test
  void buildDurability_testHandleModifier_average() {
    HeadMaterialStats statsHead = new HeadMaterialStats(200, 0, 0, 0);
    HandleMaterialStats statsHandle1 = new HandleMaterialStats(0, 0.3f, 0.7f, 0.7f);
    HandleMaterialStats statsHandle2 = new HandleMaterialStats(0, 0.7f, 0.7f, 0.7f);
    ExtraMaterialStats statsExtra = new ExtraMaterialStats(100);

    ToolStatsBuilder builder = new ToolStatsBuilder(ImmutableList.of(statsHead), ImmutableList.of(statsHandle1, statsHandle2), ImmutableList.of(statsExtra));

    assertThat(builder.buildDurability()).isEqualTo(150);
  }

  @Test
  void buildDurability_testHandleDurability_notAffectedByModifier() {
    HeadMaterialStats statsHead = new HeadMaterialStats(100, 0, 0, 0);
    HandleMaterialStats statsHandle = new HandleMaterialStats(50, 0.1f, 0.1f, 0.1f);
    ExtraMaterialStats statsExtra = new ExtraMaterialStats(0);

    ToolStatsBuilder builder = new ToolStatsBuilder(ImmutableList.of(statsHead), ImmutableList.of(statsHandle), ImmutableList.of(statsExtra));

    assertThat(builder.buildDurability()).isEqualTo(60);
  }

  @Test
  void buildMiningSpeed_ensureAverage() {
    HeadMaterialStats stats1 = new HeadMaterialStats(1, 10, 0, 0);
    HeadMaterialStats stats2 = new HeadMaterialStats(1, 5, 0, 0);

    ToolStatsBuilder builder = new ToolStatsBuilder(ImmutableList.of(stats1, stats2), Collections.emptyList(), Collections.emptyList());

    assertThat(builder.buildMiningSpeed()).isEqualTo(7.5f);
  }

  @Test
  void buildAttack_ensureAverage() {
    HeadMaterialStats stats1 = new HeadMaterialStats(1, 0, 0, 5);
    HeadMaterialStats stats2 = new HeadMaterialStats(1, 0, 0, 10);

    ToolStatsBuilder builder = new ToolStatsBuilder(ImmutableList.of(stats1, stats2), Collections.emptyList(), Collections.emptyList());

    assertThat(builder.buildAttack()).isEqualTo(7.5f);
  }

  @Test
  void buildHarvestLevel_ensureMax() {
    HeadMaterialStats stats1 = new HeadMaterialStats(1, 1, 2, 0);
    HeadMaterialStats stats2 = new HeadMaterialStats(1, 1, 1, 0);
    HeadMaterialStats stats3 = new HeadMaterialStats(1, 1, 5, 0);
    HeadMaterialStats stats4 = new HeadMaterialStats(1, 1, -1, 0);

    ToolStatsBuilder builder = new ToolStatsBuilder(ImmutableList.of(stats1, stats2, stats3, stats4), Collections.emptyList(), Collections.emptyList());

    assertThat(builder.buildHarvestLevel()).isEqualTo(5);
  }
}
