package slimeknights.tconstruct.world.worldgen.islands.variants;

import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.StructureProcessor;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Base interface for all island variants, to make extension easier
 */
public interface IIslandVariant {
  /** Gets the index for serializing this island, should be unique */
  int getIndex();

  /**
   * Gets the name of the structure NBT
   * @param variantName  Variant size string
   * @return  Structure NBT file name
   */
  ResourceLocation getStructureName(String variantName);

  /** Gets the block to fill the area below the lake with */
  BlockState getLakeBottom();

  /** Gets the block to fill the lake with */
  BlockState getLakeFluid();

  /** Gets the decorate lake edges with */
  BlockState getCongealedSlime(Random random);

  /** Gets the vine block state to place around the island, needs to extend slime vines, return null to prevent vine placement */
  @Nullable
  default BlockState getVines() {
    return null;
  }

  /** Gets a plant to place on top of the island, return null to prevent plant placement */
  @Nullable
  default BlockState getPlant(Random random) {
    return null;
  }

  /** Gets the tree to place on top of the island, return null to prevent tree placement */
  @Nullable
  default ConfiguredFeature<?, ?> getTreeFeature(Random random) {
    return null;
  }

  /** Gets the structure processor to use for this island */
  default StructureProcessor getStructureProcessor() {
    return BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK;
  }

  /** Checks if the given position is valid for this island */
  default boolean isPositionValid(ISeedReader world, BlockPos pos, ChunkGenerator generator) {
    return true;
  }
}
