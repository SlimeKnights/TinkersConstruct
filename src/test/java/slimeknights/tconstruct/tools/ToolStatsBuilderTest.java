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
}
