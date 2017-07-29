package slimeknights.tconstruct.shared.worldgen;

import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.BlockOre;

public class NetherOreGenerator implements IWorldGenerator {

  public static NetherOreGenerator INSTANCE = new NetherOreGenerator();

  public WorldGenMinable cobaltGen;
  public WorldGenMinable arditeGen;

  public NetherOreGenerator() {
    cobaltGen = new WorldGenMinable(TinkerCommons.blockOre.getStateFromMeta(BlockOre.OreTypes.COBALT.getMeta()),
                                    5,
                                    BlockMatcher.forBlock(Blocks.NETHERRACK));

    arditeGen = new WorldGenMinable(TinkerCommons.blockOre.getStateFromMeta(BlockOre.OreTypes.ARDITE.getMeta()),
                                    5,
                                    BlockMatcher.forBlock(Blocks.NETHERRACK));
  }

  @Override
  public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
    if(world.provider instanceof WorldProviderHell) {
      if(Config.genArdite) {
        generateNetherOre(arditeGen, Config.arditeRate, random, chunkX, chunkZ, world);
      }
      if(Config.genCobalt) {
        generateNetherOre(cobaltGen, Config.cobaltRate, random, chunkX, chunkZ, world);
      }
    }
  }

  public void generateNetherOre(WorldGenMinable gen, int rate, Random random, int chunkX, int chunkZ, World world) {
    BlockPos pos;
    for(int i = 0; i < rate; i += 2) {
      pos = new BlockPos(chunkX * 16, 32, chunkZ * 16);
      pos = pos.add(random.nextInt(16), random.nextInt(64), random.nextInt(16));
      gen.generate(world, random, pos);

      pos = new BlockPos(chunkX * 16, 0, chunkZ * 16);
      pos = pos.add(random.nextInt(16), random.nextInt(128), random.nextInt(16));
      gen.generate(world, random, pos);
    }
  }
}
