package slimeknights.tconstruct.world.worldgen.islands.variants;

import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

/**
 * Island variant for sky slime islands
 */
public class SkySlimeIslandVariant extends AbstractSlimeIslandVariant {
  public SkySlimeIslandVariant(int index, SlimeType dirtType) {
    super(index, dirtType, SlimeType.SKY);
  }

  @Override
  public ResourceLocation getStructureName(String variantName) {
    return TConstruct.getResource("slime_islands/sky/" + dirtType.getString() + "_" + variantName);
  }

  @Override
  protected SlimeType getCongealedSlimeType(Random random) {
    return random.nextBoolean() ? SlimeType.SKY : SlimeType.EARTH;
  }

  @Override
  public BlockState getLakeFluid() {
    return Objects.requireNonNull(TinkerFluids.skySlime.getBlock()).getDefaultState();
  }

  @Nullable
  @Override
  public BlockState getVines() {
    return TinkerWorld.skySlimeVine.get().getDefaultState();
  }

  @Nullable
  @Override
  public ConfiguredFeature<?,?> getTreeFeature(Random random) {
    return TinkerStructures.SKY_SLIME_ISLAND_TREE;
  }
}
