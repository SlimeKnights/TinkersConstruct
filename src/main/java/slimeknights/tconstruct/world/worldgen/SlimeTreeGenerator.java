package slimeknights.tconstruct.world.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Random;

public class SlimeTreeGenerator implements IWorldGenerator {

  public final int minTreeHeight;
  public final int treeHeightRange;
  public final IBlockState log;
  public final IBlockState leaves;
  public final IBlockState vine;
  public final boolean seekHeight;

  public SlimeTreeGenerator(int treeHeight, int treeRange, IBlockState log, IBlockState leaves, IBlockState vine, boolean seekHeight) {
    this.minTreeHeight = treeHeight;
    this.treeHeightRange = treeRange;
    this.log = log;
    this.leaves = leaves;
    this.vine = vine;
    this.seekHeight = seekHeight;
  }

  public SlimeTreeGenerator(int treeHeight, int treeRange, IBlockState log, IBlockState leaves, IBlockState vine) {
    this(treeHeight, treeRange, log, leaves, vine, true);
  }

  @Override
  public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {

  }

  public void generateTree(Random random, World world, BlockPos pos) {
    int height = random.nextInt(this.treeHeightRange) + this.minTreeHeight;
    boolean flag = true;
    if(seekHeight) {
      pos = findGround(world, pos);
      if(pos.getY() < 0) {
        return;
      }
    }

    int yPos = pos.getY();

    if(yPos >= 1 && yPos + height + 1 <= 256) {
      IBlockState state = world.getBlockState(pos.down());
      Block soil = state.getBlock();
      boolean isSoil = (soil != Blocks.AIR && soil.canSustainPlant(state, world, pos.down(), EnumFacing.UP, TinkerWorld.slimeSapling));

      if(isSoil) {
        soil.onPlantGrow(state, world, pos.down(), pos);
        placeTrunk(world, pos, height);
        placeCanopy(world, random, pos, height);
      }
    }
  }

  BlockPos findGround(World world, BlockPos pos) {
    do {
      IBlockState state = world.getBlockState(pos);
      Block heightID = state.getBlock();
      IBlockState up = world.getBlockState(pos.up());
      if((heightID == TinkerWorld.slimeDirt || heightID == TinkerWorld.slimeGrass) && !up.getBlock().isOpaqueCube(up)) {
        return pos.up();
      }
      pos = pos.down();
    } while(pos.getY() > 0);

    return pos;
  }

  void placeCanopy(World world, Random random, BlockPos pos, int height) {
    pos = pos.up(height);
    for(int i = 0; i < 4; i++) {
      placeDiamondLayer(world, pos.down(i), i + 1);
    }

    IBlockState air = Blocks.AIR.getDefaultState();

    pos = pos.down(3);
    this.setBlockAndMetadata(world, pos.add(+4, 0, 0), air);
    this.setBlockAndMetadata(world, pos.add(-4, 0, 0), air);
    this.setBlockAndMetadata(world, pos.add(0, 0, +4), air);
    this.setBlockAndMetadata(world, pos.add(0, 0, -4), air);
    if(vine != null) {
      this.setBlockAndMetadata(world, pos.add(+1, 0, +1), air);
      this.setBlockAndMetadata(world, pos.add(+1, 0, -1), air);
      this.setBlockAndMetadata(world, pos.add(-1, 0, +1), air);
      this.setBlockAndMetadata(world, pos.add(-1, 0, -1), air);
    }

    //Drippers
    // stuck with only one block down because of leaf decay distance
    pos = pos.down();
    this.setBlockAndMetadata(world, pos.add(+3, 0, 0), leaves);
    this.setBlockAndMetadata(world, pos.add(-3, 0, 0), leaves);
    this.setBlockAndMetadata(world, pos.add(0, 0, -3), leaves);
    this.setBlockAndMetadata(world, pos.add(0, 0, +3), leaves);
    if (vine == null) {
      this.setBlockAndMetadata(world, pos.add(+1, 0, +1), leaves);
      this.setBlockAndMetadata(world, pos.add(+1, 0, -1), leaves);
      this.setBlockAndMetadata(world, pos.add(-1, 0, +1), leaves);
      this.setBlockAndMetadata(world, pos.add(-1, 0, -1), leaves);
    }

    // vines, woo
    if(vine != null) {
      pos = pos.down();
      this.setBlockAndMetadata(world, pos.add(+3, 0, 0), getRandomizedVine(random));
      this.setBlockAndMetadata(world, pos.add(-3, 0, 0), getRandomizedVine(random));
      this.setBlockAndMetadata(world, pos.add(0, 0, -3), getRandomizedVine(random));
      this.setBlockAndMetadata(world, pos.add(0, 0, +3), getRandomizedVine(random));
      IBlockState randomVine = getRandomizedVine(random);
      this.setBlockAndMetadata(world, pos.add(+2, 1, +2), randomVine);
      this.setBlockAndMetadata(world, pos.add(+2, 0, +2), randomVine);
      randomVine = getRandomizedVine(random);
      this.setBlockAndMetadata(world, pos.add(+2, 1, -2), randomVine);
      this.setBlockAndMetadata(world, pos.add(+2, 0, -2), randomVine);
      randomVine = getRandomizedVine(random);
      this.setBlockAndMetadata(world, pos.add(-2, 1, +2), randomVine);
      this.setBlockAndMetadata(world, pos.add(-2, 0, +2), randomVine);
      randomVine = getRandomizedVine(random);
      this.setBlockAndMetadata(world, pos.add(-2, 1, -2), randomVine);
      this.setBlockAndMetadata(world, pos.add(-2, 0, -2), randomVine);
    }
  }

  protected IBlockState getRandomizedVine(Random random) {
    IBlockState state = vine;
    PropertyBool[] sides = new PropertyBool[]{BlockVine.NORTH, BlockVine.EAST, BlockVine.SOUTH, BlockVine.WEST};
    for(PropertyBool side : sides) {
      state = state.withProperty(side, false);
    }
    for(int i = random.nextInt(3) + 1; i > 0; i--) {
      state = state.withProperty(sides[random.nextInt(sides.length)], true);
    }

    return state;
  }

  protected void placeDiamondLayer(World world, BlockPos pos, int range) {
    for(int x = -range; x <= range; x++) {
      for(int z = -range; z <= range; z++) {
        if(Math.abs(x) + Math.abs(z) <= range) {
          this.setBlockAndMetadata(world, pos.add(x, 0, z), leaves);
        }
      }
    }
  }

  protected void placeTrunk(World world, BlockPos pos, int height) {
    while(height > 0) {
      IBlockState state = world.getBlockState(pos);
      Block block = state.getBlock();
      if(block.isAir(state, world, pos) || block.isReplaceable(world, pos) || block.isLeaves(state, world, pos)) {
        world.setBlockState(pos, log);
      }

      pos = pos.up();
      height--;
    }
  }

  protected void setBlockAndMetadata(World world, BlockPos pos, IBlockState stateNew) {
    IBlockState state = world.getBlockState(pos);
    Block block = state.getBlock();
    if(block.isAir(state, world, pos) || block.canPlaceBlockAt(world, pos) || world.getBlockState(pos) == leaves) {
      world.setBlockState(pos, stateNew, 2);
    }
  }
}
