package slimeknights.tconstruct.gadgets.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockLever.EnumOrientation;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.gadgets.tileentity.TileDryingRack;
import slimeknights.tconstruct.gadgets.tileentity.TileItemRack;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.shared.block.BlockTable;

import javax.annotation.Nonnull;

public class BlockRack extends BlockTable {

  // pull the facing enums from the lever, since the standard facing does not have quite enough, but the lever's facing is perfect
  public static final PropertyEnum<EnumOrientation> ORIENTATION = PropertyEnum.create("facing", EnumOrientation.class);
  public static final PropertyBool DRYING = PropertyBool.create("drying");

  public BlockRack() {
    super(Material.WOOD);
    this.setSoundType(SoundType.WOOD);
    this.setCreativeTab(TinkerRegistry.tabGadgets);
    this.setHardness(2.0F);

    this.setDefaultState(getBlockState().getBaseState()
                                        .withProperty(ORIENTATION, EnumOrientation.NORTH)
                                        .withProperty(DRYING, false)
    );
  }

  @Override
  public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
    list.add(createItemstack(this, 0, Blocks.WOODEN_SLAB, 0));
    list.add(createItemstack(this, 1, Blocks.WOODEN_SLAB, 0));
  }

  @Override
  public int damageDropped(IBlockState state) {
    if(state.getValue(DRYING)) {
      return 1;
    }
    else {
      return 0;
    }
  }

  @Override
  public boolean isSideSolid(@Nonnull IBlockState base_state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    // the center ones are considered solid at the top
    return side == EnumFacing.UP && base_state.getValue(ORIENTATION).getFacing() == EnumFacing.UP;
  }

  /* Inventory stuffs */
  @Nonnull
  @Override
  public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
    if(getStateFromMeta(meta).getValue(DRYING)) {
      return new TileDryingRack();
    }
    else {
      return new TileItemRack();
    }
  }

  @Override
  public boolean openGui(EntityPlayer player, World world, BlockPos pos) {
    return false;
  }

  /* Activation */
  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float clickX, float clickY, float clickZ) {
    if(!world.isRemote) {
      TileItemRack tileItemRack = ((TileItemRack) world.getTileEntity(pos));
      if(tileItemRack != null) {
        tileItemRack.interact(player);
        world.scheduleUpdate(pos, this, 0);
      }
    }

    return true;
  }

  /* Block state */
  @Nonnull
  @Override
  protected BlockStateContainer createBlockState() {
    // FACING is used to orientate the item, while ORIENTATION is the location of the rack. This mainly affects centered racks
    return new ExtendedBlockState(this, new IProperty[]{ORIENTATION, DRYING}, new IUnlistedProperty[]{TEXTURE, INVENTORY, FACING});
  }

  /**
   * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
   * IBlockstate
   */
  @Override
  public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
    IBlockState state = this.getDefaultState();

    // playing a drying rack instead of an item rack
    if((meta & 1) == 1) {
      state = state.withProperty(DRYING, true);
    }

    IBlockState placedOn = world.getBlockState(pos.offset(facing.getOpposite()));

    // if placing on another item rack or drying rack, use the same orientation to make building easier
    // this is for the sake of making elevated, standalone rows of racks or easier building up on walls
    if(placedOn.getBlock() == TinkerGadgets.rack) {
      return state.withProperty(ORIENTATION, placedOn.getValue(ORIENTATION));

      // otherwise place it based on side/player facing
    }
    else {
      return state.withProperty(ORIENTATION, EnumOrientation.forFacings(facing.getOpposite(), placer.getHorizontalFacing()));
    }
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer,
                              ItemStack stack) {
    super.onBlockPlacedBy(world, pos, state, placer, stack);

    TileEntity te = world.getTileEntity(pos);
    if(te != null && te instanceof TileItemRack) {
      TileItemRack rack = (TileItemRack) te;

      EnumOrientation orientation = state.getValue(ORIENTATION);
      switch(orientation) {
        // change the facing value if the state is one of the sides
        case NORTH:
        case EAST:
        case SOUTH:
        case WEST:
          rack.setFacing(orientation.getFacing().getOpposite());
          break;

        // if not on a side, make sure the facing is allowed for the state of rack, as the state will be the same as the rack its placed on
        case UP_X:
        case DOWN_X:
          if(placer.getHorizontalFacing().getAxis() != Axis.X) {
            rack.setFacing(placer.getHorizontalFacing().rotateY());
          }
          break;
        case UP_Z:
        case DOWN_Z:
          if(placer.getHorizontalFacing().getAxis() != Axis.Z) {
            rack.setFacing(placer.getHorizontalFacing().rotateY());
          }
          break;
      }
    }

  }

  /**
   * Convert the given metadata into a BlockState for this Block
   */
  @Nonnull
  @Override
  public IBlockState getStateFromMeta(int meta) {
    return this.getDefaultState()
               .withProperty(ORIENTATION, EnumOrientation.byMetadata(meta >> 1))
               .withProperty(DRYING, (meta & 1) == 1);
  }

  /**
   * Convert the BlockState into the correct metadata value
   */
  @Override
  public int getMetaFromState(IBlockState state) {
    int i = 0;
    i = i | state.getValue(ORIENTATION).getMetadata() << 1;

    if(state.getValue(DRYING)) {
      i |= 1;
    }

    return i;
  }

  /**
   * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
   * blockstate.
   */
  @Nonnull
  @Override
  public IBlockState withRotation(@Nonnull IBlockState state, Rotation rot) {
    switch(rot) {
      case CLOCKWISE_180:
        switch(state.getValue(ORIENTATION)) {
          case EAST:
            return state.withProperty(ORIENTATION, EnumOrientation.WEST);
          case WEST:
            return state.withProperty(ORIENTATION, EnumOrientation.EAST);
          case SOUTH:
            return state.withProperty(ORIENTATION, EnumOrientation.NORTH);
          case NORTH:
            return state.withProperty(ORIENTATION, EnumOrientation.SOUTH);
          default:
            return state;
        }

      case COUNTERCLOCKWISE_90:
        switch(state.getValue(ORIENTATION)) {
          case EAST:
            return state.withProperty(ORIENTATION, EnumOrientation.NORTH);
          case WEST:
            return state.withProperty(ORIENTATION, EnumOrientation.SOUTH);
          case SOUTH:
            return state.withProperty(ORIENTATION, EnumOrientation.EAST);
          case NORTH:
            return state.withProperty(ORIENTATION, EnumOrientation.WEST);
          case UP_Z:
            return state.withProperty(ORIENTATION, EnumOrientation.UP_X);
          case UP_X:
            return state.withProperty(ORIENTATION, EnumOrientation.UP_Z);
          case DOWN_X:
            return state.withProperty(ORIENTATION, EnumOrientation.DOWN_Z);
          case DOWN_Z:
            return state.withProperty(ORIENTATION, EnumOrientation.DOWN_X);
        }

      case CLOCKWISE_90:
        switch(state.getValue(ORIENTATION)) {
          case EAST:
            return state.withProperty(ORIENTATION, EnumOrientation.SOUTH);
          case WEST:
            return state.withProperty(ORIENTATION, EnumOrientation.NORTH);
          case SOUTH:
            return state.withProperty(ORIENTATION, EnumOrientation.WEST);
          case NORTH:
            return state.withProperty(ORIENTATION, EnumOrientation.EAST);
          case UP_Z:
            return state.withProperty(ORIENTATION, EnumOrientation.UP_X);
          case UP_X:
            return state.withProperty(ORIENTATION, EnumOrientation.UP_Z);
          case DOWN_X:
            return state.withProperty(ORIENTATION, EnumOrientation.DOWN_Z);
          case DOWN_Z:
            return state.withProperty(ORIENTATION, EnumOrientation.DOWN_X);
        }

      default:
        return state;
    }
  }

  /**
   * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
   * blockstate.
   */
  @Nonnull
  @Override
  public IBlockState withMirror(@Nonnull IBlockState state, Mirror mirrorIn) {
    return state.withRotation(mirrorIn.toRotation(state.getValue(ORIENTATION).getFacing()));
  }

  /* Bounding boxes */
  private static final ImmutableMap<EnumOrientation, AxisAlignedBB> BOUNDS;

  static {
    ImmutableMap.Builder<EnumOrientation, AxisAlignedBB> builder = ImmutableMap.builder();
    builder.put(EnumOrientation.DOWN_X, new AxisAlignedBB(0.375, 0, 0, 0.625, 0.25, 1));
    builder.put(EnumOrientation.DOWN_Z, new AxisAlignedBB(0, 0, 0.375, 1, 0.25, 0.625));
    builder.put(EnumOrientation.UP_X, new AxisAlignedBB(0.375, 0.75, 0, 0.625, 1, 1));
    builder.put(EnumOrientation.UP_Z, new AxisAlignedBB(0, 0.75, 0.375, 1, 1, 0.625));
    builder.put(EnumOrientation.NORTH, new AxisAlignedBB(0, 0.75, 0, 1, 1, 0.25));
    builder.put(EnumOrientation.SOUTH, new AxisAlignedBB(0, 0.75, 0.75, 1, 1, 1));
    builder.put(EnumOrientation.EAST, new AxisAlignedBB(0.75, 0.75, 0, 1, 1, 1));
    builder.put(EnumOrientation.WEST, new AxisAlignedBB(0, 0.75, 0, 0.25, 1, 1));
    BOUNDS = builder.build();
  }

  @Override
  public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
    return BOUNDS.get(blockState.getValue(ORIENTATION));
  }

  @Nonnull
  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    return BOUNDS.get(state.getValue(ORIENTATION));
  }

  @Nonnull
  @Override
  public EnumBlockRenderType getRenderType(IBlockState state) {
    return EnumBlockRenderType.MODEL;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean shouldSideBeRendered(IBlockState blockState, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, EnumFacing side) {
    return true;
  }

  // restore default raytrace, as BlockTable changes that

  /**
   * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit.
   */
  @Override
  public RayTraceResult collisionRayTrace(IBlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {
    return this.rayTrace(pos, start, end, blockState.getBoundingBox(worldIn, pos));
  }

  @Override
  public boolean hasComparatorInputOverride(IBlockState state) {
    return state.getValue(DRYING) == Boolean.TRUE;
  }

  // comparator stuffs
  @Override
  public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    if(!(te instanceof TileDryingRack)) {
      return 0;
    }

    return ((TileDryingRack) te).comparatorStrength();
  }

  @Override
  public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
    // todo: implement this properly
    return false;
  }

  @Override
  @Deprecated
  public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
    if(side == state.getValue(ORIENTATION).getFacing()) {
      return BlockFaceShape.SOLID;
    }
    return BlockFaceShape.UNDEFINED;
  }
}
