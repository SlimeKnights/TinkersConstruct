package slimeknights.tconstruct.library.materials.traits;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import slimeknights.mantle.data.listener.MergingJsonFileLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.fixture.MaterialStatsFixture;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierFixture;
import slimeknights.tconstruct.library.modifiers.util.OptionalModifier;
import slimeknights.tconstruct.test.BaseMcTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MaterialTraitsManagerTest extends BaseMcTest {
  // file containing just default traits
  private static final MaterialId DEFAULT_TRAITS = new MaterialId(TConstruct.getResource("default"));
  // file containing just traits per stat type
  private static final MaterialId PER_STAT_TRAITS = new MaterialId(TConstruct.getResource("per_stat"));
  // file containing both, and lists
  private static final MaterialId MULTIPLE_TRAITS = new MaterialId(TConstruct.getResource("multiple"));
  // file containing optionals
  private static final MaterialId OPTIONAL_TRAITS = new MaterialId(TConstruct.getResource("optional"));

  private final MaterialTraitsManager traitsManager = new MaterialTraitsManager();
  private final MergingJsonFileLoader<MaterialTraits.Builder> fileLoader = new MergingJsonFileLoader<>(traitsManager);

  @BeforeAll
  static void beforeAll() {
    ModifierFixture.init();
  }

  @Test
  void empty() {
    MaterialId empty = new MaterialId(TConstruct.getResource("empty"));
    fileLoader.loadAndParseFiles(null, empty);

    // ensure we got this far and there were no errors
    List<ModifierEntry> defaultTraits = traitsManager.getDefaultTraits(DEFAULT_TRAITS);
    assertThat(defaultTraits).isNotNull();
    assertThat(defaultTraits).isEmpty();
  }

  @Test
  void defaultTraitsOnlyAreLoaded() {
    fileLoader.loadAndParseFiles(null, DEFAULT_TRAITS);

    // ensure the default trait list is correct
    List<ModifierEntry> defaultTraits = traitsManager.getDefaultTraits(DEFAULT_TRAITS);
    assertThat(defaultTraits).hasSize(1);
    ModifierEntry trait = defaultTraits.get(0);
    assertThat(trait.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_1);
    assertThat(trait.getLevel()).isEqualTo(1);
    // ensure another stat type returns the default list
    List<ModifierEntry> statTypeTraits = traitsManager.getTraits(DEFAULT_TRAITS, MaterialStatsFixture.STATS_TYPE);
    assertThat(statTypeTraits).hasSize(1);
    trait = statTypeTraits.get(0);
    assertThat(trait.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_1);
    assertThat(trait.getLevel()).isEqualTo(1);

    // ensure that the map contains no stat type traits
    MaterialTraits internalTraits = traitsManager.materialTraits.get(DEFAULT_TRAITS);
    assertThat(internalTraits).isNotNull();
    assertThat(internalTraits.getTraitsPerStats()).isEmpty();
  }

  @Test
  void perStatsTraitsOnlyAreLoaded() {
    fileLoader.loadAndParseFiles(null, PER_STAT_TRAITS);

    // ensure the default trait list is correct
    List<ModifierEntry> defaultTraits = traitsManager.getDefaultTraits(PER_STAT_TRAITS);
    assertThat(defaultTraits).isEmpty();
    // ensure another stat type returns the default list
    List<ModifierEntry> statTypeTraits = traitsManager.getTraits(PER_STAT_TRAITS, MaterialStatsFixture.STATS_TYPE_2);
    assertThat(statTypeTraits).isEmpty();

    // ensure the set stat type has its own trait list
    statTypeTraits = traitsManager.getTraits(PER_STAT_TRAITS, MaterialStatsFixture.STATS_TYPE);
    assertThat(statTypeTraits).hasSize(1);
    ModifierEntry trait = statTypeTraits.get(0);
    assertThat(trait.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_2);
    assertThat(trait.getLevel()).isEqualTo(2);

    // check the internal object to ensure proper number of stats is loaded
    MaterialTraits internalTraits = traitsManager.materialTraits.get(PER_STAT_TRAITS);
    assertThat(internalTraits).isNotNull();
    assertThat(internalTraits.getTraitsPerStats()).hasSize(1);
  }

  @Test
  void multipleTraitsWithDefault() {
    fileLoader.loadAndParseFiles(null, MULTIPLE_TRAITS);

    // ensure the default trait list is correct
    List<ModifierEntry> defaultTraits = traitsManager.getDefaultTraits(MULTIPLE_TRAITS);
    assertThat(defaultTraits).hasSize(2);
    ModifierEntry trait = defaultTraits.get(0);
    assertThat(trait.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_1);
    assertThat(trait.getLevel()).isEqualTo(3);
    trait = defaultTraits.get(1);
    assertThat(trait.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_2);
    assertThat(trait.getLevel()).isEqualTo(4);

    // ensure an unused stat type returns the default list
    List<ModifierEntry> traitsForStatType = traitsManager.getTraits(MULTIPLE_TRAITS, MaterialStatsFixture.STATS_TYPE_4);
    assertThat(traitsForStatType).hasSize(2);
    trait = traitsForStatType.get(0);
    assertThat(trait.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_1);
    assertThat(trait.getLevel()).isEqualTo(3);
    trait = traitsForStatType.get(1);
    assertThat(trait.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_2);
    assertThat(trait.getLevel()).isEqualTo(4);

    // ensure setting to null resets to default stats
    traitsForStatType = traitsManager.getTraits(MULTIPLE_TRAITS, MaterialStatsFixture.STATS_TYPE_3);
    assertThat(traitsForStatType).hasSize(2);
    trait = traitsForStatType.get(0);
    assertThat(trait.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_1);
    assertThat(trait.getLevel()).isEqualTo(3);
    trait = traitsForStatType.get(1);
    assertThat(trait.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_2);
    assertThat(trait.getLevel()).isEqualTo(4);

    // ensure a stat type can override default traits
    traitsForStatType = traitsManager.getTraits(MULTIPLE_TRAITS, MaterialStatsFixture.STATS_TYPE);
    assertThat(traitsForStatType).hasSize(1);
    trait = traitsForStatType.get(0);
    assertThat(trait.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_1);
    assertThat(trait.getLevel()).isEqualTo(5);

    // ensure a stat type can override default traits to an empty list
    traitsForStatType = traitsManager.getTraits(MULTIPLE_TRAITS, MaterialStatsFixture.STATS_TYPE_2);
    assertThat(traitsForStatType).isEmpty();
  }

  @Test
  void optionalTraits() {
    fileLoader.loadAndParseFiles(null, OPTIONAL_TRAITS);

    // ensure the default trait list is correct
    List<ModifierEntry> defaultTraits = traitsManager.getDefaultTraits(OPTIONAL_TRAITS);
    assertThat(defaultTraits).hasSize(2);
    ModifierEntry trait = defaultTraits.get(0);
    assertThat(trait.getLazyModifier() instanceof OptionalModifier).isTrue();
    assertThat(trait.getId()).isEqualTo(ModifierFixture.TEST_MODIFIER_1.getId());
    assertThat(trait.getLevel()).isEqualTo(3);
    trait = defaultTraits.get(1);
    assertThat(trait.getLazyModifier() instanceof OptionalModifier).isFalse();
    assertThat(trait.getId()).isEqualTo(ModifierFixture.TEST_MODIFIER_2.getId());
    assertThat(trait.getLevel()).isEqualTo(4);

    // ensure an unused stat type returns the default list
    List<ModifierEntry> traitsForStatType = traitsManager.getTraits(OPTIONAL_TRAITS, MaterialStatsFixture.STATS_TYPE_4);
    assertThat(traitsForStatType).isEqualTo(defaultTraits);

    // ensure optionals work in a per stat as well
    // also ensures that setting to false works
    traitsForStatType = traitsManager.getTraits(OPTIONAL_TRAITS, MaterialStatsFixture.STATS_TYPE);
    assertThat(traitsForStatType).hasSize(2);
    trait = traitsForStatType.get(0);
    assertThat(trait.getLazyModifier() instanceof OptionalModifier).isFalse();
    assertThat(trait.getId()).isEqualTo(ModifierFixture.TEST_MODIFIER_1.getId());
    assertThat(trait.getLevel()).isEqualTo(1);
    trait = traitsForStatType.get(1);
    assertThat(trait.getLazyModifier() instanceof OptionalModifier).isTrue();
    assertThat(trait.getId()).isEqualTo(ModifierFixture.TEST_MODIFIER_2.getId());
    assertThat(trait.getLevel()).isEqualTo(2);
  }

  @Test
  void merging_addStatType() {
    fileLoader.loadAndParseFiles("merge_stat_type", MULTIPLE_TRAITS);

    // ensure the default trait list is unchanged
    List<ModifierEntry> defaultTraits = traitsManager.getDefaultTraits(MULTIPLE_TRAITS);
    assertThat(defaultTraits).hasSize(2);
    ModifierEntry trait = defaultTraits.get(0);
    assertThat(trait.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_1);
    assertThat(trait.getLevel()).isEqualTo(3);
    trait = defaultTraits.get(1);
    assertThat(trait.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_2);
    assertThat(trait.getLevel()).isEqualTo(4);

    // ensure setting to null resets to default stats
    List<ModifierEntry> traitsForStatType = traitsManager.getTraits(MULTIPLE_TRAITS, MaterialStatsFixture.STATS_TYPE_2);
    assertThat(traitsForStatType).hasSize(2);
    trait = traitsForStatType.get(0);
    assertThat(trait.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_1);
    assertThat(trait.getLevel()).isEqualTo(3);
    trait = traitsForStatType.get(1);
    assertThat(trait.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_2);
    assertThat(trait.getLevel()).isEqualTo(4);

    // ensure a the unchanged stat type is unchanged
    traitsForStatType = traitsManager.getTraits(MULTIPLE_TRAITS, MaterialStatsFixture.STATS_TYPE);
    assertThat(traitsForStatType).hasSize(1);
    trait = traitsForStatType.get(0);
    assertThat(trait.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_1);
    assertThat(trait.getLevel()).isEqualTo(5);

    // ensure a stat type can be overridden
    traitsForStatType = traitsManager.getTraits(MULTIPLE_TRAITS, MaterialStatsFixture.STATS_TYPE_3);
    assertThat(traitsForStatType).hasSize(1);
    trait = traitsForStatType.get(0);
    assertThat(trait.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_2);
    assertThat(trait.getLevel()).isEqualTo(6);
  }

  @Test
  void merging_replaceDefault() {
    fileLoader.loadAndParseFiles("merge_default", MULTIPLE_TRAITS);

    // ensure the default trait list is replaced
    List<ModifierEntry> defaultTraits = traitsManager.getDefaultTraits(MULTIPLE_TRAITS);
    assertThat(defaultTraits).hasSize(1);
    ModifierEntry trait = defaultTraits.get(0);
    assertThat(trait.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_2);
    assertThat(trait.getLevel()).isEqualTo(4);

    // ensure an unused stat type returns the new default list
    List<ModifierEntry> traitsForStatType = traitsManager.getTraits(MULTIPLE_TRAITS, MaterialStatsFixture.STATS_TYPE_4);
    assertThat(traitsForStatType).hasSize(1);
    trait = traitsForStatType.get(0);
    assertThat(trait.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_2);
    assertThat(trait.getLevel()).isEqualTo(4);

    // ensure other stat types still use the proper list
    traitsForStatType = traitsManager.getTraits(MULTIPLE_TRAITS, MaterialStatsFixture.STATS_TYPE);
    assertThat(traitsForStatType).hasSize(1);
    trait = traitsForStatType.get(0);
    assertThat(trait.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_1);
    assertThat(trait.getLevel()).isEqualTo(5);
    traitsForStatType = traitsManager.getTraits(MULTIPLE_TRAITS, MaterialStatsFixture.STATS_TYPE_2);
    assertThat(traitsForStatType).isEmpty();
  }

  @Test
  void missing_material() {
    // ensure that querying the missing material causes no errors
    List<ModifierEntry> traits = traitsManager.getDefaultTraits(DEFAULT_TRAITS);
    assertThat(traits).isNotNull();
    assertThat(traits).isEmpty();
    traits = traitsManager.getTraits(DEFAULT_TRAITS, MaterialStatsFixture.STATS_TYPE);
    assertThat(traits).isNotNull();
    assertThat(traits).isEmpty();
  }
}
