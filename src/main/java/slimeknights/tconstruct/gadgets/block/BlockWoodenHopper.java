package slimeknights.tconstruct.gadgets.block;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import slimeknights.tconstruct.gadgets.tileentity.TileWoodenHopper;

import static net.minecraft.block.BlockHopper.FACING;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class BlockWoodenHopper extends BlockContainer {
  // Quitely stolen from RWTemas Diet Hoppers
  private static final EnumMap<EnumFacing, List<AxisAlignedBB>> bounds;

  static {
    List<AxisAlignedBB> commonBounds = ImmutableList.of(
        makeAABB(0, 10, 0, 16, 16, 16),
        makeAABB(4, 4, 4, 12, 10, 12)
    );
    bounds = Stream.of(EnumFacing.values())
                   .filter(t -> t != EnumFacing.UP)
                   .collect(Collectors.toMap(a -> a, a -> new ArrayList<>(commonBounds), (u, v) -> {
                     throw new IllegalStateException();
                   }, () -> new EnumMap<>(EnumFacing.class)));

    bounds.get(EnumFacing.DOWN).add(makeAABB(6, 0, 6, 10, 4, 10));

    bounds.get(EnumFacing.NORTH).add(makeAABB(6, 4, 0, 10, 8, 4));
    bounds.get(EnumFacing.SOUTH).add(makeAABB(6, 4, 12, 10, 8, 16));

    bounds.get(EnumFacing.WEST).add(makeAABB(0, 4, 6, 4, 8, 10));
    bounds.get(EnumFacing.EAST).add(makeAABB(12, 4, 6, 16, 8, 10));
  }

  public BlockWoodenHopper() {
    super(Material.WOOD, MapColor.STONE);
    this.setHardness(3.0F);
    this.setResistance(8.0F);
    this.setSoundType(SoundType.WOOD);
    this.setCreativeTab(CreativeTabs.REDSTONE);

    this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.DOWN));
  }

  private static AxisAlignedBB makeAABB(int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
    return new AxisAlignedBB(fromX / 16F, fromY / 16F, fromZ / 16F, toX / 16F, toY / 16F, toZ / 16F);
  }

  @SuppressWarnings("deprecation")
  @Override
  public RayTraceResult collisionRayTrace(IBlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {
    return bounds.get(blockState.getValue(FACING)).stream()
                 .map(bb -> rayTrace(pos, start, end, bb))
                 .anyMatch(Objects::nonNull)
           ? super.collisionRayTrace(blockState, worldIn, pos, start, end) : null;
  }

  @Override
  public TileEntity createNewTileEntity(World worldIn, int meta) {
    return new TileWoodenHopper();
  }

  @Override
  public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
    // no redstone
  }

  @Override
  public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
    // no redstone
  }

  public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
  {
    EnumFacing enumfacing = facing.getOpposite();

    if (enumfacing == EnumFacing.UP)
    {
      enumfacing = EnumFacing.DOWN;
    }

    return this.getDefaultState().withProperty(FACING, enumfacing);
  }

  public IBlockState getStateFromMeta(int meta)
  {
    EnumFacing facing = getFacing(meta);
    if(facing == EnumFacing.UP) {
      facing = EnumFacing.DOWN;
    }
    return this.getDefaultState().withProperty(FACING, facing);
  }

  public int getMetaFromState(IBlockState state)
  {
    int i = 0;
    i = i | state.getValue(FACING).getIndex();

    return i;
  }

  protected BlockStateContainer createBlockState()
  {
    return new BlockStateContainer(this, FACING);
  }

  // unchanged copied hopper code
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
  {
    return FULL_BLOCK_AABB;
  }

  protected static final AxisAlignedBB BASE_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D);
  protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
  protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);
  protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
  protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);

  public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_)
  {
    addCollisionBoxToList(pos, entityBox, collidingBoxes, BASE_AABB);
    addCollisionBoxToList(pos, entityBox, collidingBoxes, EAST_AABB);
    addCollisionBoxToList(pos, entityBox, collidingBoxes, WEST_AABB);
    addCollisionBoxToList(pos, entityBox, collidingBoxes, SOUTH_AABB);
    addCollisionBoxToList(pos, entityBox, collidingBoxes, NORTH_AABB);
  }

  /**
   * Called by ItemBlocks after a block is set in the world, to allow post-place logic
   */
  public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
  {
    super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

    if (stack.hasDisplayName())
    {
      TileEntity tileentity = worldIn.getTileEntity(pos);

      if (tileentity instanceof TileEntityHopper)
      {
        ((TileEntityHopper)tileentity).setCustomName(stack.getDisplayName());
      }
    }
  }

  /**
   * Determines if the block is solid enough on the top side to support other blocks, like redstone components.
   */
  public boolean isTopSolid(IBlockState state)
  {
    return true;
  }

  /**
   * Called when the block is right clicked by a player.
   */
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
  {
    if (worldIn.isRemote)
    {
      return true;
    }
    else
    {
      TileEntity tileentity = worldIn.getTileEntity(pos);

      if (tileentity instanceof TileEntityHopper)
      {
        playerIn.displayGUIChest((TileEntityHopper)tileentity);
        playerIn.addStat(StatList.HOPPER_INSPECTED);
      }

      return true;
    }
  }

  /**
   * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
   */
  public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
  {
    TileEntity tileentity = worldIn.getTileEntity(pos);

    if (tileentity instanceof TileEntityHopper)
    {
      InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityHopper)tileentity);
      worldIn.updateComparatorOutputLevel(pos, this);
    }

    super.breakBlock(worldIn, pos, state);
  }

  /**
   * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
   * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
   */
  public EnumBlockRenderType getRenderType(IBlockState state)
  {
    return EnumBlockRenderType.MODEL;
  }

  public boolean isFullCube(IBlockState state)
  {
    return false;
  }

  /**
   * Used to determine ambient occlusion and culling when rebuilding chunks for render
   */
  public boolean isOpaqueCube(IBlockState state)
  {
    return false;
  }

  public static EnumFacing getFacing(int meta)
  {
    return EnumFacing.getFront(meta & 7);
  }

  @SideOnly(Side.CLIENT)
  public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
  {
    return true;
  }

  public boolean hasComparatorInputOverride(IBlockState state)
  {
    return true;
  }

  public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos)
  {
    return Container.calcRedstone(worldIn.getTileEntity(pos));
  }

  @SideOnly(Side.CLIENT)
  public BlockRenderLayer getBlockLayer()
  {
    return BlockRenderLayer.CUTOUT_MIPPED;
  }


  /**
   * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
   * blockstate.
   */
  public IBlockState withRotation(IBlockState state, Rotation rot)
  {
    return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
  }

  /**
   * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
   * blockstate.
   */
  public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
  {
    return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
  }

  /**
   * Get the geometry of the queried face at the given position and state. This is used to decide whether things like
   * buttons are allowed to be placed on the face, or how glass panes connect to the face, among other things.
   * <p>
   * Common values are {@code SOLID}, which is the default, and {@code UNDEFINED}, which represents something that
   * does not fit the other descriptions and will generally cause other things not to connect to the face.
   *
   * @return an approximation of the form of the given face
   */
  public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
  {
    return face == EnumFacing.UP ? BlockFaceShape.BOWL : BlockFaceShape.UNDEFINED;
  }
}
