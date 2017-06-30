package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.world.block.BlockSlimeGrass.FoliageType;

public class BlockSlimeVine extends BlockVine {

  protected final FoliageType foliage;
  protected final BlockSlimeVine nextStage;

  public BlockSlimeVine(FoliageType foliage, BlockSlimeVine nextStage) {
    this.setCreativeTab(TinkerRegistry.tabWorld);
    this.setSoundType(SoundType.PLANT);

    this.foliage = foliage;
    this.nextStage = nextStage;
  }

  private Boolean canAttachTo(IBlockAccess world, BlockPos pos) {
    IBlockState state = world.getBlockState(pos);
    Block block = state.getBlock();

    return block.isFullCube(state) && block.getMaterial(state).blocksMovement();
  }

  /**
   * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
   * IBlockstate
   */
  @Override
  public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
    IBlockState iblockstate = this.getDefaultState();
    iblockstate = iblockstate.withProperty(NORTH, canAttachTo(world, pos.north()));
    iblockstate = iblockstate.withProperty(EAST, canAttachTo(world, pos.east()));
    iblockstate = iblockstate.withProperty(SOUTH, canAttachTo(world, pos.south()));
    iblockstate = iblockstate.withProperty(WEST, canAttachTo(world, pos.west()));
    return iblockstate;
  }

  @Override
  public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
    if(worldIn.isRemote) {
      return;
    }

    // are we anchored to a block?
    if(!canAttachTo(worldIn, pos.north()) && !canAttachTo(worldIn, pos.east()) && !canAttachTo(worldIn, pos.south()) && !canAttachTo(worldIn, pos.west())) {
      // are we held up from above?
      if(!(worldIn.getBlockState(pos.up()).getBlock() instanceof BlockVine)) {
        this.dropBlockAsItem(worldIn, pos, state, 0);
        worldIn.setBlockToAir(pos);
      }
    }

    // notify bottom block to update its state since ours might have changed as well
    BlockPos down = pos.down();
    IBlockState state2;
    while((state2 = worldIn.getBlockState(down)).getBlock() instanceof BlockVine) {
      worldIn.notifyBlockUpdate(down, state2, state2, 3);
      down = down.down();
    }
  }

  @Override
  public void updateTick(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rand) {
    if(!worldIn.isRemote) {
      if(rand.nextInt(4) == 0) {
        grow(worldIn, rand, pos, state);
      }
    }
  }

  public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
    // end parts don't grow
    if(nextStage == null) {
      return;
    }

    // we only grow down
    BlockPos below = pos.down();
    if(worldIn.isAirBlock(below)) {
      // free floating position?
      if(!canAttachTo(worldIn, below.north()) && !canAttachTo(worldIn, below.east()) && !canAttachTo(worldIn, below.south()) && !canAttachTo(worldIn, below.west())) {
        // at most 3 middle parts
        int i = 0;
        while(worldIn.getBlockState(pos.up(i)).getBlock() == this) {
          i++;
        }

        if(i > 2 || rand.nextInt(2) == 0) {
          state = nextStage.getDefaultState()
                           .withProperty(NORTH, state.getValue(NORTH))
                           .withProperty(EAST, state.getValue(EAST))
                           .withProperty(SOUTH, state.getValue(SOUTH))
                           .withProperty(WEST, state.getValue(WEST));
        }
      }

      worldIn.setBlockState(below, state);
    }
  }
}
