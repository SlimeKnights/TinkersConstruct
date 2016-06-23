package slimeknights.tconstruct.gadgets.block;

import java.util.Locale;
import javax.annotation.Nonnull;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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

public class BlockSlimeChannel extends EnumBlock<SlimeType> implements ITileEntityProvider {

  public static final PropertyDirection SIDE = PropertyDirection.create("side");
  public static final PropertyDirection FACING = BlockHorizontal.FACING;
  public static final PropertyBool POWERED = PropertyBool.create("powered");
  public static final PropertyEnum<ChannelConnected> CONNECTED = PropertyEnum.create("connected", ChannelConnected.class); // stored dynamically
  public static final PropertyEnum<SlimeType> TYPE = BlockSlime.TYPE;

  public BlockSlimeChannel() {
    super(Material.CLAY, TYPE, SlimeType.class);
    this.setDefaultState(this.getBlockState().getBaseState().withProperty(TYPE, SlimeType.GREEN)
                                                            .withProperty(SIDE, EnumFacing.DOWN)
                                                            .withProperty(FACING, EnumFacing.NORTH)
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
    return new BlockStateContainer(this, TYPE, SIDE, FACING, POWERED, CONNECTED);
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

  @Override
  public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess source, BlockPos pos) {
    // connections!
    // first, try the outside, the block in front of this
    // this is checked first since a full peice is better than a partial one in the case of both
    EnumFacing side = state.getValue(SIDE);
    EnumFacing facing = state.getValue(FACING);
    IBlockState check = source.getBlockState(pos.offset(side.getOpposite()));
    if(check.getBlock() == this && check.getValue(SIDE) == getDirection(side, facing).getOpposite()) {
      return state.withProperty(CONNECTED, ChannelConnected.OUTER);
    }
    // if that does not work, try to connect to the inside, or the block behind this
    check = source.getBlockState(pos.offset(side));
    if(check.getBlock() == this && check.getValue(SIDE) == getDirection(side, facing)) {
      return state.withProperty(CONNECTED, ChannelConnected.INNER);
    }

    // if neither work, no connection
    return state.withProperty(CONNECTED, ChannelConnected.NONE);
  }

  /**
   * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
   * IBlockstate
   */
  @Nonnull
  @Override
  public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    return this.getDefaultState().withProperty(TYPE, SlimeType.fromMeta(meta))
                                 .withProperty(SIDE, side.getOpposite())
                                 .withProperty(FACING, getPlacement(side.getOpposite(), placer));  
  }
  
  /**
   * Called by ItemBlocks after a block is set in the world, to allow post-place logic
   */
  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    TileEntity te = worldIn.getTileEntity(pos);
    if(te instanceof TileSlimeChannel) {
      TileSlimeChannel channel = (TileSlimeChannel)te;
      channel.setSide(state.getValue(SIDE), false);
      channel.setFacing(state.getValue(FACING), false);
    }
  }
  
  public static EnumFacing getPlacement(EnumFacing side, EntityLivingBase placer) {
    EnumFacing facing;
    EnumFacing horizontal = placer.getHorizontalFacing();
    // if on the bottom/top, just rotate horizontally
    if(side.getAxis() == EnumFacing.Axis.Y) {
      facing = horizontal;
    }
    // if we are approaching a side from the side, just return that value as well
    else if(horizontal != side && horizontal != side.getOpposite()) {
      facing = getFacing(side, horizontal);
    }
    // for up and down, try and divide in the middle (well, a little above, but that makes it more natural)
    // basically, the look vector goes negative for down
    else if(placer.getLookVec().yCoord > -0.3) {
      facing = getFacing(side, EnumFacing.UP);
    }
    else {
      facing = getFacing(side, EnumFacing.DOWN);
    }
    
    // if sneaking, reverse direction
    if(placer.isSneaking()) {
      return facing.getOpposite();
    }
    return facing;
  }
  
  /* Item drops */
  @Override
  public int damageDropped(IBlockState state) {
      return state.getValue(TYPE).getMeta();
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

      // only apply movement if the entity is within the liquid
      double moveX = 0;
      double moveY = 0;
      double moveZ = 0;
      boolean inBounds = false;
      state = state.getActualState(world, pos); // get the connected values
      EnumFacing side = state.getValue(SIDE);
      if(entityAABB.intersectsWith(getBounds(state, world, pos).offset(pos))) {
        inBounds = true; // tell the other bounding box not to reduce gravity again
        entity.setFire(0);
        entity.fallDistance = 0;
        
        EnumFacing direction = getDirection(side, state.getValue(FACING));
        // its slimy, downward motion is reduced
        if(direction != EnumFacing.DOWN && entity.motionY < 0) {
          entity.motionY /= 2;
        }
        switch(direction) {
          case UP:
            if(item) {
              entity.onGround = false;
            }
            moveY += speed * 3; // compensate for gravity
            break;
          case DOWN:
            moveY -= speed;
            break;
          case NORTH:
            moveZ -= speed;
            break;
          case SOUTH:
            moveZ += speed;
            break;
          case WEST:
            moveX -= speed;
            break;
          case EAST:
            moveX += speed;
            break;
        }
      }
      
      // apply additional movement based on the "connected" bounding box
      ChannelConnected connected = state.getValue(CONNECTED);
      if(connected == ChannelConnected.OUTER && entityAABB.intersectsWith(getSecondaryBounds(state).offset(pos))) {
        // only run these if not already in bounds above, as that would double the effect in a single block
        if(side != EnumFacing.DOWN && !inBounds) {
          // makes the block "slimey", as in you fall through it slowly
          if(entity.motionY < 0) {
            entity.motionY /= 2;
          }
          entity.setFire(0);
          entity.fallDistance = 0;
        }
        switch(side) {
          // completeness
          case UP:
            if(item) {
              entity.onGround = false;
            }
            moveY += speed * 3; // conpensate for gravity
            break;
          case DOWN:
            moveY -= speed;
            break;
          case NORTH:
            moveZ -= speed;
            break;
          case SOUTH:
            moveZ += speed;
            break;
          case WEST:
            moveX -= speed;
            break;
          case EAST:
            moveX += speed;
            break;
        }
      }
      
      entity.addVelocity(moveX, moveY, moveZ);
    }
  }
  
  // tells the game that the entity is in water
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
    if(entityAABB.intersectsWith(getBounds(state, world, pos).offset(pos))) {
      return Boolean.TRUE;
    }
    // extra box used on sideways channels
    else if(state.getValue(CONNECTED) == ChannelConnected.OUTER
         && entityAABB.intersectsWith(getSecondaryBounds(state).offset(pos))) {
      return Boolean.TRUE;
    }
    
    return Boolean.FALSE;
  }
  
  /* Powering */
  @Override
  public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
    this.updateState(worldIn, pos, state, false);
  }
  
  /**
   * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
   * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
   * block, etc.
   */
  @Override
  public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
      this.updateState(worldIn, pos, state, true);
  }

  public void updateState(World world, BlockPos pos, IBlockState state, boolean refresh) {
    boolean flag = world.isBlockPowered(pos);
    IBlockState oldState = state;
    
    if(flag != state.getValue(POWERED).booleanValue()) {
      state = state.withProperty(POWERED, Boolean.valueOf(flag));
    }
    // this fixes a bug where on world reload the block added by the TE loses facing data on block update
    // note it is not called when the block is added since then we pull default data as the TE does not exist yet (nor have we defined it)
    TileEntity te = world.getTileEntity(pos);
    if(te instanceof TileSlimeChannel && refresh) {
      TileSlimeChannel channel = (TileSlimeChannel)te;
      state = state.withProperty(SIDE, channel.getSide());
      state = state.withProperty(FACING, channel.getFacing());
    }
    if(state != oldState) {
      world.setBlockState(pos, state);
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
    builder.put(EnumFacing.UP,    new AxisAlignedBB(0,   0.5, 0,   1,   1,   1  ));
    builder.put(EnumFacing.DOWN,  new AxisAlignedBB(0,   0,   0,   1,   0.5, 1  ));
    builder.put(EnumFacing.NORTH, new AxisAlignedBB(0,   0,   0,   1,   1,   0.5));
    builder.put(EnumFacing.SOUTH, new AxisAlignedBB(0,   0,   0.5, 1,   1,   1  ));
    builder.put(EnumFacing.WEST,  new AxisAlignedBB(0,   0,   0,   0.5, 1,   1  ));
    builder.put(EnumFacing.EAST,  new AxisAlignedBB(0.5, 0,   0,   1,   1,   1  ));
    BOUNDS = builder.build();

    builder = ImmutableMap.builder();
    builder.put(EnumFacing.NORTH, new AxisAlignedBB(0,   0, 0,   1,   0.5, 0.5));
    builder.put(EnumFacing.SOUTH, new AxisAlignedBB(0,   0, 0.5, 1,   0.5, 1  ));
    builder.put(EnumFacing.WEST,  new AxisAlignedBB(0,   0, 0,   0.5, 0.5, 1  ));
    builder.put(EnumFacing.EAST,  new AxisAlignedBB(0.5, 0, 0,   1,   0.5, 1  ));
    LOWER_BOUNDS = builder.build();

    builder = ImmutableMap.builder();
    builder.put(EnumFacing.NORTH, new AxisAlignedBB(0,   0, 0,   0.5, 1, 0.5));
    builder.put(EnumFacing.SOUTH, new AxisAlignedBB(0.5, 0, 0.5, 1,   1, 1  ));
    builder.put(EnumFacing.WEST,  new AxisAlignedBB(0,   0, 0.5, 0.5, 1, 1  ));
    builder.put(EnumFacing.EAST,  new AxisAlignedBB(0.5, 0, 0,   1,   1, 0.5));
    SIDE_BOUNDS = builder.build();

    builder = ImmutableMap.builder();
    builder.put(EnumFacing.NORTH, new AxisAlignedBB(0,   0.5, 0,   1,   1, 0.5));
    builder.put(EnumFacing.SOUTH, new AxisAlignedBB(0,   0.5, 0.5, 1,   1, 1  ));
    builder.put(EnumFacing.WEST,  new AxisAlignedBB(0,   0.5, 0,   0.5, 1, 1  ));
    builder.put(EnumFacing.EAST,  new AxisAlignedBB(0.5, 0.5, 0,   1,   1, 1  ));
    UPPER_BOUNDS = builder.build();
  }

  @Override
  public AxisAlignedBB getCollisionBoundingBox(IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos) {
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
   * @return
   */
  private AxisAlignedBB getBounds(IBlockState state, IBlockAccess source, BlockPos pos) {
    EnumFacing side = state.getValue(SIDE);
    EnumFacing facing = state.getValue(FACING);
    ChannelConnected connected = state.getValue(CONNECTED);
    if(connected == ChannelConnected.INNER) {
      if(side == EnumFacing.DOWN) {
        return LOWER_BOUNDS.get(facing);
      }
      // for completeness, just in case...
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
    EnumFacing facing = state.getValue(FACING);
    
    if(side == EnumFacing.DOWN) {
      return UPPER_BOUNDS.get(facing.getOpposite());
    }
    // again, completeness
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
  
  @SuppressWarnings("deprecation")
  @Override
  public boolean shouldSideBeRendered(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing face) {
    @SuppressWarnings("unused")
    int knightminers_sanity_percentage_after_writing_function = 15;
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
    
    // so, it matches, set up some data for readibility and send it along
    EnumFacing side = state.getValue(SIDE);
    ChannelConnected connected = state.getActualState(blockAccess, pos).getValue(CONNECTED);
    EnumFacing direction = getDirection(side, state.getValue(FACING));
    EnumFacing offsetSide = offset.getValue(SIDE);
    ChannelConnected offsetConnected = offset.getActualState(blockAccess, pos.offset(face)).getValue(CONNECTED);
    EnumFacing offsetDirection = getDirection(offsetSide, offset.getValue(FACING));

    // the other channel is against our back
    if(face == side) {
      // if its inner, we have a half face
      if(connected == ChannelConnected.INNER) {
        // so ask with the half being our direction
        return !hasHalfSide(direction, face, offsetSide, offsetDirection, offsetConnected);
      }
      // otherwise we have a full one
      return !hasFullSide(face, offsetSide, offsetDirection, offsetConnected);
    }
    
    // the other channel is "above" of ours
    if(face == side.getOpposite()) {
      // outer state has one half face
      if(connected == ChannelConnected.OUTER) {
        // though the half is opposite the direction now
        return !hasHalfSide(direction.getOpposite(), face, offsetSide, offsetDirection, offsetConnected);
      }
      // if its not the outer face, it really doesn't matter as no faces are here to hide
      return true;
    }
    
    // the other channel is in front of where we flow to
    if(face == direction) {
      // in this case, the face is always there, so just send a generic half side
      return !hasHalfSide(side, face, offsetSide, offsetDirection, offsetConnected);
    }
    
    // the other channel is behind us
    if(face == direction.getOpposite()) {
      // do we have a full face, a half one, or none?
      switch(connected) {
        // inner means no face to deal with, so back out
        case INNER:
          return true;
        // outer means a full face
        case OUTER:
          return !hasFullSide(face, offsetSide, offsetDirection, offsetConnected);
        // none means half face
        case NONE:
          return !hasHalfSide(side, face, offsetSide, offsetDirection, offsetConnected);
      }
    }
    
    // last two cases are complicated, as they both can be a quarter, half, or three quarters
    switch(connected) {
      // easiest version, means we have a half face like above
      case NONE:
        return !hasHalfSide(side, face, offsetSide, offsetDirection, offsetConnected);
        // outer means we have a stair shape with a hole opposite the side in the direction
      case OUTER:
        // if we have a full side, then definatelly
        if(hasFullSide(face, offsetSide, offsetDirection, offsetConnected)) {
          return false;
        }
        // if its the same shape, there is a possibility
        if(offsetConnected == ChannelConnected.OUTER) {
          // option one, we are the same shape
          if(offsetSide == side) {
            return offsetDirection != direction;
          }
          // option two is option one flipped twice
          if(offsetSide == direction.getOpposite()) {
            return offsetDirection != side.getOpposite();
          }
          // neither? just show it
          return true;
        }
        // both inner and none mean we cannot cull since full sides were already matched
        return true;
      // inner means we have a quarter in the front, or in the direction we go
      case INNER:
        // on any of theses three sides, if it matches a direction below it should not cull, but culls otherwise
        if(offsetSide == side || offsetSide == face.getOpposite() || offsetSide == direction) {
          if(offsetConnected == ChannelConnected.INNER
              && (offsetDirection == side.getOpposite() || offsetDirection == face || offsetDirection == direction.getOpposite())) {
            return true;
          }
          return false;
        }
        
        // the other three can only possibly connect if on the outer side
        if(offsetConnected == ChannelConnected.OUTER) {
          // since we already checked the side above, all thats left is the direction which states we should cull
          if(offsetDirection == face || offsetDirection == side.getOpposite() || offsetDirection == direction.getOpposite()) {
            return false;
          }
        }
        return true;
    }
    
    // isn't possible as connected cannot be null, but here to prevent errors should it become possible
    return true;
  }
  private static boolean hasFullSide(EnumFacing orginFace, EnumFacing side, EnumFacing direction, ChannelConnected connected) {
    // back, full unless we are the inner corner
    if(orginFace == side.getOpposite() && connected != ChannelConnected.INNER) {
      return true;
    }
    // back side face, full if we are connected
    if(orginFace == direction && connected == ChannelConnected.OUTER) {
      return true;
    }
    return false;
  }
  
  private static boolean hasHalfSide(EnumFacing orginHalf, EnumFacing orginFace, EnumFacing side, EnumFacing direction, ChannelConnected connected) {
    // if we are on the same side as the half face
    if(side == orginHalf) {
      // make sure inner connections face the same direction
      if(connected == ChannelConnected.INNER) {
        // the direction is opposite the face of the half
        return direction == orginFace.getOpposite();
      }
      // both outer and none have this face solid
      return true;
    }
    
    // pressed up against this, basically the same as above only switched half and direction
    if(side == orginFace.getOpposite()) {
      // inner connection must be on the same half as the direction its going 
      if(connected == ChannelConnected.INNER) {
        return direction == orginHalf;
      }
      // both outer and none have this face solid
      return true;
    }
    
    // opposite side of the block
    if(side == orginFace) {
      // it must face the opposite of the half and be connected outer
      return connected == ChannelConnected.OUTER && direction == orginHalf.getOpposite();
    }
    
    // there are three remaining directions, but their only chance is if they have the outer chance
    if(connected == ChannelConnected.OUTER) {
      // if its facing away, it has a full face here
      if(direction == orginFace) {
        return true;
      }
      // if the channel is opposite the half, the only valid facing is the one handled above
      if(side == orginHalf.getOpposite()) {
        return false;
      }
      // otherwise there is an additional valid facing, going the opposite direction of the half leading to "stairs"
      return direction == orginHalf.getOpposite();
    }
    
    return false;
  }
  
  /* Helper functions */
  /**
   * Determines the direction that the channel is flowing based on the side and facing
   */
  private static EnumFacing getDirection(EnumFacing side, EnumFacing facing) {
    switch(side) {
      case NORTH:
        switch(facing) {
          case NORTH:
            return EnumFacing.UP;
          case SOUTH:
            return EnumFacing.DOWN;
          case WEST:
            return EnumFacing.WEST;
          case EAST:
            return EnumFacing.EAST;
          default:
            return EnumFacing.DOWN;
        }
      case SOUTH:
        switch(facing) {
          case NORTH:
            return EnumFacing.UP;
          case SOUTH:
            return EnumFacing.DOWN;
          case WEST:
            return EnumFacing.EAST;
          case EAST:
            return EnumFacing.WEST;
          default:
            return EnumFacing.DOWN;
        }
      case WEST:
        switch(facing) {
          case NORTH:
            return EnumFacing.UP;
          case SOUTH:
            return EnumFacing.DOWN;
          case WEST:
            return EnumFacing.SOUTH;
          case EAST:
            return EnumFacing.NORTH;
          default:
            return EnumFacing.DOWN;
        }
      case EAST:
        switch(facing) {
          case NORTH:
            return EnumFacing.UP;
          case SOUTH:
            return EnumFacing.DOWN;
          case WEST:
            return EnumFacing.NORTH;
          case EAST:
            return EnumFacing.SOUTH;
          default:
            return EnumFacing.DOWN;
        }
      default:
        switch(facing) {
          case UP: case DOWN:
            return EnumFacing.NORTH;
          default:
            return facing;
        }
    }
  }
  /**
   * Determines which way the channel is facing based on the side and direction of flow
   */
  private static EnumFacing getFacing(EnumFacing side, EnumFacing direction) {
    switch(side) {
      case NORTH:
        switch(direction) {
          case UP:
            return EnumFacing.NORTH;
          case DOWN:
            return EnumFacing.SOUTH;
          case WEST:
            return EnumFacing.WEST;
          case EAST:
            return EnumFacing.EAST;
          default:
            return EnumFacing.SOUTH;
        }
      case SOUTH:
        switch(direction) {
          case UP:
            return EnumFacing.NORTH;
          case DOWN:
            return EnumFacing.SOUTH;
          case WEST:
            return EnumFacing.EAST;
          case EAST:
            return EnumFacing.WEST;
          default:
            return EnumFacing.SOUTH;
        }
      case WEST:
        switch(direction) {
          case UP:
            return EnumFacing.NORTH;
          case DOWN:
            return EnumFacing.SOUTH;
          case NORTH:
            return EnumFacing.EAST;
          case SOUTH:
            return EnumFacing.WEST;
          default:
            return EnumFacing.SOUTH;
        }
      case EAST:
        switch(direction) {
          case UP:
            return EnumFacing.NORTH;
          case DOWN:
            return EnumFacing.SOUTH;
          case NORTH:
            return EnumFacing.WEST;
          case SOUTH:
            return EnumFacing.EAST;
          default:
            return EnumFacing.SOUTH;
        }
      default:
        switch(direction) {
          case UP: case DOWN:
            return EnumFacing.NORTH;
          default:
            return direction;
        }
    }
  }

  public enum ChannelConnected implements IStringSerializable {
    NONE,
    INNER,
    OUTER;
    
    @Override
    public String getName() {
      return this.toString().toLowerCase(Locale.US);
    }
  }

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