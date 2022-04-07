package slimeknights.tconstruct.common.registration;

import net.minecraft.core.Registry;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.registration.deferred.DeferredRegisterWrapper;
import slimeknights.tconstruct.common.registration.GeodeItemObject.BudSize;
import slimeknights.tconstruct.world.worldgen.trees.SupplierBlockStateProvider;

import java.util.Arrays;
import java.util.function.Supplier;

public class ConfiguredFeatureDeferredRegister extends DeferredRegisterWrapper<ConfiguredFeature<?,?>> {
  public ConfiguredFeatureDeferredRegister(String modID) {
    super(Registry.CONFIGURED_FEATURE_REGISTRY, modID);
  }

  /**
   * Registers a configured feature with the register, for when the configuration needs to be built in a supplier
   * @param name     Feature name
   * @param feature  Parent feature
   * @param config   Configuration
   * @param <FC>     Config type
   * @param <F>      Feature type
   * @return  Registered instance
   */
  public <FC extends FeatureConfiguration, F extends Feature<FC>> RegistryObject<ConfiguredFeature<FC,F>> registerSupplier(String name, Supplier<F> feature, Supplier<FC> config) {
    return register.register(name, () -> new ConfiguredFeature<>(feature.get(), config.get()));
  }

  /**
   * Registers a configured feature with the register, for when the configuration can be safely statically built
   * @param name     Feature name
   * @param feature  Parent feature
   * @param config   Configuration
   * @param <FC>     Config type
   * @param <F>      Feature type
   * @return  Registered instance
   */
  public <FC extends FeatureConfiguration, F extends Feature<FC>> RegistryObject<ConfiguredFeature<FC,F>> registerStatic(String name, Supplier<F> feature, FC config) {
    return register.register(name, () -> new ConfiguredFeature<>(feature.get(), config));
  }

  /** Registers a configured geode */
  public RegistryObject<ConfiguredFeature<GeodeConfiguration,Feature<GeodeConfiguration>>> registerGeode(String name, GeodeItemObject geode,
                                                                                          BlockStateProvider middleLayer, BlockStateProvider outerLayer, GeodeLayerSettings layerSettings, GeodeCrackSettings crackSettings,
                                                                                          IntProvider outerWall, IntProvider distributionPoints, IntProvider pointOffset, int genOffset, int invalidBlocks) {
    return this.registerSupplier(name, () -> Feature.GEODE, () -> new GeodeConfiguration(
      new GeodeBlockSettings(BlockStateProvider.simple(Blocks.AIR),
                             BlockStateProvider.simple(geode.getBlock()),
                             SupplierBlockStateProvider.ofBlock(geode::getBudding),
                             middleLayer, outerLayer,
                             Arrays.stream(BudSize.values()).map(type -> geode.getBud(type).defaultBlockState()).toList(),
                             BlockTags.FEATURES_CANNOT_REPLACE, BlockTags.GEODE_INVALID_BLOCKS),
      layerSettings, crackSettings, 0.335, 0.083, true, outerWall, distributionPoints, pointOffset, -genOffset, genOffset, 0.05D, invalidBlocks));
  }
}
