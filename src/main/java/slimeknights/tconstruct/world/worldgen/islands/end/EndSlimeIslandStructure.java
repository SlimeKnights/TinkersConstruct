package slimeknights.tconstruct.world.worldgen.islands.end;

import com.mojang.serialization.Codec;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import slimeknights.tconstruct.world.worldgen.islands.SlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.islands.SlimeIslandVariant;

public class EndSlimeIslandStructure extends Structure<NoFeatureConfig> {
  private static final String[] SIZES = new String[] { "0x1x0", "2x2x4", "4x1x6", "8x1x11", "11x1x11" };

  public EndSlimeIslandStructure(Codec<NoFeatureConfig> configCodec) {
    super(configCodec);
  }

  @Override
  public IStartFactory<NoFeatureConfig> getStartFactory() {
    return Start::new;
  }

  @Override
  public String getStructureName() {
    return "tconstruct:end_slime_island";
  }

  @Override
  public GenerationStage.Decoration getDecorationStage() {
    return GenerationStage.Decoration.SURFACE_STRUCTURES;
  }

  public static class Start extends StructureStart<NoFeatureConfig> {

    public Start(Structure<NoFeatureConfig> structureIn, int chunkPosX, int chunkPosZ, MutableBoundingBox bounds, int references, long seed) {
      super(structureIn, chunkPosX, chunkPosZ, bounds, references, seed);
    }

    @Override
    public void func_230364_a_(DynamicRegistries registries, ChunkGenerator generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn, NoFeatureConfig config) {
      int x = chunkX * 16 + 4 + this.rand.nextInt(8);
      int z = chunkZ * 16 + 4 + this.rand.nextInt(8);

      Rotation rotation = Rotation.values()[this.rand.nextInt(Rotation.values().length)];
      int i = 5;
      int j = 5;
      if (rotation == Rotation.CLOCKWISE_90) {
        i = -5;
      }
      else if (rotation == Rotation.CLOCKWISE_180) {
        i = -5;
        j = -5;
      }
      else if (rotation == Rotation.COUNTERCLOCKWISE_90) {
        j = -5;
      }

      int i1 = generator.getNoiseHeightMinusOne(x, z, Heightmap.Type.WORLD_SURFACE_WG);
      int j1 = generator.getNoiseHeightMinusOne(x, z + j, Heightmap.Type.WORLD_SURFACE_WG);
      int k1 = generator.getNoiseHeightMinusOne(x + i, z, Heightmap.Type.WORLD_SURFACE_WG);
      int l1 = generator.getNoiseHeightMinusOne(x + i, z + j, Heightmap.Type.WORLD_SURFACE_WG);

      int y = Math.min(Math.min(i1, j1), Math.min(k1, l1)) + 50 + this.rand.nextInt(50) + 11;

      SlimeIslandVariant variant = SlimeIslandVariant.ENDER;
      BlockPos pos = new BlockPos(x, y, z);
      SlimeIslandPiece slimeIslandPiece = new SlimeIslandPiece(templateManagerIn, variant, SIZES[this.rand.nextInt(SIZES.length)], pos, rotation);
      this.components.add(slimeIslandPiece);
      this.recalculateStructureSize();
    }
  }
}
