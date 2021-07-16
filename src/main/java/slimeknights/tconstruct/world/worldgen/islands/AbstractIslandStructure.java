package slimeknights.tconstruct.world.worldgen.islands;

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
import slimeknights.tconstruct.world.worldgen.islands.variants.IIslandVariant;

import java.util.Objects;
import java.util.Random;

public abstract class AbstractIslandStructure extends Structure<NoFeatureConfig> {
  protected static final String[] SIZES = new String[] { "0x1x0", "2x2x4", "4x1x6", "8x1x11", "11x1x11" };
  public AbstractIslandStructure() {
    super(NoFeatureConfig.CODEC);
  }

  @Override
  public Structure.IStartFactory<NoFeatureConfig> getStartFactory() {
    return DefaultStart::new;
  }

  @Override
  public String getStructureName() {
    return Objects.requireNonNull(getRegistryName()).toString();
  }

  @Override
  public GenerationStage.Decoration getDecorationStage() {
    return GenerationStage.Decoration.SURFACE_STRUCTURES;
  }

  /** Gets the variant for this island */
  protected abstract IIslandVariant getVariant(Random random);

  /** Gets the height to generate this island */
  protected int getHeight(ChunkGenerator generator, Rotation rotation, int x, int z, Random random) {
    int xOffset;
    int yOffset;
    switch (rotation) {
      case CLOCKWISE_90:
        xOffset = -5;
        yOffset = 5;
        break;
      case CLOCKWISE_180:
        xOffset = -5;
        yOffset = -5;
        break;
      case COUNTERCLOCKWISE_90:
        xOffset = 5;
        yOffset = -5;
        break;
      default:
        xOffset = 5;
        yOffset = 5;
        break;
    }

    // determine height
    int minXMinZ = generator.getNoiseHeightMinusOne(x, z, Heightmap.Type.WORLD_SURFACE_WG);
    int minXMaxZ = generator.getNoiseHeightMinusOne(x, z + yOffset, Heightmap.Type.WORLD_SURFACE_WG);
    int maxXMinZ = generator.getNoiseHeightMinusOne(x + xOffset, z, Heightmap.Type.WORLD_SURFACE_WG);
    int maxXMaxZ = generator.getNoiseHeightMinusOne(x + xOffset, z + yOffset, Heightmap.Type.WORLD_SURFACE_WG);
    return Math.min(Math.min(minXMinZ, minXMaxZ), Math.min(maxXMinZ, maxXMaxZ)) + 50 + random.nextInt(50) + 11;
  }

  public class DefaultStart extends StructureStart<NoFeatureConfig> {
    public DefaultStart(Structure<NoFeatureConfig> structureIn, int chunkPosX, int chunkPosZ, MutableBoundingBox bounds, int references, long seed) {
      super(structureIn, chunkPosX, chunkPosZ, bounds, references, seed);
    }

    @Override
    public void func_230364_a_(DynamicRegistries registries, ChunkGenerator generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn, NoFeatureConfig config) {
      // determine orientation
      Rotation rotation = Rotation.values()[this.rand.nextInt(Rotation.values().length)];
      // determine coords
      int x = chunkX * 16 + 4 + this.rand.nextInt(8);
      int z = chunkZ * 16 + 4 + this.rand.nextInt(8);
      int y = getHeight(generator, rotation, x, z, this.rand);

      IIslandVariant variant = getVariant(rand);
      // fetch the tree now so its consistent on the whole island
      SlimeIslandPiece slimeIslandPiece = new SlimeIslandPiece(templateManagerIn, variant, SIZES[this.rand.nextInt(SIZES.length)], new BlockPos(x, y, z), variant.getTreeFeature(rand), rotation);
      this.components.add(slimeIslandPiece);
      this.recalculateStructureSize();
    }
  }
}
