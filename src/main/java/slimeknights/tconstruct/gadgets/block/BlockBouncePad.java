package slimeknights.tconstruct.gadgets.block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.gadgets.tileentity.TileBouncePad;
import slimeknights.tconstruct.shared.block.BlockSlime;
import slimeknights.tconstruct.shared.block.BlockSlime.SlimeType;

public class BlockBouncePad extends EnumBlock<SlimeType> {
  public static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.125, 0.0625, 0.125, 0.875, 0.625, 0.875);
  
  public static final PropertyEnum<BlockSlimeChannel.ChannelDirection> DIRECTION = PropertyEnum.create("direction", BlockSlimeChannel.ChannelDirection.class);
  public static final PropertyBool POWERED = PropertyBool.create("powered");
  public static final PropertyEnum<SlimeType> TYPE = BlockSlime.TYPE;
  
  
  public BlockBouncePad() {
    super(Material.CLOTH, TYPE, SlimeType.class);
    this.setHardness(0);
    this.setSoundType(SoundType.SLIME);
    
    this.setDefaultState(this.blockState.getBaseState()
        .withProperty(DIRECTION, BlockSlimeChannel.ChannelDirection.NORTH)
        .withProperty(TYPE, SlimeType.GREEN)
        .withProperty(POWERED, false));
  }
  
  @Override
  public boolean hasTileEntity(IBlockState state) {
    return true;
  }
  
  @Override
  @Nullable
  public TileEntity createTileEntity(World world, IBlockState state) {
    return new TileBouncePad();
  }
  
  /**
   * Safe way to grab TE data above since we don't want to call getActualState inside itself for connections
   * (it would go back and forth and back and forth between the two blocks)
   */
  private IBlockState addDataFromTE(IBlockState state, IBlockAccess source, BlockPos pos) {
    TileEntity te = source.getTileEntity(pos);
    if(te instanceof TileBouncePad) {
      TileBouncePad pad = (TileBouncePad) te;
      return state.withProperty(DIRECTION, pad.getDirection());
    }
    return state;
  }
  
  @Nonnull
  @Override
  public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess source, BlockPos pos) {
    return addDataFromTE(state, source, pos);
  }
  
  @Nonnull
  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, DIRECTION, POWERED, TYPE);
  }
  
  @Override
  public IBlockState getStateFromMeta(int meta) {
    return this.getDefaultState().withProperty(TYPE, SlimeType.fromMeta(meta & 7))
        .withProperty(POWERED, (meta & 8) > 0);
  }
  
  @Override
  public int getMetaFromState(IBlockState state) {
    int meta = state.getValue(TYPE).getMeta();
    if(state.getValue(POWERED)) {
      meta |= 8;
    }
    return meta;
  }
  
  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    return BOUNDING_BOX;
  }
  
  @Override
  @Nullable
  public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
    return null;
  }
  
  @Override
  public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
    if (!state.getValue(POWERED)) {
      double moveX = 0;
      double moveZ = 0;

      double speed = 0.25;

      final BlockSlimeChannel.ChannelDirection dir = state.getValue(DIRECTION);
      final byte[] vec = dir.toVector();
      moveX += vec[0] * speed;
      moveZ += vec[1] * speed;

      if (entityIn instanceof EntityItem) {
        entityIn.posY += 1;
      }
      entityIn.fallDistance = 0.0F;
      entityIn.addVelocity(moveX, speed * 2, moveZ);
      worldIn.playSound(null, pos, this.blockSoundType.getStepSound(), entityIn.getSoundCategory(), this.blockSoundType.getVolume() / 2, this.blockSoundType.getPitch() * 0.65f);
    }
  }
  
  @Override
  public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
    // we temporarily store the data in the blockstate until the TE is created
    return this.getDefaultState().withProperty(TYPE, SlimeType.fromMeta(meta))
                                 .withProperty(DIRECTION, BlockSlimeChannel.getPlacement(EnumFacing.DOWN, hitX, hitY, hitZ, placer));
  }
  
  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    TileEntity te = worldIn.getTileEntity(pos);
    // pull the data we stored earlier into the Tile Entity
    if(te instanceof TileBouncePad) {
      TileBouncePad pad = (TileBouncePad)te;
      pad.setDirection(state.getValue(DIRECTION));
    }
  }
  
  /* Powering */
  @Override
  public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
    this.updateState(worldIn, pos, state);
  }

  /**
   * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
   * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
   * block, etc.
   */
  @Override
  public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
    this.updateState(worldIn, pos, state);
  }

  public void updateState(World world, BlockPos pos, IBlockState state) {
    boolean powered = world.isBlockPowered(pos);

    // don't do any changes if the block is the same
    if(powered != state.getValue(POWERED)) {
      world.setBlockState(pos, state.withProperty(POWERED, powered));
    }
  }
  
  @Override
  public boolean isFullCube(IBlockState state) {
    return false;
  }
  
  @Override
  public boolean isOpaqueCube(IBlockState state) {
    return false;
  }
}
