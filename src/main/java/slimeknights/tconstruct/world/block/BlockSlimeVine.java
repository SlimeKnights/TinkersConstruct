package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.world.block.BlockSlimeGrass.FoliageType;
import slimeknights.tconstruct.world.client.SlimeColorizer;

public class BlockSlimeVine extends BlockVine {
  protected final FoliageType foliage;
  protected final BlockSlimeVine nextStage;

  public BlockSlimeVine(FoliageType foliage, BlockSlimeVine nextStage) {
    this.setCreativeTab(TinkerRegistry.tabWorld);
    this.setStepSound(soundTypeGrass);

    this.foliage = foliage;
    this.nextStage = nextStage;
  }
/*
  @Override
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
    if(this == TinkerWorld.slimeVinePurple1 || this == TinkerWorld.slimeVineBlue1) {
      list.add(new ItemStack(this, 1, 0));
    }
  }
*/
  private Boolean canAttachTo(IBlockAccess world, BlockPos pos) {
    Block block = world.getBlockState(pos).getBlock();

    return block.isFullCube() && block.getMaterial().blocksMovement();
  }

  /**
   * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
   * IBlockstate
   */
  public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
  {
    IBlockState iblockstate = this.getDefaultState();
    iblockstate = iblockstate.withProperty(NORTH, canAttachTo(worldIn, pos.north()));
    iblockstate = iblockstate.withProperty(EAST, canAttachTo(worldIn, pos.east()));
    iblockstate = iblockstate.withProperty(SOUTH, canAttachTo(worldIn, pos.south()));
    iblockstate = iblockstate.withProperty(WEST, canAttachTo(worldIn, pos.west()));
    return iblockstate;
  }

  @Override
  public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
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
    while(worldIn.getBlockState(down).getBlock() instanceof BlockVine) {
      worldIn.markBlockForUpdate(down);
      down = down.down();
    }
  }

  @Override
  public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
    if(!worldIn.isRemote) {
      if (rand.nextInt(4) == 0) {
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

  @Override
  @SideOnly(Side.CLIENT)
  public int getBlockColor ()
  {
    return 0xffffff;
  }

  // Used for the item
  @SideOnly(Side.CLIENT)
  @Override
  public int getRenderColor(IBlockState state) {
    return SlimeColorizer.getColorStatic(foliage);
  }

  // Used for the block in world
  @SideOnly(Side.CLIENT)
  @Override
  public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
    IBlockState state = worldIn.getBlockState(pos);
    if(state.getBlock() != this) return getBlockColor();

    return SlimeColorizer.getColorForPos(pos, foliage);
  }
}
