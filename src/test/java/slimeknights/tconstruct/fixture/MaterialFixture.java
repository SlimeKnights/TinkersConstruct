package slimeknights.tconstruct.fixture;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.Material;
import slimeknights.tconstruct.library.materials.definition.TestMaterial;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;

import java.util.List;
import java.util.Map;

/**
 * All the materials in here are made available during tests using the {@link slimeknights.tconstruct.fixture.MaterialRegistryFixture}
 */
public final class MaterialFixture {

  // declaration order is important here, the builder needs to be the first thing declared for this to work!
  private static final ImmutableList.Builder<Material> ALL_MATERIALS_BUILDER = ImmutableList.builder();
  private static final ImmutableMap.Builder<IMaterial, List<IMaterialStats>> ALL_MATERIAL_FIXTURES_BUILDER = ImmutableMap.builder();
  public static final Material MATERIAL_1 = material("mat1", true, MaterialStatsFixture.MATERIAL_STATS);
  public static final Material MATERIAL_2 = material("mat2", false, MaterialStatsFixture.MATERIAL_STATS_2);

  public static final Material MATERIAL_WITH_HEAD = material("mat_head", MaterialStatsFixture.MATERIAL_STATS_HEAD);
  public static final Material MATERIAL_WITH_HANDLE = material("mat_handle", MaterialStatsFixture.MATERIAL_STATS_HANDLE);
  public static final Material MATERIAL_WITH_EXTRA = material("mat_extra", MaterialStatsFixture.MATERIAL_STATS_EXTRA);
  public static final Material MATERIAL_WITH_ALL_STATS = material("mat_all_stats", MaterialStatsFixture.MATERIAL_STATS_HEAD, MaterialStatsFixture.MATERIAL_STATS_HANDLE, MaterialStatsFixture.MATERIAL_STATS_EXTRA);

  public static final List<Material> ALL_MATERIALS = ALL_MATERIALS_BUILDER.build();
  public static final Map<IMaterial, List<IMaterialStats>> ALL_MATERIAL_FIXTURES = ALL_MATERIAL_FIXTURES_BUILDER.build();

  private static Material material(String mat, IMaterialStats... stats) {
    return material(mat, true, stats);
  }

  private static Material material(String mat, boolean craftable, IMaterialStats... stats) {
    Material material = new TestMaterial(new ResourceLocation("test", mat), craftable, false);
    ALL_MATERIALS_BUILDER.add(material);
    ALL_MATERIAL_FIXTURES_BUILDER.put(material, ImmutableList.copyOf(stats));
    return material;
  }

  private MaterialFixture() {
  }
}
