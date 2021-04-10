package slimeknights.tconstruct.world.worldgen.islands.end;

import com.mojang.serialization.Codec;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import slimeknights.tconstruct.world.worldgen.islands.SlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.islands.SlimeIslandVariant;

public class EndSlimeIslandStructure extends StructureFeature<DefaultFeatureConfig> {
  private static final String[] SIZES = new String[] { "0x1x0", "2x2x4", "4x1x6", "8x1x11", "11x1x11" };

  public EndSlimeIslandStructure(Codec<DefaultFeatureConfig> configCodec) {
    super(configCodec);
  }

  @Override
  public StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
    return Start::new;
  }

  @Override
  public String getName() {
    return "tconstruct:end_slime_island";
  }

  @Override
  public GenerationStep.Feature getGenerationStep() {
    return GenerationStep.Feature.SURFACE_STRUCTURES;
  }

  public static class Start extends StructureStart<DefaultFeatureConfig> {

    public Start(StructureFeature<DefaultFeatureConfig> structureIn, int chunkPosX, int chunkPosZ, BlockBox bounds, int references, long seed) {
      super(structureIn, chunkPosX, chunkPosZ, bounds, references, seed);
    }

    @Override
    public void init(DynamicRegistryManager registries, ChunkGenerator generator, StructureManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn, DefaultFeatureConfig config) {
      int x = chunkX * 16 + 4 + this.random.nextInt(8);
      int z = chunkZ * 16 + 4 + this.random.nextInt(8);

      BlockRotation rotation = BlockRotation.values()[this.random.nextInt(BlockRotation.values().length)];
      int i = 5;
      int j = 5;
      if (rotation == BlockRotation.CLOCKWISE_90) {
        i = -5;
      }
      else if (rotation == BlockRotation.CLOCKWISE_180) {
        i = -5;
        j = -5;
      }
      else if (rotation == BlockRotation.COUNTERCLOCKWISE_90) {
        j = -5;
      }

      int i1 = generator.getHeightInGround(x, z, Heightmap.Type.WORLD_SURFACE_WG);
      int j1 = generator.getHeightInGround(x, z + j, Heightmap.Type.WORLD_SURFACE_WG);
      int k1 = generator.getHeightInGround(x + i, z, Heightmap.Type.WORLD_SURFACE_WG);
      int l1 = generator.getHeightInGround(x + i, z + j, Heightmap.Type.WORLD_SURFACE_WG);

      int y = Math.min(Math.min(i1, j1), Math.min(k1, l1)) + 50 + this.random.nextInt(50) + 11;

      SlimeIslandVariant variant = SlimeIslandVariant.ENDER;
      BlockPos pos = new BlockPos(x, y, z);
      SlimeIslandPiece slimeIslandPiece = new SlimeIslandPiece(templateManagerIn, variant, SIZES[this.random.nextInt(SIZES.length)], pos, rotation);
      this.children.add(slimeIslandPiece);
      this.setBoundingBoxFromChildren();
    }
  }
}
