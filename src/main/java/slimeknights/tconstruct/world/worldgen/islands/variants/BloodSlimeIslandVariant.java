package slimeknights.tconstruct.world.worldgen.islands.variants;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.fluids.TinkerFluids;
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
    return TConstruct.getResource("slime_islands/blood/" + variantName);
  }

  @Override
  protected SlimeType getCongealedSlimeType(Random random) {
    return random.nextBoolean() ? SlimeType.BLOOD : SlimeType.ICHOR;
  }

  @Override
  public BlockState getLakeFluid() {
    return Objects.requireNonNull(TinkerFluids.magma.getBlock()).defaultBlockState();
  }

  @Nullable
  @Override
  public ConfiguredFeature<?,?> getTreeFeature(Random random) {
    return TinkerStructures.BLOOD_SLIME_ISLAND_FUNGUS;
  }

  @Override
  public StructureProcessor getStructureProcessor() {
    return BlockIgnoreProcessor.STRUCTURE_AND_AIR;
  }

  private static boolean isLava(WorldGenLevel world, BlockPos pos) {
    return world.isEmptyBlock(pos) || world.getBlockState(pos).getBlock() == Blocks.LAVA;
  }

  @Override
  public boolean isPositionValid(WorldGenLevel world, BlockPos pos, ChunkGenerator generator) {
    BlockPos up = pos.above();
    if (isLava(world, up)) {
      for (Direction direction : Plane.HORIZONTAL) {
        if (!isLava(world, up.relative(direction))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
}
