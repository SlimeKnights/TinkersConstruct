package slimeknights.tconstruct.world.worldgen.islands.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
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
    return Blocks.CLAY.defaultBlockState();
  }

  @Override
  public BlockState getLakeFluid() {
    return Blocks.WATER.defaultBlockState();
  }

  @Override
  public BlockState getCongealedSlime(Random random) {
    return Blocks.SAND.defaultBlockState();
  }

  @Nullable
  @Override
  public BlockState getPlant(Random random) {
    Block block = random.nextInt(8) == 0 ? Blocks.FERN : Blocks.GRASS;
    return block.defaultBlockState();
  }

  @Nullable
  @Override
  public ConfiguredFeature<?,?> getTreeFeature(Random random) {
    // all variants except dark oak, no 2x2 trees
    return null; // TODO
    /*
    return switch (random.nextInt(10)) {
      // 40% oak
      case 0, 1, 2, 3 -> Features.OAK;
      // 30% birch
      case 4, 5, 6 -> Features.BIRCH;
      // 10% spruce
      case 7 -> Features.SPRUCE;
      // 10% acacia
      case 8 -> Features.ACACIA;
      // 10% jungle
      case 9 -> Features.JUNGLE_TREE_NO_VINE;
      default -> null;
    };
     */
  }
}
