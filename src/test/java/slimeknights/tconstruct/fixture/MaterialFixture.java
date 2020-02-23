package slimeknights.tconstruct.fixture;

import com.google.common.collect.ImmutableList;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.TestMaterial;

import java.util.List;

public final class MaterialFixture {

  // declaration order is important here, the builder needs to be the first thing declared for this to work!
  private static final ImmutableList.Builder<Material> ALL_MATERIALS_BUILDER = ImmutableList.builder();
  public static final Material MATERIAL_1 = material("mat1", Fluids.WATER, true);
  public static final Material MATERIAL_2 = material("mat2", Fluids.LAVA, false);

  public static final Material MATERIAL_WITH_HEAD = material("mat_head", Fluids.EMPTY, false);
  public static final Material MATERIAL_WITH_HANDLE = material("mat_handle", Fluids.EMPTY, false);
  public static final Material MATERIAL_WITH_EXTRA = material("mat_extra", Fluids.EMPTY, false);
  public static final Material MATERIAL_WITH_ALL_STATS = material("mat_all_stats", Fluids.EMPTY, false);

  public static final List<Material> ALL_MATERIALS = ALL_MATERIALS_BUILDER.build();

  private static Material material(String mat1, Fluid water, boolean b) {
    Material material = new TestMaterial(new ResourceLocation("test", mat1), water, b, ItemStack.EMPTY);
    ALL_MATERIALS_BUILDER.add(material);
    return material;
  }

  private MaterialFixture() {
  }
}
