package slimeknights.tconstruct.tools;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import slimeknights.tconstruct.fixture.MaterialStatsFixture;
import slimeknights.tconstruct.fixture.ToolDefinitionFixture;
import slimeknights.tconstruct.library.MaterialRegistryExtension;
import slimeknights.tconstruct.library.MaterialRegistryImpl;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.test.BaseMcTest;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static slimeknights.tconstruct.fixture.MaterialFixture.*;

@ExtendWith(MaterialRegistryExtension.class)
class ToolStatsBuilderTest extends BaseMcTest {

  @BeforeEach
  void setUp(MaterialRegistryImpl materialRegistry) {
    setupStatsInRegistry(materialRegistry, MATERIAL_WITH_HEAD, MaterialStatsFixture.MATERIAL_STATS_HEAD, HeadMaterialStats.DEFAULT);
    setupStatsInRegistry(materialRegistry, MATERIAL_WITH_HANDLE, MaterialStatsFixture.MATERIAL_STATS_HANDLE, HandleMaterialStats.DEFAULT);
    setupStatsInRegistry(materialRegistry, MATERIAL_WITH_EXTRA, MaterialStatsFixture.MATERIAL_STATS_EXTRA, ExtraMaterialStats.DEFAULT);
  }

  private <T extends IMaterialStats> void setupStatsInRegistry(MaterialRegistryImpl materialRegistry, Material material, T stats, T defaultStats) {
    MaterialStatsId id = defaultStats.getIdentifier();
    when(materialRegistry.getMaterialStats(eq(material.getIdentifier()), eq(id))).thenReturn(Optional.of(stats));
    when(materialRegistry.getMaterialStats(eq(material.getIdentifier()), argThat(argument -> !argument.equals(id)))).thenReturn(Optional.empty());
    when(materialRegistry.getDefaultStats(eq(id))).thenReturn(defaultStats);
  }

  @Test
  void name(MaterialRegistryImpl materialRegistry) {
    when(materialRegistry.getMaterialStats(any(), any())).thenReturn(Optional.of(MaterialStatsFixture.MATERIAL_STATS_2));

    // todo: setup materialfixture with 1 material with head only, handle only, extra only, and a material with all stats, then test against the singles and all with all

    ToolStatsBuilder builder = ToolStatsBuilder.from(ImmutableList.of(MATERIAL_1, MATERIAL_2), ToolDefinitionFixture.getTestToolDefinition());

    assertThat(builder.getHeads()).isEmpty();
    assertThat(builder.getHandles()).isEmpty();
    assertThat(builder.getExtras()).isEmpty();
  }

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
