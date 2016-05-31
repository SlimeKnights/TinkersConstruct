package slimeknights.tconstruct.smeltery.block;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.common.block.BlockInventoryTinkers;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;

public class BlockSmelteryController extends BlockInventoryTinkers {

  public static PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
  public static PropertyBool ACTIVE = PropertyBool.create("active");

  public BlockSmelteryController() {
    super(Material.ROCK);
    this.setCreativeTab(TinkerRegistry.tabSmeltery);
    this.setHardness(3F);
    this.setResistance(20F);
    this.setSoundType(SoundType.METAL);

    this.setDefaultState(this.blockState.getBaseState().withProperty(ACTIVE, false));
  }

  @Nonnull
  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, FACING, ACTIVE);
  }

  @Nonnull
  @Override
  public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
    return new TileSmeltery();
  }

  @Nonnull
  @Override
  public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    // active or inactive?
    return state.withProperty(ACTIVE, isActive(worldIn, pos));
  }

  public boolean isActive(IBlockAccess world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    if(te instanceof TileSmeltery) {
      return ((TileSmeltery) te).isActive();
    }
    return false;
  }

  @Override
  protected boolean openGui(EntityPlayer player, World world, BlockPos pos) {
    if(!isActive(world, pos)) {
      return false;
    }
    return super.openGui(player, world, pos);
  }

  @Nonnull
  @Override
  public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    // set rotation
    return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
  }

  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    // check structure
    TileEntity te = worldIn.getTileEntity(pos);
    if(te instanceof TileSmeltery) {
      ((TileSmeltery) te).checkSmelteryStructure();
    }
  }

  // METADATA

  @Nonnull
  @Override
  public IBlockState getStateFromMeta(int meta) {
    EnumFacing enumfacing = EnumFacing.getFront(meta);

    if (enumfacing.getAxis() == EnumFacing.Axis.Y)
    {
      enumfacing = EnumFacing.NORTH;
    }

    return this.getDefaultState().withProperty(FACING, enumfacing);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return (state.getValue(FACING)).getIndex();
  }

  // RENDERING

  @Nonnull
  @Override
  public EnumBlockRenderType getRenderType(IBlockState state) {
    return EnumBlockRenderType.MODEL;
  }

  @Override
  public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
    if (isActive(world, pos))
    {
      EnumFacing enumfacing = state.getValue(FACING);
      double d0 = (double)pos.getX() + 0.5D;
      double d1 = (double)pos.getY() + 0.5D + (rand.nextFloat() * 6F) / 16F;
      double d2 = (double)pos.getZ() + 0.5D;
      double d3 = 0.52D;
      double d4 = rand.nextDouble() * 0.6D - 0.3D;

      switch (enumfacing)
      {
        case WEST:
          world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 - d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
          world.spawnParticle(EnumParticleTypes.FLAME, d0 - d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
          break;
        case EAST:
          world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
          world.spawnParticle(EnumParticleTypes.FLAME, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
          break;
        case NORTH:
          world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 - d3, 0.0D, 0.0D, 0.0D);
          world.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 - d3, 0.0D, 0.0D, 0.0D);
          break;
        case SOUTH:
          world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 + d3, 0.0D, 0.0D, 0.0D);
          world.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 + d3, 0.0D, 0.0D, 0.0D);
      }
    }
  }
}
