package slimeknights.tconstruct.world.worldgen.islands.variants;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

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
  protected SlimeType getCongealedSlimeType(Random random) {
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
  public ConfiguredFeature<?,?> getTreeFeature(Random random) {
    return TinkerStructures.ENDER_SLIME_ISLAND_TREE;
  }
}
