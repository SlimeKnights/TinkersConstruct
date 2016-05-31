package slimeknights.tconstruct.world.worldgen;

import gnu.trove.map.hash.TIntObjectHashMap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.awt.geom.Ellipse2D;
import java.util.Random;

import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.BlockSlime;
import slimeknights.tconstruct.world.block.BlockSlimeDirt;
import slimeknights.tconstruct.world.block.BlockSlimeGrass;
import slimeknights.tconstruct.world.block.BlockSlimeVine;

public class SlimeIslandGenerator implements IWorldGenerator {

  public static SlimeIslandGenerator INSTANCE = new SlimeIslandGenerator();

  // defines the jaggedness of the surface/bottom
  protected int randomness = 1; // 2% chance to have an abnormality in the surface

  protected SlimeLakeGenerator lakeGenGreen;
  protected SlimeLakeGenerator lakeGenBlue;
  protected SlimeLakeGenerator lakeGenPurple;

  protected SlimePlantGenerator plantGenBlue;
  protected SlimePlantGenerator plantGenPurple;

  protected SlimeTreeGenerator treeGenBlue;
  protected SlimeTreeGenerator treeGenPurple;

  protected IBlockState air;

  protected TIntObjectHashMap<SlimeIslandData> islandData = new TIntObjectHashMap<SlimeIslandData>();

  public SlimeIslandGenerator() {
    air = Blocks.AIR.getDefaultState();

    IBlockState slimeGreen = TinkerWorld.slimeBlockCongealed.getDefaultState().withProperty(BlockSlime.TYPE, BlockSlime.SlimeType.GREEN);
    IBlockState slimeBlue = TinkerWorld.slimeBlockCongealed.getDefaultState().withProperty(BlockSlime.TYPE, BlockSlime.SlimeType.BLUE);
    IBlockState slimePurple = TinkerWorld.slimeBlockCongealed.getDefaultState().withProperty(BlockSlime.TYPE, BlockSlime.SlimeType.PURPLE);

    IBlockState leaves = TinkerWorld.slimeLeaves.getDefaultState();

    IBlockState slimeFLuidBlue = Blocks.WATER.getDefaultState();
    IBlockState slimeFLuidPurple = Blocks.WATER.getDefaultState();
    if(TinkerFluids.blueslime != null) {
      slimeFLuidBlue = TinkerFluids.blueslime.getBlock().getDefaultState();
      slimeFLuidPurple = slimeFLuidBlue; // just in case, will never be used with how the mod is set up
    }
    if(TinkerFluids.purpleSlime != null) {
      slimeFLuidPurple = TinkerFluids.purpleSlime.getBlock().getDefaultState();
    }

    lakeGenGreen = new SlimeLakeGenerator(slimeFLuidBlue, slimeGreen, slimeGreen, slimeBlue);
    lakeGenBlue = new SlimeLakeGenerator(slimeFLuidBlue, slimeBlue, slimeGreen, slimeBlue);
    lakeGenPurple = new SlimeLakeGenerator(slimeFLuidPurple, slimePurple, slimePurple);

    treeGenBlue = new SlimeTreeGenerator(5, 4, slimeGreen, leaves.withProperty(BlockSlimeGrass.FOLIAGE, BlockSlimeGrass.FoliageType.BLUE), TinkerWorld.slimeVineBlue3.getDefaultState());
    treeGenPurple = new SlimeTreeGenerator(5, 4, slimeGreen, leaves.withProperty(BlockSlimeGrass.FOLIAGE, BlockSlimeGrass.FoliageType.PURPLE), TinkerWorld.slimeVinePurple3.getDefaultState());

    plantGenBlue = new SlimePlantGenerator(BlockSlimeGrass.FoliageType.BLUE, false);
    plantGenPurple = new SlimePlantGenerator(BlockSlimeGrass.FoliageType.PURPLE, false);
  }

  public boolean isSlimeIslandAt(World world, BlockPos pos) {
    for(StructureBoundingBox data : getIslandData(world).islands) {
      if(data.isVecInside(pos)) {
        return true;
      }
    }
    return false;
  }

  protected String getDataName() {
    return "SlimeIslands";
  }

  protected SlimeIslandData getIslandData(World world) {
    int dimensionId = world.provider.getDimension();
    if(!islandData.containsKey(dimensionId)) {
      SlimeIslandData data = (SlimeIslandData)world.getPerWorldStorage().getOrLoadData(SlimeIslandData.class, getDataName());
      if(data == null) {
        data = new SlimeIslandData(getDataName());
        world.getPerWorldStorage().setData(getDataName(), data);
      }
      islandData.put(dimensionId, data);
    }

    return islandData.get(dimensionId);
  }

  protected boolean shouldGenerateInDimension(int id) {
    for(int dim : Config.slimeIslandBlacklist) {
      if(dim == id) {
        return false;
      }
    }
    return true;
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

    // should generate in this dimension?
    if(!shouldGenerateInDimension(world.provider.getDimension())) {
      return;
    }

    // do we generate in this chunk?
    if(random.nextInt(Config.slimeIslandsRate) > 0) {
      return;
    }

    // We do. determine parameters of the slime island!
    // default is a blue island
    BlockSlimeGrass.FoliageType grass = BlockSlimeGrass.FoliageType.BLUE;
    BlockSlimeDirt.DirtType dirt = BlockSlimeDirt.DirtType.BLUE;
    SlimeLakeGenerator lakeGen = lakeGenBlue;
    SlimePlantGenerator plantGen = plantGenPurple;
    SlimeTreeGenerator treeGen = treeGenPurple; // purple trees on blue/green islands
    IBlockState vine = TinkerWorld.slimeVineBlue1.getDefaultState();

    int rnr = random.nextInt(10);
    // purple island.. rare!
    if(rnr <= 1) {
      grass = BlockSlimeGrass.FoliageType.PURPLE;
      dirt = BlockSlimeDirt.DirtType.PURPLE;
      lakeGen = lakeGenPurple;
      treeGen = treeGenBlue; // blue trees on purple grass. yay
      plantGen = plantGenBlue;
      vine = TinkerWorld.slimeVinePurple1.getDefaultState();
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
    int y = world.getHeight(new BlockPos(x,0,z)).getY() + 50 + random.nextInt(50) + 11;

    generateIsland(random, world, x, z, y, dirtState, grassState, vine, lakeGen, treeGen, plantGen);
  }

  public void generateIsland(Random random, World world, int xPos, int zPos, int ySurfacePos, IBlockState dirt, IBlockState grass, IBlockState vine, SlimeLakeGenerator lakeGenerator, SlimeTreeGenerator treeGenerator, SlimePlantGenerator plantGen) {
    int xRange = 20 + random.nextInt(13);
    int zRange = 20 + random.nextInt(13);
    int yRange = 11 + random.nextInt(3);
    int height = yRange;
    //int top = height;

    int yBottom = ySurfacePos - yRange;

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
          if (ellipse.contains(x, z)) {
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
            if(world.getBlockState(pos) == dirt) {
              if(world.getBlockState(pos.add(-1, +1, 0)) != dirt ||
                 world.getBlockState(pos.add(+1, +1, 0)) != dirt ||
                 world.getBlockState(pos.add(0, +1, -1)) != dirt ||
                 world.getBlockState(pos.add(-1, +1, +1)) != dirt ||
                 random.nextInt(100) <= randomness) {
                world.setBlockState(pos, air, 2);
              }
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
              world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
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
            world.setBlockState(pos, grass, 2);
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

    // plants
    if(plantGen != null) {
      plantGen.generatePlants(random, world, start.up(height + 1), start.add(xRange, height-3, zRange), 128);
    }

    if(treeGenerator != null) {
      // trees
      for(int i = 0; i < 3; i++) {
        BlockPos pos = start.add(random.nextInt(xRange), height, random.nextInt(zRange));
        treeGenerator.generateTree(random, world, pos);
      }
    }

    if(vine != null) {
      for(int i = 0; i < 30; i++) {
        BlockPos pos = start.add(-1 + random.nextInt(xRange + 2), 0, -1 + random.nextInt(zRange + 2));
        tryPlacingVine(random, world, pos, height, vine);
      }
    }

    // save it
    SlimeIslandData data = getIslandData(world);
    data.islands.add(new StructureBoundingBox(start.getX(), start.getY(), start.getZ(),
                                                              start.getX()+xRange, start.getY()+yRange, start.getZ()+yRange));
    data.markDirty();
  }

  // takse the position and goes up until it finds a block. if it doesn't find a block directly above it'll check if it has side blocks on the way up to attach to.
  public void tryPlacingVine(Random random, World world, BlockPos below, int limit, IBlockState vine) {
    BlockPos pos = below;
    BlockPos candidate = null;
    // check straight up first
    for(int i = 0; i < limit; i++) {
      // check around for a possible block
      if(vine.getBlock().canPlaceBlockOnSide(world, pos, EnumFacing.NORTH)
          || vine.getBlock().canPlaceBlockOnSide(world, pos, EnumFacing.EAST)
          || vine.getBlock().canPlaceBlockOnSide(world, pos, EnumFacing.SOUTH)
          || vine.getBlock().canPlaceBlockOnSide(world, pos, EnumFacing.WEST)) {
        if(candidate == null || random.nextInt(10) == 0) {
          candidate = pos;
        }
      }

      pos = pos.up();
    }

    if(candidate != null) {
      // place the vine
      world.setBlockState(candidate, vine.getBlock().onBlockPlaced(world, candidate, EnumFacing.UP, 0, 0, 0, 0, null), 2);

      // and let it grow, let it grow, let it groooooow!
      pos = candidate;
      for(int size = random.nextInt(8); size >= 0; size++) {
        if(!(world.getBlockState(pos).getBlock() instanceof BlockSlimeVine)) {
          break;
        }
        ((BlockSlimeVine)world.getBlockState(pos).getBlock()).grow(world, random, pos, world.getBlockState(pos));
        pos = pos.down();
      }
    }
  }


}
