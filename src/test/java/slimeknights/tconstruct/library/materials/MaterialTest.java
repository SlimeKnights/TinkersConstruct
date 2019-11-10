package slimeknights.tconstruct.library.materials;

import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

class MaterialTest {

  private static final MaterialStatType TEST_PART_TYPE = new MaterialStatType(new ResourceLocation("testPartType"));
  private static final MaterialStatType TEST_PART_TYPE2 = new MaterialStatType(new ResourceLocation("testPartType2"));

  private Material material = new Material(new ResourceLocation("test", "material"), Fluids.WATER, true, new ItemStack(Items.STICK));

  @BeforeAll
  static void setUpRegistries() {
    Bootstrap.register();
  }

  @Test
  void ensureStatsAreAlwaysInOrder() {
    fail();
  }

  @Test
  void ensureTraitsAreAlwaysInOrder() {
    fail();
  }

  @Test
  void getTraitsForSpecificPartType() {
    IMaterialStats test = generateStats("testStats");
    // add trait to stats, add different trait to default traits, ensure correct one is returned
    material.getAllTraitsForStats(TEST_PART_TYPE);
    fail();
  }

  @Test
  void getDefaultTraits() {
    IMaterialStats test = generateStats("testStats");
    // add trait to stats, add different trait to default traits, ensure default correct one is returned for other part type
    material.getAllTraitsForStats(TEST_PART_TYPE2);
    fail();
  }

  private IMaterialStats generateStats(final String id) {
    return new IMaterialStats() {
      @Override
      public ResourceLocation getIdentifier() {
        return new ResourceLocation(id);
      }

      @Override
      public String getLocalizedName() {
        return null;
      }

      @Override
      public List<String> getLocalizedInfo() {
        return null;
      }

      @Override
      public List<String> getLocalizedDesc() {
        return null;
      }
    };
  }
}
