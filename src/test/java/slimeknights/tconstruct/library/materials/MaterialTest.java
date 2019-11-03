package slimeknights.tconstruct.library.materials;

import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.PartType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

class MaterialTest {

  private static final PartType TEST_PART_TYPE = new PartType.PartTypeImpl("testPartType");
  private static final PartType TEST_PART_TYPE2 = new PartType.PartTypeImpl("testPartType2");

  private Material material;// = new Material("test");

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
      public PartType getIdentifier() {
        return new PartType.PartTypeImpl(id);
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
