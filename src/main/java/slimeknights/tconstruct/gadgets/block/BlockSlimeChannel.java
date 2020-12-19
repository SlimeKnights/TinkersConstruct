package slimeknights.tconstruct.gadgets.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.gadgets.tileentity.TileSlimeChannel;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.shared.block.BlockSlime;
import slimeknights.tconstruct.shared.block.BlockSlime.SlimeType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class BlockSlimeChannel extends EnumBlock<SlimeType> implements ITileEntityProvider {

  public static final PropertyDirection SIDE = PropertyDirection.create("side");
  public static final PropertyEnum<ChannelDirection> DIRECTION = PropertyEnum.create("direction", ChannelDirection.class);
  public static final PropertyBool POWERED = PropertyBool.create("powered");
  public static final PropertyEnum<ChannelConnected> CONNECTED = PropertyEnum.create("connected", ChannelConnected.class); // stored dynamically
  public static final PropertyEnum<SlimeType> TYPE = BlockSlime.TYPE;

  public BlockSlimeChannel() {
    super(Material.CLAY, TYPE, SlimeType.class);
    this.setDefaultState(this.getBlockState().getBaseState().withProperty(TYPE, SlimeType.GREEN)
                                                            .withProperty(SIDE, EnumFacing.DOWN)
                                                            .withProperty(DIRECTION, ChannelDirection.NORTH)
                                                            .withProperty(POWERED, Boolean.FALSE)
                                                            .withProperty(CONNECTED, ChannelConnected.NONE));
    //this.side = side;
    this.setHardness(0f);
    this.setSoundType(SoundType.SLIME);

    this.setCreativeTab(TinkerRegistry.tabGadgets);
    this.isBlockContainer = true; // has TE
  }

  /* Block state */
  @Nonnull
  @Override
  protected BlockStateContainer createBlockState() {
    // CONNECTED determines how the channel is connected to the blocks next to it
    return new BlockStateContainer(this, TYPE, SIDE, DIRECTION, POWERED, CONNECTED);
  }

  // color and side data are stored on the tile entity, but are pulled into the blockstate upon loading the tile entity
  @Nonnull
  @Override
  public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
    return new TileSlimeChannel();
  }

  /**
   * Convert the given metadata into a BlockState for this Block
   */
  @Nonnull
  @Override
  public IBlockState getStateFromMeta(int meta) {
    return this.getDefaultState().withProperty(TYPE, SlimeType.fromMeta(meta & 7))
                                 .withProperty(POWERED, (meta & 8) > 0);
  }

  /**
   * Convert the BlockState into the correct metadata value
   */
  @Override
  public int getMetaFromState(IBlockState state) {
    int meta = state.getValue(TYPE).getMeta();
    if(state.getValue(POWERED)) {
      meta |= 8;
    }
    return meta;
  }

  @Nonnull
  @Override
  public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess source, BlockPos pos) {
    state = addDataFromTE(state, source, pos);
    // connections!
    // first, try the outside, the block in front of this
    // this is checked first since a full peice is better than a partial one in the case of both
    EnumFacing side = state.getValue(SIDE);
    EnumFacing flow = state.getValue(DIRECTION).getFlow(side);
    BlockPos offset = pos.offset(side.getOpposite());
    IBlockState check = source.getBlockState(offset);
    if(check.getBlock() == this && addDataFromTE(check, source, offset).getValue(SIDE).getOpposite() == flow) {
      return state.withProperty(CONNECTED, ChannelConnected.OUTER);
    }
    // if that does not work, try to connect to the inside, or the block behind this
    offset = pos.offset(side);
    check = source.getBlockState(offset);
    if(check.getBlock() == this && addDataFromTE(check, source, offset).getValue(SIDE) == flow) {
      return state.withProperty(CONNECTED, ChannelConnected.INNER);
    }

    // if neither work, no connection
    return state.withProperty(CONNECTED, ChannelConnected.NONE);
  }

  /**
   * Safe way to grab TE data above since we don't want to call getActualState inside itself for connections
   * (it would go back and forth and back and forth between the two blocks)
   */
  private IBlockState addDataFromTE(IBlockState state, IBlockAccess source, BlockPos pos) {
    TileEntity te = source.getTileEntity(pos);
    if(te instanceof TileSlimeChannel) {
      TileSlimeChannel channel = (TileSlimeChannel) te;
      return state.withProperty(SIDE, channel.getSide())
                  .withProperty(DIRECTION, channel.getDirection());
    }
    return state;
  }

  /**
   * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
   * IBlockState
   */
  @Override
  public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
    // we temporarily store the data in the blockstate until the TE is created
    return this.getDefaultState().withProperty(TYPE, SlimeType.fromMeta(meta))
                                 .withProperty(SIDE, facing.getOpposite())
                                 .withProperty(DIRECTION, getPlacement(facing.getOpposite(), hitX, hitY, hitZ, placer));
  }

  private ChannelDirection getPlacement(EnumFacing side, float hitX, float hitY, float hitZ, EntityLivingBase placer) {
    // determine the coordinates that we hit the face on
    int u = 0,
        v = 0;
    // up and down are the same
    if(side.getAxis() == EnumFacing.Axis.Y) {
      u = (int) (hitX * 16);
      v = (int) (hitZ * 16);
    }
    else {
      // all other sides use "y" as the "v" coordinate, but different "u"
      v = 15 - (int) (hitY * 16);
      switch(side) {
        case NORTH:
          u = (int) (hitX * 16);
          break;
        case SOUTH:
          u = 15 - (int) (hitX * 16);
          break;
        case WEST:
          u = 15 - (int) (hitZ * 16);
          break;
        case EAST:
          u = (int) (hitZ * 16);
          break;
      }
    }

    // now that we have our UV, determine the direction from that
    ChannelDirection direction;
    // top
    if(v < 5) {
      // left
      if(u < 5) {
        direction = ChannelDirection.NORTHWEST;
      }
      // right
      else if(u > 10) {
        direction = ChannelDirection.NORTHEAST;
      }
      // middle
      else {
        direction = ChannelDirection.NORTH;
      }
    }
    // bottom
    else if(v > 10) {
      // left
      if(u < 5) {
        direction = ChannelDirection.SOUTHWEST;
      }
      // right
      else if(u > 10) {
        direction = ChannelDirection.SOUTHEAST;
      }
      // middle
      else {
        direction = ChannelDirection.SOUTH;
      }
    }
    // middle
    else {
      // left
      if(u < 5) {
        direction = ChannelDirection.WEST;
      }
      // right
      else if(u > 10) {
        direction = ChannelDirection.EAST;
      }
      // exact center defaults to facing
      else {
        int facing = MathHelper.floor(placer.rotationYaw * 8.0F / 360.0F + 0.5D) & 7;
        direction = ChannelDirection.fromIndex(facing);
        // if on a wall, we rotate it a bit to make sure facing directly towards the wall is up
        if(side.getAxis() != EnumFacing.Axis.Y) {
          switch(side) {
            case SOUTH:
              direction = direction.getOpposite();
              break;
            case WEST:
              direction = direction.rotate90();
              break;
            case EAST:
              direction = direction.rotate90().getOpposite();
              break;
          }
        }
      }
    }

    // if sneaking, reverse direction
    if(direction != null && placer.isSneaking()) {
      direction = direction.getOpposite();
    }
    return direction;
  }

  /**
   * Called by ItemBlocks after a block is set in the world, to allow post-place logic
   */
  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    TileEntity te = worldIn.getTileEntity(pos);
    // pull the data we stored earlier into the Tile Entity
    if(te instanceof TileSlimeChannel) {
      TileSlimeChannel channel = (TileSlimeChannel) te;
      channel.setSide(state.getValue(SIDE));
      channel.setDirection(state.getValue(DIRECTION));
    }
  }

  /* Item drops */
  @Override
  public int damageDropped(IBlockState state) {
    return state.getValue(TYPE).getMeta();
  }

  @Override
  public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
    for(SlimeType type : SlimeType.VISIBLE_COLORS) {
      list.add(new ItemStack(this, 1, type.getMeta()));
    }
  }

  /* Slimey flow */

  /**
   * Called When an Entity Collided with the Block
   */
  @Override
  public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
    if(!state.getValue(POWERED)) {
      // bounding box to check
      AxisAlignedBB entityAABB = entity.getCollisionBoundingBox();
      if(entityAABB == null) {
        entityAABB = entity.getEntityBoundingBox();
      }

      // items be dumb
      double speed = 0.01;
      boolean item = false;
      if(entity instanceof EntityItem) {
        speed *= 1.5;
        item = true;
      }

      // data
      Motion motion = new Motion();
      boolean inBounds = false;
      state = state.getActualState(world, pos); // get the direction and connected values
      EnumFacing side = state.getValue(SIDE);

      // only apply movement if the entity is within the liquid
      if(entityAABB.intersects(getBounds(state, world, pos).offset(pos))) {
        inBounds = true; // tell the other bounding box not to reduce gravity again
        // no drowining in slime channels
        if(entity.isEntityAlive()) {
          entity.setAir(300);
        }
        // generic liquid stuff
        entity.setFire(0);
        entity.fallDistance = 0;

        List<EnumFacing> flow = state.getValue(DIRECTION).getFlowDiagonals(side);
        // its slimy, downward motion is reduced
        if(!flow.contains(EnumFacing.DOWN) && entity.motionY < 0) {
          entity.motionY /= 2;
        }

        // allow items to float upwards
        if(flow.contains(EnumFacing.UP) && item) {
          entity.onGround = false;
        }

        // apply motion boosts, may be twice
        for(EnumFacing facing : flow) {
          motion.boost(facing, speed);
        }
      }

      // apply additional movement based on the "connected" bounding box
      ChannelConnected connected = state.getValue(CONNECTED);
      if(connected == ChannelConnected.OUTER && entityAABB.intersects(getSecondaryBounds(state).offset(pos))) {
        // only run this if not already in bounds above
        // mainly to remove redundancy, but it does have an effect with the fall speed
        if(!inBounds) {
          // makes the block "slimey", as in you fall through it slowly
          if(side != EnumFacing.DOWN && entity.motionY < 0) {
            entity.motionY /= 2;
          }
          // stop drowning
          if(entity.isEntityAlive()) {
            entity.setAir(300);
          }
          // normal liquid
          entity.setFire(0);
          entity.fallDistance = 0;
        }

        // allow items to float upwards
        if(side == EnumFacing.UP && item) {
          entity.onGround = false;
        }
        motion.boost(side, speed);
      }

      // finally, apply the boost
      entity.addVelocity(motion.x, motion.y, motion.z);
    }
  }

  // tells the game that the entity is in water
  @Nullable
  @Override
  public Boolean isEntityInsideMaterial(IBlockAccess world, BlockPos pos, IBlockState state, Entity entity, double yToTest, Material material, boolean testingHead) {
    if(material != Material.WATER) {
      return null;
    }

    // bounding box to check
    AxisAlignedBB entityAABB = entity.getCollisionBoundingBox();
    if(entityAABB == null) {
      entityAABB = entity.getEntityBoundingBox();
    }

    // main bounding box
    state = state.getActualState(world, pos); // connected properties
    if(entityAABB.intersects(getBounds(state, world, pos).offset(pos))) {
      return Boolean.TRUE;
    }
    // extra box used on sideways channels
    else if(state.getValue(CONNECTED) == ChannelConnected.OUTER
            && entityAABB.intersects(getSecondaryBounds(state).offset(pos))) {
      return Boolean.TRUE;
    }

    return Boolean.FALSE;
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
    if(powered != state.getValue(POWERED).booleanValue()) {
      world.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(powered)));
    }
  }

  /* Bounds */
  // Block hitbox and main location for motion
  private static final ImmutableMap<EnumFacing, AxisAlignedBB> BOUNDS;
  // "quarter slab" bounds on bottom used for location checks on connected blocks
  private static final ImmutableMap<EnumFacing, AxisAlignedBB> LOWER_BOUNDS;
  // "quarter slab" bounds on side for sideways connected
  private static final ImmutableMap<EnumFacing, AxisAlignedBB> SIDE_BOUNDS;
  // "quarter slab" bounds on top for bottom outer connected
  private static final ImmutableMap<EnumFacing, AxisAlignedBB> UPPER_BOUNDS;

  static {
    ImmutableMap.Builder<EnumFacing, AxisAlignedBB> builder = ImmutableMap.builder();
    builder.put(EnumFacing.UP, new AxisAlignedBB(0, 0.5, 0, 1, 1, 1));
    builder.put(EnumFacing.DOWN, new AxisAlignedBB(0, 0, 0, 1, 0.5, 1));
    builder.put(EnumFacing.NORTH, new AxisAlignedBB(0, 0, 0, 1, 1, 0.5));
    builder.put(EnumFacing.SOUTH, new AxisAlignedBB(0, 0, 0.5, 1, 1, 1));
    builder.put(EnumFacing.WEST, new AxisAlignedBB(0, 0, 0, 0.5, 1, 1));
    builder.put(EnumFacing.EAST, new AxisAlignedBB(0.5, 0, 0, 1, 1, 1));
    BOUNDS = builder.build();

    builder = ImmutableMap.builder();
    builder.put(EnumFacing.NORTH, new AxisAlignedBB(0, 0, 0, 1, 0.5, 0.5));
    builder.put(EnumFacing.SOUTH, new AxisAlignedBB(0, 0, 0.5, 1, 0.5, 1));
    builder.put(EnumFacing.WEST, new AxisAlignedBB(0, 0, 0, 0.5, 0.5, 1));
    builder.put(EnumFacing.EAST, new AxisAlignedBB(0.5, 0, 0, 1, 0.5, 1));
    LOWER_BOUNDS = builder.build();

    builder = ImmutableMap.builder();
    builder.put(EnumFacing.NORTH, new AxisAlignedBB(0, 0, 0, 0.5, 1, 0.5));
    builder.put(EnumFacing.SOUTH, new AxisAlignedBB(0.5, 0, 0.5, 1, 1, 1));
    builder.put(EnumFacing.WEST, new AxisAlignedBB(0, 0, 0.5, 0.5, 1, 1));
    builder.put(EnumFacing.EAST, new AxisAlignedBB(0.5, 0, 0, 1, 1, 0.5));
    SIDE_BOUNDS = builder.build();

    builder = ImmutableMap.builder();
    builder.put(EnumFacing.NORTH, new AxisAlignedBB(0, 0.5, 0, 1, 1, 0.5));
    builder.put(EnumFacing.SOUTH, new AxisAlignedBB(0, 0.5, 0.5, 1, 1, 1));
    builder.put(EnumFacing.WEST, new AxisAlignedBB(0, 0.5, 0, 0.5, 1, 1));
    builder.put(EnumFacing.EAST, new AxisAlignedBB(0.5, 0.5, 0, 1, 1, 1));
    UPPER_BOUNDS = builder.build();
  }

  @Override
  public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
    return NULL_AABB;
  }

  @Nonnull
  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    return getBounds(state.getActualState(source, pos), source, pos);
  }

  /**
   * Returns the bounds for the current state
   * <br>
   * Makes sure you pas the actual state into this or it won't take connections into account
   */
  private AxisAlignedBB getBounds(IBlockState state, IBlockAccess source, BlockPos pos) {
    EnumFacing side = state.getValue(SIDE);
    EnumFacing facing = state.getValue(DIRECTION).getFacing();
    ChannelConnected connected = state.getValue(CONNECTED);

    // diagonals return null above, and cannot have such connections anyways
    if(connected == ChannelConnected.INNER && facing != null) {
      if(side == EnumFacing.DOWN) {
        return LOWER_BOUNDS.get(facing);
      }
      else if(side == EnumFacing.UP) {
        return UPPER_BOUNDS.get(facing);
      }
      else {
        switch(facing) {
          case NORTH:
            return UPPER_BOUNDS.get(side);
          case SOUTH:
            return LOWER_BOUNDS.get(side);
          case WEST:
            return SIDE_BOUNDS.get(side);
          case EAST:
            return SIDE_BOUNDS.get(side.rotateY());
        }
      }
    }
    return BOUNDS.get(side);
  }

  private AxisAlignedBB getSecondaryBounds(IBlockState state) {
    EnumFacing side = state.getValue(SIDE);
    EnumFacing facing = state.getValue(DIRECTION).getFacing();

    // this just prevents a NPE in the case of an invalid state
    // as a block will never be connected and diagonal except in debug
    if(facing == null) {
      return FULL_BLOCK_AABB;
    }

    if(side == EnumFacing.DOWN) {
      return UPPER_BOUNDS.get(facing.getOpposite());
    }
    else if(side == EnumFacing.UP) {
      return LOWER_BOUNDS.get(facing.getOpposite());
    }
    else {
      switch(facing) {
        case NORTH:
          return LOWER_BOUNDS.get(side.getOpposite());
        case SOUTH:
          return UPPER_BOUNDS.get(side.getOpposite());
        case WEST:
          return SIDE_BOUNDS.get(side.getOpposite());
        case EAST:
          return SIDE_BOUNDS.get(side.rotateYCCW());
        default:
          return FULL_BLOCK_AABB;
      }
    }
  }

  /* Misc */

  @Nonnull
  @Override
  @SideOnly(Side.CLIENT)
  public BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.TRANSLUCENT;
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
  @Deprecated
  public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
    state = state.getActualState(world, pos);
    EnumFacing side = state.getValue(SIDE);
    if(hasFullSide(face.getOpposite(), side, state.getValue(DIRECTION).getFlow(side), state.getValue(CONNECTED))) {
      return BlockFaceShape.SOLID;
    }
    return BlockFaceShape.UNDEFINED;
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean shouldSideBeRendered(IBlockState state, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, EnumFacing face) {
    @SuppressWarnings("unused")
    int knightminers_sanity_percentage_after_writing_function = 14;
    // common logic for solid blocks, basically if a solid face is on the side we can skip all of this
    if(!super.shouldSideBeRendered(state, blockAccess, pos, face)) {
      return false;
    }
    // otherwise back out if the block is not a channel
    IBlockState offset = blockAccess.getBlockState(pos.offset(face));
    if(!(offset.getBlock() == this)) {
      return true;
    }
    // or if it is a different color
    if(state.getValue(TYPE) != offset.getValue(TYPE)) {
      return true;
    }

    // so, it matches. great, grab directions and connections
    state = state.getActualState(blockAccess, pos);
    offset = offset.getActualState(blockAccess, pos.offset(face));

    // then set up some data for readability and send it along
    EnumFacing side = state.getValue(SIDE);
    ChannelConnected connected = state.getValue(CONNECTED);
    EnumFacing flow = state.getValue(DIRECTION).getFlow(side);
    EnumFacing offsetSide = offset.getValue(SIDE);
    ChannelConnected offsetConnected = offset.getValue(CONNECTED);
    EnumFacing offsetFlow = offset.getValue(DIRECTION).getFlow(offsetSide);

    // for diagonals, overwrite the connected value
    if(flow == null) {
      connected = ChannelConnected.NONE;
    }
    if(offsetFlow == null) {
      offsetConnected = ChannelConnected.NONE;
    }

    // the other channel is against our back
    if(face == side) {
      // if its inner, we have a half face
      if(connected == ChannelConnected.INNER) {
        // so ask with the half being our direction
        return !hasHalfSide(flow, face, offsetSide, offsetFlow, offsetConnected);
      }
      // otherwise we have a full one
      return !hasFullSide(face, offsetSide, offsetFlow, offsetConnected);
    }

    // the other channel is "above" of ours
    if(face == side.getOpposite()) {
      // outer state has one half face
      if(connected == ChannelConnected.OUTER) {
        // though the half is opposite the direction now
        return !hasHalfSide(flow.getOpposite(), face, offsetSide, offsetFlow, offsetConnected);
      }
      // if its not the outer face, it really doesn't matter as no faces are here to hide
      return true;
    }

    // the other channel is in front of where we flow to
    if(face == flow) {
      // in this case, the face is always there, so just send a generic half side
      return !hasHalfSide(side, face, offsetSide, offsetFlow, offsetConnected);
    }

    // the other channel is behind us
    if(face.getOpposite() == flow) {
      // do we have a full face, a half one, or none?
      switch(connected) {
        // inner means no face to deal with, so back out
        case INNER:
          return true;
        // outer means a full face
        case OUTER:
          return !hasFullSide(face, offsetSide, offsetFlow, offsetConnected);
        // none means half face
        case NONE:
          return !hasHalfSide(side, face, offsetSide, offsetFlow, offsetConnected);
      }
    }

    // last two cases are complicated, as they both can be a quarter, half, or three quarters
    switch(connected) {
      // easiest version, means we have a half face like above
      case NONE:
        return !hasHalfSide(side, face, offsetSide, offsetFlow, offsetConnected);
      // outer means we have a stair shape with a hole opposite the side in the direction
      case OUTER:
        // if we have a full side, then definatelly
        if(hasFullSide(face, offsetSide, offsetFlow, offsetConnected)) {
          return false;
        }
        // if its the same shape, there is a possibility
        if(offsetConnected == ChannelConnected.OUTER) {
          // option one, we are the same shape
          if(offsetSide == side) {
            return offsetFlow != flow;
          }
          // option two is option one flipped twice
          if(offsetSide == flow.getOpposite()) {
            return offsetFlow != side.getOpposite();
          }
          // neither? just show it
          return true;
        }
        // both inner and none mean we cannot cull since full sides were already matched
        return true;
      // inner means we have a quarter in the front, or in the direction we go
      case INNER:
        // on any of theses three sides, if it matches a direction below it should not cull, but culls otherwise
        if(offsetSide == side || offsetSide == face.getOpposite() || offsetSide == flow) {
          return offsetConnected == ChannelConnected.INNER
                 && (offsetFlow == side.getOpposite() || offsetFlow == face || offsetFlow == flow.getOpposite());
        }

        // the other three can only possibly connect if on the outer side
        if(offsetConnected == ChannelConnected.OUTER) {
          // since we already checked the side above, all thats left is the direction which states we should cull
          if(offsetFlow == face || offsetFlow == side.getOpposite() || offsetFlow == flow.getOpposite()) {
            return false;
          }
        }
        return true;
    }

    // isn't possible as connected cannot be null, but here to prevent errors should it become possible
    return true;
  }

  private static boolean hasFullSide(EnumFacing orginFace, EnumFacing side, EnumFacing flow, ChannelConnected connected) {
    // back, full unless we are the inner corner
    if(orginFace == side.getOpposite() && connected != ChannelConnected.INNER) {
      return true;
    }
    // back side face, full if we are connected
    return orginFace == flow && connected == ChannelConnected.OUTER;
  }

  private static boolean hasHalfSide(EnumFacing orginHalf, EnumFacing orginFace, EnumFacing side, EnumFacing flow, ChannelConnected connected) {
    // if we are on the same side as the half face
    if(side == orginHalf) {
      // make sure inner connections face the same direction
      if(connected == ChannelConnected.INNER) {
        // the direction is opposite the face of the half
        return flow == orginFace.getOpposite();
      }
      // both outer and none have this face solid
      return true;
    }

    // pressed up against this, basically the same as above only switched half and direction
    if(side == orginFace.getOpposite()) {
      // inner connection must be on the same half as the direction its going
      if(connected == ChannelConnected.INNER) {
        return flow == orginHalf;
      }
      // both outer and none have this face solid
      return true;
    }

    // opposite side of the block
    if(side == orginFace) {
      // it must face the opposite of the half and be connected outer
      return connected == ChannelConnected.OUTER && flow == orginHalf.getOpposite();
    }

    // there are three remaining directions, but their only chance is if they have the outer chance
    if(connected == ChannelConnected.OUTER) {
      // if its facing away, it has a full face here
      if(flow == orginFace) {
        return true;
      }
      // if the channel is opposite the half, the only valid facing is the one handled above
      if(side == orginHalf.getOpposite()) {
        return false;
      }
      // otherwise there is an additional valid facing, going the opposite direction of the half leading to "stairs"
      return flow == orginHalf.getOpposite();
    }

    return false;
  }

  /* Helpers */

  /**
   * Stores the direction of the channel, though relative to the side
   */
  public enum ChannelDirection implements IStringSerializable {
    SOUTH,
    SOUTHWEST,
    WEST,
    NORTHWEST,
    NORTH,
    NORTHEAST,
    EAST,
    SOUTHEAST;

    public final int index;

    ChannelDirection() {
      this.index = this.ordinal();
    }

    @Override
    public String getName() {
      return this.toString().toLowerCase(Locale.US);
    }

    /**
     * @return an integer representing this value, used for the sake of saving this to the TE
     */
    public int getIndex() {
      return index;
    }

    /**
     * @return the value corresponding to the integer given, used for loading from the TE
     */
    public static ChannelDirection fromIndex(int index) {
      if(index < 0 || index >= values().length) {
        index = 0;
      }

      return values()[index];
    }

    /**
     * @return the opposite direction for the current side
     */
    public ChannelDirection getOpposite() {
      switch(this) {
        case SOUTH:
          return NORTH;
        case SOUTHWEST:
          return NORTHEAST;
        case WEST:
          return EAST;
        case NORTHWEST:
          return SOUTHEAST;
        case NORTH:
          return SOUTH;
        case NORTHEAST:
          return SOUTHWEST;
        case EAST:
          return WEST;
        case SOUTHEAST:
          return NORTHWEST;
      }
      // not possible, but here because eclipse wants it
      return null;
    }

    /**
     * @return the opposite direction for the current side
     */
    public ChannelDirection rotate90() {
      switch(this) {
        case SOUTH:
          return WEST;
        case SOUTHWEST:
          return NORTHWEST;
        case WEST:
          return NORTH;
        case NORTHWEST:
          return NORTHEAST;
        case NORTH:
          return EAST;
        case NORTHEAST:
          return SOUTHEAST;
        case EAST:
          return SOUTH;
        case SOUTHEAST:
          return SOUTHWEST;
        default:
          throw new IllegalArgumentException("Unknown enum value? Impossibru!");
      }
    }

    /**
     * Gets the EnumFacing value with the same name as one of this Enum
     */
    public EnumFacing getFacing() {
      switch(this) {
        case NORTH:
          return EnumFacing.NORTH;
        case SOUTH:
          return EnumFacing.SOUTH;
        case WEST:
          return EnumFacing.WEST;
        case EAST:
          return EnumFacing.EAST;
      }
      return null;
    }

    /**
     * Gets the EnumFacing value with the same name as one of this Enum
     */
    public ChannelDirection fromFacing(EnumFacing facing) {
      switch(facing) {
        case NORTH:
          return NORTH;
        case SOUTH:
          return SOUTH;
        case WEST:
          return WEST;
        case EAST:
          return EAST;
      }
      return null;
    }

    /**
     * Returns the direction of flow for the given side
     * <br>
     * If the side is a diagonal, it returns null, use getDiagonal below for the two relevant directions
     */
    @Nullable
    public EnumFacing getFlow(EnumFacing side) {
      switch(side) {
        case NORTH:
          switch(this) {
            case NORTH:
              return EnumFacing.UP;
            case SOUTH:
              return EnumFacing.DOWN;
            case WEST:
              return EnumFacing.WEST;
            case EAST:
              return EnumFacing.EAST;
          }
        case SOUTH:
          switch(this) {
            case NORTH:
              return EnumFacing.UP;
            case SOUTH:
              return EnumFacing.DOWN;
            case WEST:
              return EnumFacing.EAST;
            case EAST:
              return EnumFacing.WEST;
          }
        case WEST:
          switch(this) {
            case NORTH:
              return EnumFacing.UP;
            case SOUTH:
              return EnumFacing.DOWN;
            case WEST:
              return EnumFacing.SOUTH;
            case EAST:
              return EnumFacing.NORTH;
          }
        case EAST:
          switch(this) {
            case NORTH:
              return EnumFacing.UP;
            case SOUTH:
              return EnumFacing.DOWN;
            case WEST:
              return EnumFacing.NORTH;
            case EAST:
              return EnumFacing.SOUTH;
          }
        default:
          // note that this returns null for diagonals
          return this.getFacing();
      }
    }

    /**
     * Returns a list of one or two directions for the sake of liquid flow
     */
    public List<EnumFacing> getFlowDiagonals(@Nonnull EnumFacing side) {
      switch(this) {
        case NORTH:
          return ImmutableList.of(NORTH.getFlow(side));
        case SOUTH:
          return ImmutableList.of(SOUTH.getFlow(side));
        case WEST:
          return ImmutableList.of(WEST.getFlow(side));
        case EAST:
          return ImmutableList.of(EAST.getFlow(side));
        case NORTHWEST:
          return ImmutableList.of(NORTH.getFlow(side), WEST.getFlow(side));
        case NORTHEAST:
          return ImmutableList.of(NORTH.getFlow(side), EAST.getFlow(side));
        case SOUTHWEST:
          return ImmutableList.of(SOUTH.getFlow(side), WEST.getFlow(side));
        case SOUTHEAST:
          return ImmutableList.of(SOUTH.getFlow(side), EAST.getFlow(side));
      }
      return ImmutableList.of();
    }
  }

  /**
   * Determines in what way the channel connects to other channels
   */
  public enum ChannelConnected implements IStringSerializable {
    NONE,
    INNER,
    OUTER;

    @Override
    public String getName() {
      return this.toString().toLowerCase(Locale.US);
    }
  }

  /**
   * A convince class to both store and modify the motion for channels
   */
  private static class Motion {

    public double x, y, z;

    public Motion() {
      x = 0;
      y = 0;
      z = 0;
    }

    public Motion boost(EnumFacing facing, double speed) {
      switch(facing) {
        case UP:
          this.y += speed * 3; // compensate for gravity
          break;
        case DOWN:
          this.y -= speed;
          break;
        case NORTH:
          this.z -= speed;
          break;
        case SOUTH:
          this.z += speed;
          break;
        case WEST:
          this.x -= speed;
          break;
        case EAST:
          this.x += speed;
          break;
      }
      return this;
    }
  }

  /**
   * Does all of the events used by slime channel
   */
  public static class EventHandler {

    public static final EventHandler instance = new EventHandler();

    private EventHandler() {
    }

    // stop items from despawning when inside channels
    // this won't give them a full 5 minutes upon exiting, only upon attempting to despawn
    @SubscribeEvent
    public void onItemExpire(ItemExpireEvent event) {
      EntityItem item = event.getEntityItem();
      if(item.getEntityWorld().getBlockState(item.getPosition()).getBlock() instanceof BlockSlimeChannel) {
        event.setCanceled(true);
      }
    }
  }
}