package slimeknights.tconstruct.world.worldgen.islands.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Features;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;
import java.util.Random;

@RequiredArgsConstructor
public class ClayIslandVariant implements IIslandVariant {
  @Getter
  private final int index;

  @Override
  public ResourceLocation getStructureName(String variantName) {
    return TConstruct.getResource("slime_islands/vanilla/" + variantName);
  }

  @Override
  public BlockState getLakeBottom() {
    return Blocks.CLAY.getDefaultState();
  }

  @Override
  public BlockState getLakeFluid() {
    return Blocks.WATER.getDefaultState();
  }

  @Override
  public BlockState getCongealedSlime(Random random) {
    return Blocks.SAND.getDefaultState();
  }

  @Nullable
  @Override
  public BlockState getPlant(Random random) {
    Block block = random.nextInt(8) == 0 ? Blocks.FERN : Blocks.GRASS;
    return block.getDefaultState();
  }

  @Nullable
  @Override
  public ConfiguredFeature<?,?> getTreeFeature(Random random) {
    // all variants except dark oak, no 2x2 trees
    switch (random.nextInt(10)) {
      // 40% oak
      case 0: case 1: case 2: case 3: return Features.OAK;
      // 30% birch
      case 4: case 5: case 6: return Features.BIRCH;
      // 10% spruce
      case 7: return Features.SPRUCE;
      // 10% acacia
      case 8: return Features.ACACIA;
      // 10% jungle
      case 9: return Features.JUNGLE_TREE_NO_VINE;
    }
    return null;
  }
}
