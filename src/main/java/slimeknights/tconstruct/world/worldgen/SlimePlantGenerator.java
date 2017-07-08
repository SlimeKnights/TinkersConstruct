package slimeknights.tconstruct.world.worldgen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.BlockSlimeGrass;
import slimeknights.tconstruct.world.block.BlockTallSlimeGrass;

public class SlimePlantGenerator implements IWorldGenerator {

  public final BlockSlimeGrass.FoliageType foliageType;
  public final boolean clumped;

  public SlimePlantGenerator(BlockSlimeGrass.FoliageType foliageType, boolean clumped) {
    this.foliageType = foliageType;
    this.clumped = clumped;
  }

  @Override
  public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {

  }

  public void generatePlants(Random random, World world, BlockPos from, BlockPos to, int attempts) {
    int xd = to.getX() - from.getX();
    int yd = to.getY() - from.getY();
    int zd = to.getZ() - from.getZ();

    IBlockState state = TinkerWorld.slimeGrassTall.getDefaultState().withProperty(BlockTallSlimeGrass.FOLIAGE, foliageType);

    for(int i = 0; i < attempts; i++) {
      BlockPos pos = from.add(random.nextInt(xd), 0, random.nextInt(zd));
      if(clumped) {
        pos = pos.add(-random.nextInt(xd), 0, -random.nextInt(zd));
      }

      for(int j = 0; j < yd && world.isAirBlock(pos.down()); j++) {
        pos = pos.down();
      }

      state = state.cycleProperty(BlockTallSlimeGrass.TYPE);

      // suitable position?
      if(world.isAirBlock(pos) && TinkerWorld.slimeGrassTall.canBlockStay(world, pos, state)) {
        world.setBlockState(pos, state, 2);
      }
    }
  }
}
