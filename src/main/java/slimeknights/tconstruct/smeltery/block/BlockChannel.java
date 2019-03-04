package slimeknights.tconstruct.smeltery.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockWall;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.smeltery.IFaucetDepth;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileChannel;
import slimeknights.tconstruct.smeltery.tileentity.TileChannel.ChannelConnection;

public class BlockChannel extends BlockContainer implements IFaucetDepth {

  /** Properties for the channel */
  public static final PropertyBool DOWN = PropertyBool.create("down");

  public static final PropertyEnum<ChannelConnectionState> NORTH = PropertyEnum.create("north", ChannelConnectionState.class);
  public static final PropertyEnum<ChannelConnectionState> SOUTH = PropertyEnum.create("south", ChannelConnectionState.class);
  public static final PropertyEnum<ChannelConnectionState> WEST = PropertyEnum.create("west", ChannelConnectionState.class);
  public static final PropertyEnum<ChannelConnectionState> EAST = PropertyEnum.create("east", ChannelConnectionState.class);

  public BlockChannel() {
    super(Material.ROCK);

    this.setCreativeTab(TinkerRegistry.tabSmeltery);
    this.setHardness(3F);
    this.setResistance(20F);
    this.setSoundType(SoundType.METAL);
    this.setDefaultState(this.getDefaultState()
        .withProperty(DOWN, false)
        .withProperty(NORTH, ChannelConnectionState.NONE)
        .withProperty(SOUTH, ChannelConnectionState.NONE)
        .withProperty(WEST, ChannelConnectionState.NONE)
        .withProperty(EAST, ChannelConnectionState.NONE));
  }

  @Nonnull
  @Override
  public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
    return new TileChannel();
  }

  @Nonnull
  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, DOWN, NORTH, SOUTH, WEST, EAST);
  }

  /**
   * Convert the BlockState into the correct metadata value
   */
  @Override
  public int getMetaFromState(IBlockState state) {
    return 0;
  }


  /* Basic block logic */

  // Update the shape when a neighbor is added
  @Override
  public void neighborChanged(IBlockState state, World world, BlockPos pos, Block oldBlock, BlockPos neighbor) {
    // only run on server
    if(world.isRemote) {
      return;
    }

    // ignore if the block did not change
    TileEntity te = world.getTileEntity(pos);
    if(te instanceof TileChannel) {
      ((TileChannel) te).handleBlockUpdate(neighbor, oldBlock == Blocks.AIR, world.isBlockPowered(pos));
    }
  }

  // updates the channels blocked connections
  @Override
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    // if the player is holding a channel, skip unless we clicked the top
    // they can shift click to place one on the top
    if(player.getHeldItem(hand).getItem() == Item.getItemFromBlock(TinkerSmeltery.channel) && facing != EnumFacing.UP) {
      return false;
    }

    TileEntity te = worldIn.getTileEntity(pos);
    if(te instanceof TileChannel) {
      // default to using the clicked side, though null (is that valid?) and up act as down
      EnumFacing side = (facing == null || facing == EnumFacing.UP) ? EnumFacing.DOWN : facing;

      // try each of the sides, if clicked use that
      if(Util.clickedAABB(BOUNDS_NORTH, hitX, hitY, hitZ)) {
        side = EnumFacing.NORTH;
      } else if(Util.clickedAABB(BOUNDS_SOUTH, hitX, hitY, hitZ)) {
        side = EnumFacing.SOUTH;
      } else if(Util.clickedAABB(BOUNDS_WEST, hitX, hitY, hitZ)) {
        side = EnumFacing.WEST;
      } else if(Util.clickedAABB(BOUNDS_EAST, hitX, hitY, hitZ)) {
        side = EnumFacing.EAST;
      }

      // then run the interaction
      return ((TileChannel) te).interact(player, side);
    }
    return super.onBlockActivated(worldIn, pos, state, player, hand, facing, hitX, hitY, hitZ);
  }

  /**
   * Get the actual Block state of this Block at the given position. This applies properties not visible in the
   * metadata, such as fence connections.
   */
  @Override
  @Deprecated
  public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
    state = addTEData(state, world, pos);

    state = addExtra(state, world, pos, NORTH, EnumFacing.NORTH);
    state = addExtra(state, world, pos, SOUTH, EnumFacing.SOUTH);
    state = addExtra(state, world, pos, WEST, EnumFacing.WEST);
    state = addExtra(state, world, pos, EAST, EnumFacing.EAST);

    return state;
  }

  private IBlockState addExtra(IBlockState state, IBlockAccess world, BlockPos pos, PropertyEnum<ChannelConnectionState> prop, EnumFacing side) {
    ChannelConnectionState connection = state.getValue(prop);
    IBlockState offsetState = world.getBlockState(pos.offset(side));
    Block block = offsetState.getBlock();
    if(connection == ChannelConnectionState.NONE
        && (block instanceof BlockLever && offsetState.getValue(BlockLever.FACING).getFacing() == side
        || block instanceof BlockButton && offsetState.getValue(BlockButton.FACING) == side)) {
      state = state.withProperty(prop, ChannelConnectionState.LEVER);
    }

    return state;
  }

  protected IBlockState addTEData(IBlockState state, IBlockAccess world, BlockPos pos) {
    // needs to be a channel
    TileEntity te = world.getTileEntity(pos);
    if(!(te instanceof TileChannel)) {
      return state;
    }

    // then just query the TE
    TileChannel channel = (TileChannel) te;
    return state.withProperty(DOWN, channel.isConnectedDown())
        .withProperty(NORTH, ChannelConnectionState.fromConnection(channel.getConnection(EnumFacing.NORTH)))
        .withProperty(SOUTH, ChannelConnectionState.fromConnection(channel.getConnection(EnumFacing.SOUTH)))
        .withProperty(WEST, ChannelConnectionState.fromConnection(channel.getConnection(EnumFacing.WEST)))
        .withProperty(EAST, ChannelConnectionState.fromConnection(channel.getConnection(EnumFacing.EAST)));
  }

  /* Bounds */
  /** Bounds for the full center piece, used just about everywhere */
  private static final AxisAlignedBB BOUNDS_CENTER = new AxisAlignedBB(0.3125, 0.125, 0.3125, 0.6875, 0.5, 0.6875);
  /** Bounds for the center channel when unconnected, used for collision */
  private static final AxisAlignedBB BOUNDS_CENTER_UNCONNECTED = new AxisAlignedBB(0.3125, 0.25, 0.3125, 0.6875, 0.5, 0.6875);

  /* Bounds for channels with just one side, used for collision and interaction */
  private static final AxisAlignedBB BOUNDS_NORTH = new AxisAlignedBB(0.3125, 0.25, 0,      0.6875, 0.5, 0.3125);
  private static final AxisAlignedBB BOUNDS_SOUTH = new AxisAlignedBB(0.3125, 0.25, 0.6875, 0.6875, 0.5, 1);
  private static final AxisAlignedBB BOUNDS_WEST  = new AxisAlignedBB(0,      0.25, 0.3125, 0.3125, 0.5, 0.6875);
  private static final AxisAlignedBB BOUNDS_EAST  = new AxisAlignedBB(0.6875, 0.25, 0.3125, 1,      0.5, 0.6875);

  /**
   * Visible bounding boxes for the channel. These are index using some bitmath as it was the cleanest approach (O(1))
   * Note the single sides differ from the directional bounds above in that they include both the center and the possible lower connection
   */
  private static final AxisAlignedBB[] BOUNDS = {
      BOUNDS_CENTER, // 0000
      new AxisAlignedBB(0.3125, 0.125, 0.3125, 1,      0.5, 0.6875), // 0001    E
      new AxisAlignedBB(0,      0.125, 0.3125, 0.6875, 0.5, 0.6875), // 0010   W
      new AxisAlignedBB(0,      0.125, 0.3125, 1,      0.5, 0.6875), // 0011   WE
      new AxisAlignedBB(0.3125, 0.125, 0.3125, 0.6875, 0.5, 1     ), // 0100  S
      new AxisAlignedBB(0.3125, 0.125, 0.3125, 1,      0.5, 1     ), // 0101  S E
      new AxisAlignedBB(0,      0.125, 0.3125, 0.6875, 0.5, 1     ), // 0110  SW
      new AxisAlignedBB(0,      0.125, 0.3125, 1,      0.5, 1     ), // 0111  SWE
      new AxisAlignedBB(0.3125, 0.125, 0,      0.6875, 0.5, 0.6875), // 1000 N
      new AxisAlignedBB(0.3125, 0.125, 0,      1,      0.5, 0.6875), // 1001 N  E
      new AxisAlignedBB(0,      0.125, 0,      0.6875, 0.5, 0.6875), // 1010 N W
      new AxisAlignedBB(0,      0.125, 0,      1,      0.5, 0.6875), // 1011 N WE
      new AxisAlignedBB(0.3125, 0.125, 0,      0.6875, 0.5, 1     ), // 1100 NS
      new AxisAlignedBB(0.3125, 0.125, 0,      1,      0.5, 1     ), // 1101 NS E
      new AxisAlignedBB(0,      0.125, 0,      0.6875, 0.5, 1     ), // 1110 NSW
      new AxisAlignedBB(0,      0.125, 0,      1,      0.5, 1     )  // 1111 NSWE
  };

  @Nonnull
  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    // do a bit of bit math to determine the box to use, it seemed like the fastest solution
    state = state.getActualState(source, pos);
    int index = (state.getValue(NORTH).canFlow() ? 8 : 0)
        + (state.getValue(SOUTH).canFlow() ? 4 : 0)
        + (state.getValue(WEST).canFlow() ? 2 : 0)
        + (state.getValue(EAST).canFlow() ? 1 : 0);

    return BOUNDS[index];
  }

  @Override
  public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean p_185477_7_) {
    state = state.getActualState(world, pos);
    // if downspout, used extended down
    if(state.getValue(DOWN)) {
      addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDS_CENTER);
    } else {
      addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDS_CENTER_UNCONNECTED);
    }
    // add each side
    if(state.getValue(NORTH).canFlow()) {
      addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDS_NORTH);
    }
    if(state.getValue(SOUTH).canFlow()) {
      addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDS_SOUTH);
    }
    if(state.getValue(WEST).canFlow()) {
      addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDS_WEST);
    }
    if(state.getValue(EAST).canFlow()) {
      addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDS_EAST);
    }
  }

  @Deprecated
  @Override
  public RayTraceResult collisionRayTrace(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {
    state = state.getActualState(world, pos);

    // basically the same BlockStairs does
    // Raytrace through all included AABBs (sides) and return the nearest
    // in the case of channels, we need to ensure each piece is actually enabled first though
    List<RayTraceResult> list = new ArrayList<>(5);
    list.add(rayTrace(pos, start, end, BOUNDS_CENTER));

    // add each enabled side
    if(state.getValue(NORTH).canFlow()) {
      list.add(rayTrace(pos, start, end, BOUNDS_NORTH));
    }
    if(state.getValue(SOUTH).canFlow()) {
      list.add(rayTrace(pos, start, end, BOUNDS_SOUTH));
    }
    if(state.getValue(WEST).canFlow()) {
      list.add(rayTrace(pos, start, end, BOUNDS_WEST));
    }
    if(state.getValue(EAST).canFlow()) {
      list.add(rayTrace(pos, start, end, BOUNDS_EAST));
    }

    // compare results
    RayTraceResult result = null;
    double max = 0.0D;
    for(RayTraceResult raytraceresult : list) {
      if(raytraceresult != null) {
        double distance = raytraceresult.hitVec.squareDistanceTo(end);
        if(distance > max) {
          result = raytraceresult;
          max = distance;
        }
      }
    }

    return result;
  }

  /* Block properties */
  @Nonnull
  @Override
  public EnumBlockRenderType getRenderType(IBlockState state) {
    return EnumBlockRenderType.MODEL;
  }

  @Override
  @Deprecated
  public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
    if(side.getAxis() == Axis.Y) {
      return BlockFaceShape.UNDEFINED;
    }
    // slightly hacky as we want to avoid fences connecting, but they use the same logic as lever placement
    Block block = world.getBlockState(pos.offset(side)).getBlock();
    return block instanceof BlockFence || block instanceof BlockWall ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
  }

  @Override
  public boolean isFullCube(IBlockState state) {
    return false;
  }

  @Override
  public boolean isOpaqueCube(IBlockState state) {
    return false;
  }

  @Override
  public float getFlowDepth(World world, BlockPos pos, IBlockState state) {
    return 0.53125f;
  }

  public static enum ChannelConnectionState implements IStringSerializable {
    NONE,
    IN,
    OUT,
    LEVER;

    byte index;
    ChannelConnectionState() {
      index = (byte)ordinal();
    }

    public static ChannelConnectionState fromConnection(ChannelConnection connection) {
      return values()[connection.getIndex()];
    }

    @Override
    public String getName() {
      return this.toString().toLowerCase(Locale.US);
    }

    public boolean canFlow() {
      return this != NONE;
    }
  }
}
