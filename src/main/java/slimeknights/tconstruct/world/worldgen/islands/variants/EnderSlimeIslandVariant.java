package slimeknights.tconstruct.world.worldgen.islands.variants;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nullable;
import java.util.Objects;

/** Slime island variant for the end */
public class EnderSlimeIslandVariant extends AbstractSlimeIslandVariant {
  public EnderSlimeIslandVariant(int index) {
    super(index, SlimeType.ENDER, SlimeType.ENDER);
  }

  @Override
  public ResourceLocation getStructureName(String variantName) {
    return TConstruct.getResource("slime_islands/ender/" + variantName);
  }

  @Override
  protected SlimeType getCongealedSlimeType(RandomSource random) {
    return SlimeType.ENDER;
  }

  @Nullable
  @Override
  public BlockState getVines() {
    return TinkerWorld.enderSlimeVine.get().defaultBlockState();
  }

  @Override
  public BlockState getLakeFluid() {
    return Objects.requireNonNull(TinkerFluids.enderSlime.getBlock()).defaultBlockState();
  }

  @Nullable
  @Override
  public ConfiguredFeature<?,?> getTreeFeature(RandomSource random) {
    return TinkerStructures.enderSlimeIslandTree.get();
  }
}
