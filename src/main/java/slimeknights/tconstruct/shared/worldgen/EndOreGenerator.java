package slimeknights.tconstruct.shared.worldgen;

import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.BlockOre2;

public class EndOreGenerator implements IWorldGenerator {

  public static EndOreGenerator INSTANCE = new EndOreGenerator();

  public WorldGenMinable enderitumGen;
  public WorldGenMinable titaniumGen;

  public EndOreGenerator() {
    enderitumGen = new WorldGenMinable(TinkerCommons.blockOre2.getStateFromMeta(BlockOre2.OreTypes.ENDERITUM.getMeta()),
                                    5,
                                    BlockMatcher.forBlock(Blocks.END_STONE));

    titaniumGen = new WorldGenMinable(TinkerCommons.blockOre2.getStateFromMeta(BlockOre2.OreTypes.TITANIUM.getMeta()),
                                    5,
                                    BlockMatcher.forBlock(Blocks.END_STONE));                               
  }

  @Override
  public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
    if (world.provider instanceof WorldProviderEnd) {
      if (Config.genEnderitum) {
        generateEndOre(enderitumGen, Config.arditeRate, random, chunkX, chunkZ, world);
      }
      if (Config.genTitanium) {
        generateEndOre(titaniumGen, Config.titaniumRate, random, chunkX, chunkZ, world);
      }
    }
  }

  public void generateEndOre(WorldGenMinable gen, int rate, Random random, int chunkX, int chunkZ, World world) {
    BlockPos pos;
    for (int i = 0; i < rate; i += 2) {
      pos = new BlockPos(chunkX * 16, 32, chunkZ * 16);
      pos = pos.add(random.nextInt(16), random.nextInt(64), random.nextInt(16));
      gen.generate(world, random, pos);

      pos = new BlockPos(chunkX * 16, 0, chunkZ * 16);
      pos = pos.add(random.nextInt(16), random.nextInt(128), random.nextInt(16));
      gen.generate(world, random, pos);
    }
  }
}
