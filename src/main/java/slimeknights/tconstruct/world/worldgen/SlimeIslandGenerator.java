package slimeknights.tconstruct.world.worldgen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.awt.geom.Ellipse2D;
import java.util.Random;

import slimeknights.tconstruct.common.Config;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.BlockSlime;
import slimeknights.tconstruct.world.block.BlockSlimeDirt;
import slimeknights.tconstruct.world.block.BlockSlimeGrass;

public class SlimeIslandGenerator implements IWorldGenerator {

  // defines the jaggedness of the surface/bottom
  protected int randomness = 1; // 2% chance to have an abnormality in the surface

  protected SlimeLakeGenerator lakeGenGreen;
  protected SlimeLakeGenerator lakeGenBlue;
  protected SlimeLakeGenerator lakeGenPurple;

  protected SlimeTreeGenerator treeGenBlue;
  protected SlimeTreeGenerator treeGenPurple;

  public SlimeIslandGenerator() {
    IBlockState slimeGreen = TinkerWorld.slimeBlockCongealed.getDefaultState().withProperty(BlockSlime.TYPE, BlockSlime.SlimeType.GREEN);
    IBlockState slimeBlue = TinkerWorld.slimeBlockCongealed.getDefaultState().withProperty(BlockSlime.TYPE, BlockSlime.SlimeType.BLUE);
    IBlockState slimePurple = TinkerWorld.slimeBlockCongealed.getDefaultState().withProperty(BlockSlime.TYPE, BlockSlime.SlimeType.PURPLE);

    lakeGenGreen = new SlimeLakeGenerator(Blocks.water.getDefaultState(), slimeGreen, slimeGreen, slimeBlue, slimePurple);
    lakeGenBlue = new SlimeLakeGenerator(Blocks.water.getDefaultState(), slimeBlue, slimeGreen, slimeBlue, slimePurple);
    lakeGenPurple = new SlimeLakeGenerator(Blocks.water.getDefaultState(), slimePurple, slimePurple);

    treeGenBlue = new SlimeTreeGenerator(5, 4, slimeGreen, TinkerWorld.slimeLeaves.getDefaultState().withProperty(BlockSlimeGrass.FOLIAGE, BlockSlimeGrass.FoliageType.BLUE));
    treeGenPurple = new SlimeTreeGenerator(5, 4, slimeGreen, TinkerWorld.slimeLeaves.getDefaultState().withProperty(BlockSlimeGrass.FOLIAGE, BlockSlimeGrass.FoliageType.PURPLE));
  }

  protected boolean shouldGenerateInDimension(int id) {
    return id != 1 && id != -1;
  }

  @Override
  public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
    // do we generate in superflat?
    if(world.getWorldType() == WorldType.FLAT && !Config.genIslandsInSuperflat) {
      return;
    }

    // should generate in this dimension?
    if(!shouldGenerateInDimension(world.provider.getDimensionId())) {
      return;
    }

    // do we generate in this chunk?
    if(random.nextInt(Config.slimeIslandsRate) > 0) {
      return;
    }

    // We do. determine parameters of the slime island!
    // defoult is a blue island
    BlockSlimeGrass.FoliageType grass = BlockSlimeGrass.FoliageType.BLUE;
    BlockSlimeDirt.DirtType dirt = BlockSlimeDirt.DirtType.BLUE;
    SlimeLakeGenerator lakeGen = lakeGenBlue;
    SlimeTreeGenerator treeGenerator = treeGenPurple; // purple trees on blue/green islands

    int rnr = random.nextInt(10);
    // purple island.. rare!
    if(rnr <= 1) {
      grass = BlockSlimeGrass.FoliageType.PURPLE;
      dirt = BlockSlimeDirt.DirtType.PURPLE;
      lakeGen = lakeGenPurple;
      treeGenerator = treeGenBlue; // blue trees on purple grass. yay
    }
    // green island.. not so rare
    else if(rnr < 6) {
      dirt = BlockSlimeDirt.DirtType.GREEN;
      lakeGen = lakeGenGreen;
    }

    IBlockState dirtState = TinkerWorld.slimeDirt.getDefaultState().withProperty(BlockSlimeDirt.TYPE, dirt);
    IBlockState grassState = TinkerWorld.slimeGrass.getStateFromDirt(dirtState).withProperty(BlockSlimeGrass.FOLIAGE, grass);

    int x = chunkX*16 + 7 + random.nextInt(6) - 3;
    int z = chunkZ*16 + 7 + random.nextInt(6) - 3;

    generateIsland(random, world, x, z, dirtState, grassState, lakeGen, treeGenerator);
  }

  public void generateIsland(Random random, World world, int xPos, int zPos, IBlockState dirt, IBlockState grass, SlimeLakeGenerator lakeGenerator, SlimeTreeGenerator treeGenerator) {
    int xRange = 20 + random.nextInt(13);
    int zRange = 20 + random.nextInt(13);
    int yRange = 11 + random.nextInt(3);
    int height = yRange;
    //int top = height;

    int yBottom = world.getHeight(new BlockPos(xPos,0,zPos)).getY() + 50 + random.nextInt(50);

    BlockPos center = new BlockPos(xPos, yBottom + height, zPos);
    BlockPos start = new BlockPos(xPos - xRange/2, yBottom, zPos - zRange/2);

    // the elliptic shape
    Ellipse2D.Double ellipse = new Ellipse2D.Double(0, 0, xRange, zRange);

    // Basic shape
    for (int x = 0; x <= xRange; x++)
    {
      for (int z = 0; z <= zRange; z++)
      {
        for (int y = 0; y <= yRange; y++)
        {
          if (ellipse.contains(x, z) && world.isAirBlock(start.add(x,y,z))) {
            world.setBlockState(start.add(x,y,z), dirt, 2);
          }
        }
      }
    }

    // now we have a cylindric-elliptic shape floating 50+ blocks above the ground. yaaaaay
    // Erode bottom
    int erode_height = 8;
    for (int x = 0; x <= xRange; x++)
    {
      for (int z = 0; z <= zRange; z++)
      {
        for (int y = 0; y <= erode_height; y++)
        {
          // we go top down
          BlockPos pos1 = start.add(x,erode_height - y,z);
          BlockPos pos2 = start.add(xRange - x,erode_height - y, zRange - z);

          for(BlockPos pos : new BlockPos[]{pos1, pos2}) {
            if(world.getBlockState(pos.add(-1,+1, 0)) != dirt ||
               world.getBlockState(pos.add(+1,+1, 0)) != dirt ||
               world.getBlockState(pos.add( 0,+1,-1)) != dirt ||
               world.getBlockState(pos.add(-1,+1,+1)) != dirt ||
               random.nextInt(100) <= randomness) {
              world.setBlockState(pos, Blocks.air.getDefaultState(), 2);
            }
          }
        }
      }
    }

    // Erode top
    erode_height = 2;
    for (int x = 0; x <= xRange; x++)
    {
      for (int z = 0; z <= zRange; z++)
      {
        for (int y = 0; y <= erode_height; y++)
        {
          // bottom up, starting with top - erosion layers
          BlockPos pos1 = start.add(x, y + height - erode_height + 2, z);
          BlockPos pos2 = start.add(xRange - x, y + height - erode_height + 2, zRange - z);


          for(BlockPos pos : new BlockPos[]{pos1, pos2}) {
            BlockPos below = pos.down();
            if(world.getBlockState(below.north()) != dirt
               || world.getBlockState(below.east()) != dirt
               || world.getBlockState(below.south()) != dirt
               || world.getBlockState(below.west()) != dirt) {
              world.setBlockState(pos, Blocks.air.getDefaultState(), 2);
            }
          }
        }
      }
    }

    // make surface grass
    for (int x = 0; x <= xRange; x++)
    {
      for (int z = 0; z <= zRange; z++)
      {
        BlockPos top = start.add(x, height, z);
        for (int y = 0; y <= height; y++)
        {
          BlockPos pos = top.down(y);
          if(world.getBlockState(pos) == dirt && world.isAirBlock(pos.up())) {
            world.setBlockState(pos, grass);
            break;
          }
        }
      }
    }

    // lake
    if(lakeGenerator != null) {
      //System.out.println(center.toString());
      lakeGenerator.generateLake(random, world, center);
    }

    if(treeGenerator != null) {
      // trees
      for(int i = 0; i < 3; i++) {
        BlockPos pos = start.add(random.nextInt(xRange), height, random.nextInt(zRange));
        treeGenerator.generateTree(random, world, pos);
      }
    }
  }
}
