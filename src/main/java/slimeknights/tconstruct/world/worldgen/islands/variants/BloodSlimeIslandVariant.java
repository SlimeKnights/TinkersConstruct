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
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerStructures;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

/**
 * Nether slime island variant that spawns in lava oceans
 */
public class BloodSlimeIslandVariant extends AbstractSlimeIslandVariant {
  public BloodSlimeIslandVariant(int index) {
    super(index, SlimeType.ICHOR, SlimeType.BLOOD);
  }

  @Override
  public ResourceLocation getStructureName(String variantName) {
    return Util.getResource("slime_islands/blood/" + variantName);
  }

  @Override
  protected SlimeType getCongealedSlimeType(Random random) {
    return random.nextBoolean() ? SlimeType.BLOOD : SlimeType.ICHOR;
  }

  @Override
  public BlockState getLakeFluid() {
    return Objects.requireNonNull(TinkerFluids.magma.getBlock()).getDefaultState();
  }

  @Nullable
  @Override
  public ConfiguredFeature<?,?> getTreeFeature(Random random) {
    return TinkerStructures.BLOOD_SLIME_TREE;
  }

  @Override
  public StructureProcessor getStructureProcessor() {
    return BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK;
  }

  private static boolean isLava(ISeedReader world, BlockPos pos) {
    return world.isAirBlock(pos) || world.getBlockState(pos).getBlock() == Blocks.LAVA;
  }

  @Override
  public boolean isPositionValid(ISeedReader world, BlockPos pos, ChunkGenerator generator) {
    BlockPos up = pos.up();
    if (isLava(world, up)) {
      for (Direction direction : Plane.HORIZONTAL) {
        if (!isLava(world, up.offset(direction))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
}
