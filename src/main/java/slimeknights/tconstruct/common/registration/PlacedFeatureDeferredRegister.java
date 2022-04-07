package slimeknights.tconstruct.common.registration;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.registration.deferred.DeferredRegisterWrapper;

import java.util.Arrays;
import java.util.List;

public class PlacedFeatureDeferredRegister extends DeferredRegisterWrapper<PlacedFeature> {
  public PlacedFeatureDeferredRegister(String modID) {
    super(Registry.PLACED_FEATURE_REGISTRY, modID);
  }

  /**
   * Registers a placed feature
   * @param name       Feature name
   * @param feature    Configured feature base
   * @param placement  Placements
   * @return  Registry object
   */
  public RegistryObject<PlacedFeature> register(String name, RegistryObject<? extends ConfiguredFeature<?,?>> feature, List<PlacementModifier> placement) {
    return register.register(name, () -> new PlacedFeature(Holder.hackyErase(feature.getHolder().orElseThrow(() -> new IllegalStateException("Feature does not have a holder"))), List.copyOf(placement)));
  }

  /**
   * Registers a placed feature
   * @param name       Feature name
   * @param feature    Configured feature base
   * @param placement  Placements
   * @return  Registry object
   */
  public RegistryObject<PlacedFeature> register(String name, RegistryObject<? extends ConfiguredFeature<?,?>> feature, PlacementModifier... placement) {
    return register(name, feature, Arrays.asList(placement));
  }

  /** Registers a geode feature */
  public RegistryObject<PlacedFeature> registerGeode(String name, RegistryObject<ConfiguredFeature<GeodeConfiguration,Feature<GeodeConfiguration>>> geode, RarityFilter rarity, HeightRangePlacement height) {
    return register(name, geode, rarity, InSquarePlacement.spread(), height, BiomeFilter.biome());
  }
}
