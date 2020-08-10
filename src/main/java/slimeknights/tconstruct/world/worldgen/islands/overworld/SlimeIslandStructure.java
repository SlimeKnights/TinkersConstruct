package slimeknights.tconstruct.world.worldgen.islands.overworld;

/*
import com.mojang.datafixers.Dynamic;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.ScatteredStructure;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import slimeknights.tconstruct.world.worldgen.islands.SlimeIslandVariant;

import java.util.Random;
import java.util.function.Function;

public class SlimeIslandStructure extends ScatteredStructure<NoFeatureConfig> {

  public SlimeIslandStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51476_1_) {
    super(p_i51476_1_);
  }

  @Override
  protected ChunkPos getStartPositionForPosition(ChunkGenerator<?> chunkGenerator, Random random, int x, int z, int spacingOffsetsX, int spacingOffsetsZ) {
    random.setSeed(this.getSeedModifier());
    int distance = this.getDistance();
    int separation = this.getSeparation();
    int x1 = x + distance * spacingOffsetsX;
    int z1 = z + distance * spacingOffsetsZ;
    int x2 = x1 < 0 ? x1 - distance + 1 : x1;
    int z2 = z1 < 0 ? z1 - distance + 1 : z1;
    int x3 = x2 / distance;
    int z3 = z2 / distance;
    ((SharedSeedRandom) random).setLargeFeatureSeedWithSalt(chunkGenerator.getSeed(), x3, z3, this.getSeedModifier());
    x3 = x3 * distance;
    z3 = z3 * distance;
    x3 = x3 + random.nextInt(distance - separation);
    z3 = z3 + random.nextInt(distance - separation);

    return new ChunkPos(x3, z3);
  }

  protected int getDistance() {
    return 20;
  }

  protected int getSeparation() {
    return 11;
  }

  @Override
  protected int getSeedModifier() {
    return 14357800;
  }

  @Override
  public Structure.IStartFactory getStartFactory() {
    return SlimeIslandStructure.Start::new;
  }

  @Override
  public String getStructureName() {
    return "Slime_Island";
  }

  @Override
  public int getSize() {
    return 8;
  }

  public static class Start extends StructureStart<NoFeatureConfig> {

    public Start(Structure<NoFeatureConfig> structureIn, int chunkPosX, int chunkPosZ, MutableBoundingBox bounds, int references, long seed) {
      super(structureIn, chunkPosX, chunkPosZ, bounds, references, seed);
    }

    @Override
    public void func_230364_a_(ChunkGenerator generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn, NoFeatureConfig config) {
      int x = chunkX * 16 + 4 + this.rand.nextInt(8);
      int z = chunkZ * 16 + 4 + this.rand.nextInt(8);

      Rotation rotation = Rotation.values()[this.rand.nextInt(Rotation.values().length)];
      int i = 5;
      int j = 5;
      if (rotation == Rotation.CLOCKWISE_90) {
        i = -5;
      } else if (rotation == Rotation.CLOCKWISE_180) {
        i = -5;
        j = -5;
      } else if (rotation == Rotation.COUNTERCLOCKWISE_90) {
        j = -5;
      }

      int k = x + 7;
      int l = z + 7;
      int i1 = generator.getNoiseHeightMinusOne(x, z, Heightmap.Type.WORLD_SURFACE_WG);
      int j1 = generator.getNoiseHeightMinusOne(x, z + j, Heightmap.Type.WORLD_SURFACE_WG);
      int k1 = generator.getNoiseHeightMinusOne(x + i, z, Heightmap.Type.WORLD_SURFACE_WG);
      int l1 = generator.getNoiseHeightMinusOne(x + i, z + j, Heightmap.Type.WORLD_SURFACE_WG);

      int y = Math.min(Math.min(i1, j1), Math.min(k1, l1)) + 50 + this.rand.nextInt(50) + 11;

      int rnr = this.rand.nextInt(10);
      SlimeIslandVariant variant = SlimeIslandVariant.BLUE;
      String[] sizes = new String[]{"0x1x0", "2x2x4", "4x1x6", "8x1x11", "11x1x11"};

      if (rnr <= 1) {
        variant = SlimeIslandVariant.PURPLE;
      } else if (rnr < 6) {
        variant = SlimeIslandVariant.GREEN;
      }

      SlimeIslandPiece slimeIslandPiece = new SlimeIslandPiece(templateManagerIn, variant, sizes[this.rand.nextInt(sizes.length)], new BlockPos(x, y, z), rotation);
      this.components.add(slimeIslandPiece);
      this.recalculateStructureSize();
    }
  }
}
*/
