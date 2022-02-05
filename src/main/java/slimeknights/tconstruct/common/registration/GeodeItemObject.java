package slimeknights.tconstruct.common.registration;

import lombok.Getter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.registration.deferred.BlockDeferredRegister;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.world.block.BuddingCrystalBlock;
import slimeknights.tconstruct.world.block.CrystalBlock;
import slimeknights.tconstruct.world.block.CrystalClusterBlock;
import slimeknights.tconstruct.world.worldgen.trees.SupplierBlockStateProvider;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

/** Item object for geode related blocks. Main methods represent the block */
public class GeodeItemObject extends ItemObject<Item> {
  private final Supplier<? extends Block> block;
  private final Supplier<? extends Block> budding;
  private final Supplier<? extends Block> cluster;
  private final Supplier<? extends Block> smallBud;
  private final Supplier<? extends Block> mediumBud;
  private final Supplier<? extends Block> largeBud;
  private ConfiguredFeature<GeodeConfiguration,?> configuredGeode;
  private PlacedFeature placedGeode;
  public GeodeItemObject(RegistryObject<? extends Item> shard, BlockDeferredRegister register, MaterialColor color, SoundType blockSound, SoundEvent chimeSound, Map<BudSize,SoundType> clusterSounds, int baseLight, Properties props) {
    super(shard);
    // allow the crystals to glow optionally
    IntFunction<ToIntFunction<BlockState>> light = extra -> {
      int calculated = Math.min(extra + baseLight, 15);
      return state -> calculated;
    };
    String name = shard.getId().getPath();
    Function<Block, ? extends BlockItem> blockItem = block -> new BlockItem(block, props);
    ToIntFunction<BlockState> crystalLight = light.apply(0);
    block = register.register(name + "_block", () -> new CrystalBlock(chimeSound, BlockBehaviour.Properties.of(Material.AMETHYST, color).lightLevel(crystalLight).strength(1.5F).sound(blockSound).requiresCorrectToolForDrops()), blockItem);
    budding = register.register("budding_" + name, () -> new BuddingCrystalBlock(this, chimeSound, BlockBehaviour.Properties.of(Material.AMETHYST, color).randomTicks().lightLevel(crystalLight).strength(1.5F).sound(blockSound).requiresCorrectToolForDrops()), blockItem);
    // buds
    Supplier<BlockBehaviour.Properties> budProps = () -> BlockBehaviour.Properties.of(Material.AMETHYST, color).noOcclusion().strength(1.5F);
    cluster   = register.register(name + "_cluster", () -> new CrystalClusterBlock(chimeSound, 7, 3, budProps.get().lightLevel(light.apply(5)).sound(clusterSounds.get(BudSize.CLUSTER))), blockItem);
    smallBud  = register.register("small_" + name + "_bud",  () -> new CrystalClusterBlock(chimeSound, 3, 3, budProps.get().lightLevel(light.apply(1)).sound(clusterSounds.get(BudSize.SMALL))),  blockItem);
    mediumBud = register.register("medium_" + name + "_bud", () -> new CrystalClusterBlock(chimeSound, 4, 3, budProps.get().lightLevel(light.apply(2)).sound(clusterSounds.get(BudSize.MEDIUM))), blockItem);
    largeBud  = register.register("large_" + name + "_bud",  () -> new CrystalClusterBlock(chimeSound, 5, 3, budProps.get().lightLevel(light.apply(4)).sound(clusterSounds.get(BudSize.LARGE))),  blockItem);
  }

  /** Gets the block form of this */
  public Block getBlock() {
    return block.get();
  }

  /** Gets the budding form of the crystal */
  public Block getBudding() {
    return budding.get();
  }

  /** Gets a specific size of bud */
  public Block getBud(BudSize size) {
    return switch (size) {
      case SMALL -> smallBud.get();
      case MEDIUM -> mediumBud.get();
      case LARGE -> largeBud.get();
      case CLUSTER -> cluster.get();
    };
  }

  /** Creates the configured geode feature */
  public ConfiguredFeature<GeodeConfiguration, ?> configureGeode(BlockStateProvider middleLayer, BlockStateProvider outerLayer, GeodeLayerSettings layerSettings, GeodeCrackSettings crackSettings,
                                                                 IntProvider outerWall, IntProvider distributionPoints, IntProvider pointOffset, int genOffset, int invalidBlocks) {
    if (configuredGeode != null) {
      throw new IllegalStateException("Geode is already configured");
    }
    configuredGeode = Feature.GEODE.configured(
      new GeodeConfiguration(
        new GeodeBlockSettings(BlockStateProvider.simple(Blocks.AIR),
                               SupplierBlockStateProvider.ofBlock(block),
                               SupplierBlockStateProvider.ofBlock(budding),
                               middleLayer, outerLayer,
                               List.of(smallBud.get().defaultBlockState(), mediumBud.get().defaultBlockState(), largeBud.get().defaultBlockState(), cluster.get().defaultBlockState()),
                               BlockTags.FEATURES_CANNOT_REPLACE.getName(), BlockTags.GEODE_INVALID_BLOCKS.getName()),
        layerSettings, crackSettings, 0.335, 0.083, true, outerWall, distributionPoints, pointOffset, -genOffset, genOffset, 0.05D, invalidBlocks));
    return configuredGeode;
  }

  /** Gets the configured geode, must already be configured */
  public ConfiguredFeature<GeodeConfiguration, ?> getConfiguredGeode() {
    return Objects.requireNonNull(configuredGeode);
  }

  /** Creates the placed geode feature */
  public PlacedFeature placeGeode(RarityFilter rarity, HeightRangePlacement height) {
    if (placedGeode != null) {
      throw new IllegalStateException("Geode is already placed");
    }
    placedGeode = getConfiguredGeode().placed(rarity, InSquarePlacement.spread(), height, BiomeFilter.biome());
    return placedGeode;
  }

  /** Gets the configured geode, must already be placed and configured */
  public PlacedFeature getPlacedGeode() {
    return Objects.requireNonNull(placedGeode);
  }

  /** Variants for the bud */
  public enum BudSize {
    SMALL,
    MEDIUM,
    LARGE,
    CLUSTER;

    public static final BudSize[] SIZES = {SMALL, MEDIUM, LARGE};

    @Getter
    private final String name = name().toLowerCase(Locale.ROOT);
    @Getter
    private final int size = ordinal() + 1;

    /** Gets the next bud size */
    public BudSize getNext() {
      return switch (this) {
        case SMALL -> MEDIUM;
        case MEDIUM -> LARGE;
        case LARGE -> CLUSTER;
        default -> SMALL;
      };
    }
  }
}
