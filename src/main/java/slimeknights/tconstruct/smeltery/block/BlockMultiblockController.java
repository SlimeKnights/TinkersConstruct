package slimeknights.tconstruct.smeltery.block;

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
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.common.block.BlockInventoryTinkers;
import slimeknights.tconstruct.smeltery.tileentity.TileMultiblock;

public abstract class BlockMultiblockController extends BlockInventoryTinkers {

  public static PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
  public static PropertyBool ACTIVE = PropertyBool.create("active");

  protected BlockMultiblockController(Material material) {
    super(material);

    this.setDefaultState(this.blockState.getBaseState().withProperty(ACTIVE, false));
  }

  @Nonnull
  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, FACING, ACTIVE);
  }

  @Nonnull
  @Override
  public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    // active or inactive?
    if(getTile(worldIn, pos) != null) {
      return state.withProperty(ACTIVE, isActive(worldIn, pos));
    }
    return state;
  }

  protected TileMultiblock<?> getTile(IBlockAccess world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    if(te instanceof TileMultiblock) {
      return ((TileMultiblock<?>) te);
    }
    return null;
  }

  public boolean isActive(IBlockAccess world, BlockPos pos) {
    TileMultiblock<?> te = getTile(world, pos);
    return te != null && te.isActive();
  }

  @Override
  protected boolean openGui(EntityPlayer player, World world, BlockPos pos) {
    if(!isActive(world, pos)) {
      return false;
    }
    return super.openGui(player, world, pos);
  }

  @Override
  public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
    // set rotation
    return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
  }

  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    // check structure
    TileMultiblock<?> te = getTile(worldIn, pos);
    if(te != null) {
      te.checkMultiblockStructure();
    }
  }

  @Nonnull
  @Override
  public IBlockState getStateFromMeta(int meta) {
    EnumFacing enumfacing = EnumFacing.getFront(meta);

    if(enumfacing.getAxis() == EnumFacing.Axis.Y) {
      enumfacing = EnumFacing.NORTH;
    }

    return this.getDefaultState().withProperty(FACING, enumfacing);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return state.getValue(FACING).getIndex();
  }

  @Override
  public int damageDropped(IBlockState state) {
    return 0;
  }

  @Nonnull
  @Override
  public EnumBlockRenderType getRenderType(IBlockState state) {
    return EnumBlockRenderType.MODEL;
  }

  protected void spawnFireParticles(World world, EnumFacing enumfacing, double d0, double d1, double d2, double d3, double d4) {
    switch(enumfacing) {
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

  @Override
  public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
    // don't rotate, we like our tile entity data and we don't want to create an invalid structure by rotating the controller
    return false;
  }
}
