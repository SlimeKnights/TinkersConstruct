package slimeknights.tconstruct.world.worldgen.islands.nether;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import slimeknights.tconstruct.world.worldgen.islands.SlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.islands.SlimeIslandVariant;

import java.util.List;

public class NetherSlimeIslandStructure extends StructureFeature<DefaultFeatureConfig> {
  private static final String[] SIZES = new String[] { "0x1x0", "2x2x4", "4x1x6", "8x1x11", "11x1x11" };

  private static final List<SpawnSettings.SpawnEntry> STRUCTURE_MONSTERS = ImmutableList.of(
    new SpawnSettings.SpawnEntry(EntityType.MAGMA_CUBE, 150, 4, 6)
  );

  public NetherSlimeIslandStructure(Codec<DefaultFeatureConfig> configCodec) {
    super(configCodec);
  }

  @Override
  public StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
    return Start::new;
  }

  @Override
  public String getName() {
    return "tconstruct:nether_slime_island";
  }

  @Override
  public GenerationStep.Feature getGenerationStep() {
    return GenerationStep.Feature.UNDERGROUND_DECORATION;
  }

  @Override
  public List<SpawnSettings.SpawnEntry> getDefaultSpawnList() {
    return STRUCTURE_MONSTERS;
  }

  public static class Start extends StructureStart<DefaultFeatureConfig> {

    public Start(StructureFeature<DefaultFeatureConfig> structureIn, int chunkPosX, int chunkPosZ, BlockBox bounds, int references, long seed) {
      super(structureIn, chunkPosX, chunkPosZ, bounds, references, seed);
    }

    @Override
    public void func_230364_a_(DynamicRegistryManager registries, ChunkGenerator generator, StructureManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn, DefaultFeatureConfig config) {
      int x = chunkX * 16 + 4 + this.random.nextInt(8);
      int y = 25;
      int z = chunkZ * 16 + 4 + this.random.nextInt(8);

      BlockRotation rotation = BlockRotation.values()[this.random.nextInt(BlockRotation.values().length)];

      SlimeIslandVariant variant = SlimeIslandVariant.BLOOD;

      BlockPos pos = new BlockPos(x, y, z);
      SlimeIslandPiece slimeIslandPiece = new SlimeIslandPiece(templateManagerIn, variant, SIZES[this.random.nextInt(SIZES.length)], pos, rotation);
      this.children.add(slimeIslandPiece);
      this.setBoundingBoxFromChildren();
    }
  }
}
