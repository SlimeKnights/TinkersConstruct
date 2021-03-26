package slimeknights.tconstruct.world.worldgen.islands.nether;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import slimeknights.tconstruct.world.worldgen.islands.SlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.islands.SlimeIslandVariant;

import java.util.List;

public class NetherSlimeIslandStructure extends Structure<NoFeatureConfig> {
  private static final String[] SIZES = new String[] { "0x1x0", "2x2x4", "4x1x6", "8x1x11", "11x1x11" };

  private static final List<MobSpawnInfo.Spawners> STRUCTURE_MONSTERS = ImmutableList.of(
    new MobSpawnInfo.Spawners(EntityType.MAGMA_CUBE, 150, 4, 6)
  );

  public NetherSlimeIslandStructure(Codec<NoFeatureConfig> configCodec) {
    super(configCodec);
  }

  @Override
  public Structure.IStartFactory<NoFeatureConfig> getStartFactory() {
    return NetherSlimeIslandStructure.Start::new;
  }

  @Override
  public String getStructureName() {
    return "tconstruct:nether_slime_island";
  }

  @Override
  public GenerationStage.Decoration getDecorationStage() {
    return GenerationStage.Decoration.UNDERGROUND_DECORATION;
  }

  @Override
  public List<MobSpawnInfo.Spawners> getDefaultSpawnList() {
    return STRUCTURE_MONSTERS;
  }

  public static class Start extends StructureStart<NoFeatureConfig> {

    public Start(Structure<NoFeatureConfig> structureIn, int chunkPosX, int chunkPosZ, MutableBoundingBox bounds, int references, long seed) {
      super(structureIn, chunkPosX, chunkPosZ, bounds, references, seed);
    }

    @Override
    public void func_230364_a_(DynamicRegistries registries, ChunkGenerator generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn, NoFeatureConfig config) {
      int x = chunkX * 16 + 4 + this.rand.nextInt(8);
      int y = 25;
      int z = chunkZ * 16 + 4 + this.rand.nextInt(8);

      Rotation rotation = Rotation.values()[this.rand.nextInt(Rotation.values().length)];

      SlimeIslandVariant variant = SlimeIslandVariant.BLOOD;

      BlockPos pos = new BlockPos(x, y, z);
      SlimeIslandPiece slimeIslandPiece = new SlimeIslandPiece(templateManagerIn, variant, SIZES[this.rand.nextInt(SIZES.length)], pos, rotation);
      this.components.add(slimeIslandPiece);
      this.recalculateStructureSize();
    }
  }
}
