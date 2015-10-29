package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.world.block.BlockSlimeGrass.FoliageType;
import slimeknights.tconstruct.world.client.SlimeColorizer;

public class BlockSlimeVine extends BlockVine {
  public static PropertyEnum FOLIAGE = BlockSlimeGrass.FOLIAGE;
  public static PropertyInteger STAGE = PropertyInteger.create("stage", 0, 2); // 0 = full, 1 = middle, 2 = end

  public static final int FULL_STAGE = 0;
  public static final int MID_STAGE = 1;
  public static final int END_STAGE = 2;

  public BlockSlimeVine() {
    this.setCreativeTab(TinkerRegistry.tabWorld);
    this.setDefaultState(this.blockState.getBaseState()
                                        .withProperty(UP, false)
                                        .withProperty(NORTH, false)
                                        .withProperty(EAST, false)
                                        .withProperty(SOUTH, false)
                                        .withProperty(WEST, false)
                                        .withProperty(STAGE, 0));
  }

  @Override
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
    for(FoliageType type : FoliageType.values()) {
      list.add(new ItemStack(this, 1, getMetaFromState(this.getDefaultState().withProperty(BlockSlimeGrass.FOLIAGE, type))));
    }
  }

  @Override
  protected BlockState createBlockState() {
    return new BlockState(this, UP, NORTH, EAST, SOUTH, WEST, FOLIAGE, STAGE);
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    FoliageType foliage = FoliageType.getValFromMeta(meta & 3);
    int stage = meta >> 2;
    if(stage > 2) stage = 0;
    return getDefaultState().withProperty(FOLIAGE, foliage).withProperty(STAGE, stage);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return ((FoliageType)state.getValue(FOLIAGE)).getMeta() | ((Integer)state.getValue(STAGE)) << 2;
  }

  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    IBlockState base = state;
    IBlockState above = worldIn.getBlockState(pos.up());

    boolean n,e,s,w;
    n = canAttachTo(worldIn, pos.north());
    e = canAttachTo(worldIn, pos.east());
    s = canAttachTo(worldIn, pos.south());
    w = canAttachTo(worldIn, pos.west());

    // got another vine above?
    if(!(n || e || s || w) && above.getBlock() == this) {
      // base state on aboves state
      base = above.getBlock().getActualState(above, worldIn, pos.up());
    }
    // set state on surrounding blocks
    else {
      base = base.withProperty(NORTH, n);
      base = base.withProperty(EAST, e);
      base = base.withProperty(SOUTH, s);
      base = base.withProperty(WEST, w);
    }

    // check if block above
    base = base.withProperty(UP, canAttachTo(worldIn, pos.up()));

    // set correct foliage and stage
    base = base.withProperty(FOLIAGE, state.getValue(FOLIAGE));
    base = base.withProperty(STAGE, state.getValue(STAGE));


    return base;
  }

  private Boolean canAttachTo(IBlockAccess world, BlockPos pos) {
    Block block = world.getBlockState(pos).getBlock();

    return block.isFullCube() && block.getMaterial().blocksMovement();
  }

  @Override
  public boolean isReplaceable(World worldIn, BlockPos pos) {
    return true;
  }

  /**
   * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
   * IBlockstate
   */
  public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
  {
    IBlockState iblockstate = this.getStateFromMeta(meta).withProperty(UP, Boolean.valueOf(false)).withProperty(NORTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false));
    return facing.getAxis().isHorizontal() ? iblockstate.withProperty(getPropertyFor(facing.getOpposite()), Boolean.valueOf(true)) : iblockstate;
  }

  @Override
  public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
    if(worldIn.isRemote) {
      return;
    }

    // are we anchored to a block?
    if(!canAttachTo(worldIn, pos.north()) && !canAttachTo(worldIn, pos.east()) && !canAttachTo(worldIn, pos.south()) && !canAttachTo(worldIn, pos.west())) {
      // are we held up from above?
      if(worldIn.getBlockState(pos.up()).getBlock() != this) {
        this.dropBlockAsItem(worldIn, pos, state, 0);
        worldIn.setBlockToAir(pos);
      }
    }

    // notify bottom block to update its state since ours might have changed as well
    BlockPos down = pos.down();
    while(worldIn.getBlockState(down).getBlock() == this) {
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
    if((Integer)state.getValue(STAGE) == END_STAGE) {
      return;
    }

    // we only grow down
    BlockPos below = pos.down();
    if(worldIn.isAirBlock(below)) {
      // free floating position?
      if(!canAttachTo(worldIn, below.north()) && !canAttachTo(worldIn, below.east()) && !canAttachTo(worldIn, below.south()) && !canAttachTo(worldIn, below.west())) {
        // at most 3 middle parts
        int i = 0;
        for(; worldIn.getBlockState(below.up(i)).getBlock() == this; i++) {
          if((Integer) worldIn.getBlockState(below.up(i)).getValue(STAGE) != MID_STAGE)
            break;
        }

        if(i > 2 || rand.nextInt(2) == 0) {
          state = state.withProperty(STAGE, (Integer) state.getValue(STAGE) + 1);
        }
      }

      worldIn.setBlockState(below, state);
    }
  }

  @Override
  public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
    float f1 = 1.0F;
    float f2 = 1.0F;
    float f3 = 1.0F;
    float f4 = 0.0F;
    float f5 = 0.0F;
    float f6 = 0.0F;
    boolean flag = false;

    if (this.canAttachTo(worldIn, pos.west()))
    {
      f4 = Math.max(f4, 0.0625F);
      f1 = 0.0F;
      f2 = 0.0F;
      f5 = 1.0F;
      f3 = 0.0F;
      f6 = 1.0F;
      flag = true;
    }

    if (this.canAttachTo(worldIn, pos.east()))
    {
      f1 = Math.min(f1, 0.9375F);
      f4 = 1.0F;
      f2 = 0.0F;
      f5 = 1.0F;
      f3 = 0.0F;
      f6 = 1.0F;
      flag = true;
    }

    if (this.canAttachTo(worldIn, pos.north()))
    {
      f6 = Math.max(f6, 0.0625F);
      f3 = 0.0F;
      f1 = 0.0F;
      f4 = 1.0F;
      f2 = 0.0F;
      f5 = 1.0F;
      flag = true;
    }

    if (this.canAttachTo(worldIn, pos.south()))
    {
      f3 = Math.min(f3, 0.9375F);
      f6 = 1.0F;
      f1 = 0.0F;
      f4 = 1.0F;
      f2 = 0.0F;
      f5 = 1.0F;
      flag = true;
    }

    if (!flag && this.canAttachTo(worldIn, pos.up()))
    {
      f2 = Math.min(f2, 0.9375F);
      f5 = 1.0F;
      f1 = 0.0F;
      f4 = 1.0F;
      f3 = 0.0F;
      f6 = 1.0F;
    }

    this.setBlockBounds(f1, f2, f3, f4, f5, f6);
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
    FoliageType foliageType = (FoliageType) state.getValue(FOLIAGE);
    return SlimeColorizer.getColorStaticBGR(foliageType);
  }

  // Used for the block in world
  @SideOnly(Side.CLIENT)
  @Override
  public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
    IBlockState state = worldIn.getBlockState(pos);
    if(state.getBlock() != this) return getBlockColor();

    FoliageType foliageType = (FoliageType) state.getValue(FOLIAGE);
    return SlimeColorizer.getColorForPos(pos, foliageType);
  }
}
