package slimeknights.tconstruct.world.worldgen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;

import java.util.Random;

import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.BlockSlime;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.BlockSlimeDirt;
import slimeknights.tconstruct.world.block.BlockSlimeGrass;

public class MagmaSlimeIslandGenerator extends SlimeIslandGenerator {

  public static MagmaSlimeIslandGenerator INSTANCE = new MagmaSlimeIslandGenerator();

  protected SlimeLakeGenerator lakeGenMagma;
  protected SlimePlantGenerator plantGenMagma;
  protected SlimeTreeGenerator treeGenMagma;
  protected IBlockState dirtMagma;
  protected IBlockState grassMagma;

  public MagmaSlimeIslandGenerator() {
    air = Blocks.LAVA.getDefaultState();

    IBlockState slimeMagma = TinkerCommons.blockSlimeCongealed.getDefaultState().withProperty(BlockSlime.TYPE, BlockSlime.SlimeType.MAGMA);
    IBlockState slimeBlood = TinkerCommons.blockSlimeCongealed.getDefaultState().withProperty(BlockSlime.TYPE, BlockSlime.SlimeType.BLOOD);

    dirtMagma = TinkerWorld.slimeDirt.getDefaultState().withProperty(BlockSlimeDirt.TYPE, BlockSlimeDirt.DirtType.MAGMA);
    grassMagma = TinkerWorld.slimeGrass.getStateFromDirt(dirtMagma).withProperty(BlockSlimeGrass.FOLIAGE, BlockSlimeGrass.FoliageType.ORANGE);

    lakeGenMagma = new SlimeLakeGenerator(Blocks.LAVA.getDefaultState(), slimeMagma, slimeMagma, slimeMagma, slimeMagma, slimeMagma, slimeBlood);
    treeGenMagma = new SlimeTreeGenerator(5, 4, slimeMagma, TinkerWorld.slimeLeaves.getDefaultState().withProperty(BlockSlimeGrass.FOLIAGE, BlockSlimeGrass.FoliageType.ORANGE), null);
    plantGenMagma = new SlimePlantGenerator(BlockSlimeGrass.FoliageType.ORANGE, false);
  }

  @Override
  protected String getDataName() {
    return "MagmaIslands";
  }

  @Override
  public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
    if(!Config.genSlimeIslands) {
      return;
    }
    // do we generate in superflat?
    if(world.getWorldType() == WorldType.FLAT && !Config.genIslandsInSuperflat) {
      return;
    }

    // we only generate in hell
    if(!(world.provider instanceof WorldProviderHell)) {
      return;
    }

    markChunkForIslandGenerationAndGenerateMarked(random, chunkX, chunkZ, world, chunkProvider);
  }

  @Override
  protected int getGenerationChance() {
    return Config.magmaIslandsRate;
  }

  @Override
  protected void generateIslandInChunk(long seed, World world, int chunkX, int chunkZ) {
    Random random = new Random(seed);

    int y = 31; // lava lake surface is at 32
    int x = chunkX * 16 + 4 + random.nextInt(8);
    int z = chunkZ * 16 + 4 + random.nextInt(8);

    BlockPos pos = new BlockPos(x, y, z);

    // check if we got a bit of lava
    if(isLava(world, pos) &&
       isLava(world, pos.north()) &&
       isLava(world, pos.east()) &&
       isLava(world, pos.south()) &&
       isLava(world, pos.west())) {
      // technically this chunk counts as "island generated here" even if there was no lava
      // this is due to marking and generating chunks later
      generateIsland(random, world, x, z, y + 1, dirtMagma, grassMagma, null, lakeGenMagma, treeGenMagma, plantGenMagma);
    }
  }

  private boolean isLava(World world, BlockPos pos) {
    return world.getBlockState(pos).getBlock() == Blocks.LAVA;
  }
}
