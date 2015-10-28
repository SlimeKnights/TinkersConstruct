package slimeknights.tconstruct.world.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

import slimeknights.tconstruct.world.TinkerWorld;

public class SlimeTreeGenerator implements IWorldGenerator {

  public final int minTreeHeight;
  public final int treeHeightRange;
  public final IBlockState log;
  public final IBlockState leaves;
  public final boolean seekHeight;

  public SlimeTreeGenerator(int treeHeight, int treeRange, IBlockState log, IBlockState leaves, boolean seekHeight) {
    this.minTreeHeight = treeHeight;
    this.treeHeightRange = treeRange;
    this.log = log;
    this.leaves = leaves;
    this.seekHeight = seekHeight;
  }

  public SlimeTreeGenerator(int treeHeight, int treeRange, IBlockState log, IBlockState leaves)
  {
    this(treeHeight, treeRange, log, leaves, true);
  }


  @Override
  public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

  }

  public void generateTree(Random random, World world, BlockPos pos)
  {
    int height = random.nextInt(this.treeHeightRange) + this.minTreeHeight;
    boolean flag = true;
    if (seekHeight)
    {
      pos = findGround(world, pos);
      if (pos.getY() < 0)
        return;
    }

    int yPos = pos.getY();

    if (yPos >= 1 && yPos + height + 1 <= 256)
    {
      Block soil = world.getBlockState(pos.down()).getBlock();
      boolean isSoil = (soil != null && soil.canSustainPlant(world, pos.down(), EnumFacing.UP, TinkerWorld.slimeSapling));

      if (isSoil)
      {
        soil.onPlantGrow(world, pos.down(), pos);
        placeCanopy(world, random, pos, height);
        placeTrunk(world, pos, height);
      }
    }
  }

  BlockPos findGround (World world, BlockPos pos)
  {
    do
    {
      Block heightID = world.getBlockState(pos).getBlock();
      if ((heightID == TinkerWorld.slimeDirt || heightID == TinkerWorld.slimeGrass) && !world.getBlockState(pos.up()).getBlock().isOpaqueCube())
      {
        return pos.up();
      }
      pos = pos.down();
    } while (pos.getY() > 0);

    return pos;
  }

  void placeCanopy (World world, Random random, BlockPos pos, int height)
  {
    pos = pos.up(height);
    for (int i = 0; i < 4; i++)
    {
      placeDiamondLayer(world, pos.down(i), i + 1);
    }

    IBlockState air = Blocks.air.getDefaultState();

    pos = pos.down();
    this.setBlockAndMetadata(world, pos.add(+4, 0,  0), air);
    this.setBlockAndMetadata(world, pos.add(-4, 0,  0), air);
    this.setBlockAndMetadata(world, pos.add( 0, 0, +4), air);
    this.setBlockAndMetadata(world, pos.add( 0, 0, -4), air);
    this.setBlockAndMetadata(world, pos.add(+1, 0, +1), air);
    this.setBlockAndMetadata(world, pos.add(+1, 0, -1), air);
    this.setBlockAndMetadata(world, pos.add(-1, 0, +1), air);
    this.setBlockAndMetadata(world, pos.add(-1, 0, -1), air);

    //Drippers
    pos = pos.down();
    this.setBlockAndMetadata(world, pos.add(+3, 0,  0), leaves);
    this.setBlockAndMetadata(world, pos.add(-3, 0,  0), leaves);
    this.setBlockAndMetadata(world, pos.add( 0, 0, -3), leaves);
    this.setBlockAndMetadata(world, pos.add( 0, 0, +3), leaves);
    this.setBlockAndMetadata(world, pos.add(+2, 0, +2), leaves);
    this.setBlockAndMetadata(world, pos.add(+2, 0, -2), leaves);
    this.setBlockAndMetadata(world, pos.add(-2, 0, +2), leaves);
    this.setBlockAndMetadata(world, pos.add(-2, 0, -2), leaves);

    pos = pos.down();
    this.setBlockAndMetadata(world, pos.add(+3, 0,  0), leaves);
    this.setBlockAndMetadata(world, pos.add(-3, 0,  0), leaves);
    this.setBlockAndMetadata(world, pos.add( 0, 0, -3), leaves);
    this.setBlockAndMetadata(world, pos.add( 0, 0, +3), leaves);
    this.setBlockAndMetadata(world, pos.add(+2, 0, +2), leaves);
    this.setBlockAndMetadata(world, pos.add(+2, 0, -2), leaves);
    this.setBlockAndMetadata(world, pos.add(-2, 0, +2), leaves);
    this.setBlockAndMetadata(world, pos.add(-2, 0, -2), leaves);
  }

  void placeDiamondLayer (World world, BlockPos pos, int range)
  {
    for (int x = -range; x <= range; x++)
    {
      for (int z = -range; z <= range; z++)
      {
        if (Math.abs(x) + Math.abs(z) <= range)
        {
          this.setBlockAndMetadata(world, pos.add(x,0,z), leaves);
        }
      }
    }
  }

  void placeTrunk (World world, BlockPos pos, int height)
  {
    while(height >= 0) {
      Block block = world.getBlockState(pos).getBlock();
      if (block == Blocks.air || block.isReplaceable(world, pos) || block.isLeaves(world, pos))
      {
        this.setBlockAndMetadata(world, pos, log);
      }

      pos = pos.up();
      height--;
    }
  }

  protected void setBlockAndMetadata (World world, BlockPos pos, IBlockState state)
  {
    Block block = world.getBlockState(pos).getBlock();
    if (block == Blocks.air || block.canPlaceBlockAt(world, pos) || world.getBlockState(pos) == leaves)
    {
      world.setBlockState(pos, state, 2);
    }
  }
}
