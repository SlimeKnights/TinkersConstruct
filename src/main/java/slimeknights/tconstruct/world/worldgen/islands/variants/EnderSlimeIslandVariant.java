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
    return TinkerWorld.enderSlimeVine.get().getDefaultState();
  }

  @Override
  public BlockState getLakeFluid() {
    return Objects.requireNonNull(TinkerFluids.enderSlime.getBlock()).getDefaultState();
  }

  @Nullable
  @Override
  public ConfiguredFeature<?,?> getTreeFeature(Random random) {
    return TinkerStructures.ENDER_SLIME_ISLAND_TREE;
  }
}
