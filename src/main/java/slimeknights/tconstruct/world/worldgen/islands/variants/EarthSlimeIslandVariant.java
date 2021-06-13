package slimeknights.tconstruct.world.worldgen.islands.variants;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerStructures;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

public class EarthSlimeIslandVariant extends AbstractSlimeIslandVariant {
  public EarthSlimeIslandVariant(int index, SlimeType dirtType) {
    super(index, dirtType, SlimeType.EARTH);
  }

  @Override
  public ResourceLocation getStructureName(String variantName) {
    return Util.getResource("slime_islands/earth/" + dirtType.getString() + "_" + variantName);
  }

  @Override
  protected SlimeType getCongealedSlimeType(Random random) {
    return SlimeType.EARTH;
  }

  @Override
  public BlockState getLakeFluid() {
    return Objects.requireNonNull(TinkerFluids.earthSlime.getBlock()).getDefaultState();
  }

  @Nullable
  @Override
  public ConfiguredFeature<?,?> getTreeFeature(Random random) {
    return TinkerStructures.EARTH_SLIME_TREE;
  }

  private static boolean isWater(ISeedReader world, BlockPos pos) {
    return world.isAirBlock(pos) || world.getBlockState(pos).getBlock() == Blocks.WATER;
  }

  @Override
  public boolean isPositionValid(ISeedReader world, BlockPos pos, ChunkGenerator generator) {
    BlockPos up = pos.up();
    if (isWater(world, up)) {
      for (Direction direction : Plane.HORIZONTAL) {
        if (!isWater(world, up.offset(direction))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
}
