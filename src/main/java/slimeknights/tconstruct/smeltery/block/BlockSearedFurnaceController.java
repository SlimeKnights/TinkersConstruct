package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.common.block.BlockInventoryTinkers;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.smeltery.tileentity.TileSearedFurnace;

public class BlockSearedFurnaceController extends BlockInventoryTinkers {

  public static final PropertyDirection FACING = BlockHorizontal.FACING;
  public static final PropertyBool ACTIVE = PropertyBool.create("active");

  public BlockSearedFurnaceController() {
    super(Material.ROCK);
    this.setCreativeTab(TinkerRegistry.tabSmeltery);
    this.setHardness(3F);
    this.setResistance(20F);
    this.setSoundType(SoundType.METAL);

    this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(ACTIVE, false));
  }

  @Nonnull
  @Override
  public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
    return new TileSearedFurnace();
  }

  @Override
  protected boolean openGui(EntityPlayer player, World world, BlockPos pos) {
    if(!isActive(world, pos)) {
      return false;
    }
    return super.openGui(player, world, pos);
  }

  /* Blockstate */
  @Nonnull
  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, FACING, ACTIVE);
  }

  /**
   * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
   * IBlockstate
   */
  @Nonnull
  @Override
  public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    // check for a valid structure
    TileEntity te = world.getTileEntity(pos);
    if(te instanceof TileSearedFurnace) {
      ((TileSearedFurnace) te).checkFurnaceStructure();
    }
  }

  @Nonnull
  @Override
  public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    // active or inactive?
    return state.withProperty(ACTIVE, isActive(worldIn, pos));
  }

  public boolean isActive(IBlockAccess world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    if(te instanceof TileSearedFurnace) {
      return ((TileSearedFurnace) te).isActive();
    }
    return false;
  }

  // lit furnaces produce light
  @Override
  public int getLightValue(@Nonnull IBlockState state, IBlockAccess world, @Nonnull BlockPos pos) {
    if(state.getBlock() == this && state.getActualState(world, pos).getValue(ACTIVE) == Boolean.TRUE) {
      return 15;
    }
    return super.getLightValue(state, world, pos);
  }

  /**
   * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
   * blockstate.
   */
  @Nonnull
  @Override
  public IBlockState withRotation(@Nonnull IBlockState state, Rotation rot) {
    return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
  }

  /**
   * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
   * blockstate.
   */
  @Nonnull
  @Override
  public IBlockState withMirror(@Nonnull IBlockState state, Mirror mirrorIn) {
    return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
  }

  /* Metadata */
  @Nonnull
  @Override
  public IBlockState getStateFromMeta(int meta) {
    EnumFacing enumfacing = EnumFacing.getHorizontal(meta);

    return this.getDefaultState().withProperty(FACING, enumfacing);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return state.getValue(FACING).getHorizontalIndex();
  }

  /* Rendering */
  @Nonnull
  @Override
  public EnumBlockRenderType getRenderType(IBlockState state) {
    return EnumBlockRenderType.MODEL;
  }

  @Override
  public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
    if(isActive(world, pos)) {
      EnumFacing enumfacing = state.getValue(FACING);
      double x = pos.getX() + 0.5D;
      double y = pos.getY() + 0.375D + (rand.nextFloat() * 8F) / 16F;
      double z = pos.getZ() + 0.5D;
      double frontOffset = 0.52D;
      double sideOffset = rand.nextDouble() * 0.4D - 0.2D;

      switch(enumfacing) {
        case WEST:
          world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x - frontOffset, y, z + sideOffset, 0.0D, 0.0D, 0.0D);
          world.spawnParticle(EnumParticleTypes.FLAME, x - frontOffset, y, z + sideOffset, 0.0D, 0.0D, 0.0D);
          break;
        case EAST:
          world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + frontOffset, y, z + sideOffset, 0.0D, 0.0D, 0.0D);
          world.spawnParticle(EnumParticleTypes.FLAME, x + frontOffset, y, z + sideOffset, 0.0D, 0.0D, 0.0D);
          break;
        case NORTH:
          world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + sideOffset, y, z - frontOffset, 0.0D, 0.0D, 0.0D);
          world.spawnParticle(EnumParticleTypes.FLAME, x + sideOffset, y, z - frontOffset, 0.0D, 0.0D, 0.0D);
          break;
        case SOUTH:
          world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + sideOffset, y, z + frontOffset, 0.0D, 0.0D, 0.0D);
          world.spawnParticle(EnumParticleTypes.FLAME, x + sideOffset, y, z + frontOffset, 0.0D, 0.0D, 0.0D);
      }
    }
  }
}
