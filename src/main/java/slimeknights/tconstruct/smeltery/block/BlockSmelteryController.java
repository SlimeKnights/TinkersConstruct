package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;

public class BlockSmelteryController extends BlockContainer {

  public static PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
  public static PropertyBool ACTIVE = PropertyBool.create("active");

  public BlockSmelteryController() {
    super(Material.rock);
    this.setCreativeTab(TinkerRegistry.tabSmeltery);
    setHardness(3F);
    setResistance(20F);
    setStepSound(soundTypeMetal);

    this.setDefaultState(this.blockState.getBaseState().withProperty(ACTIVE, false));
  }

  @Override
  protected BlockState createBlockState() {
    return new BlockState(this, FACING, ACTIVE);
  }

  @Override
  public TileEntity createNewTileEntity(World worldIn, int meta) {
    return new TileSmeltery();
  }

  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    // active or inactive?
    return state.withProperty(ACTIVE, isActive(worldIn, pos));
  }

  public boolean isActive(IBlockAccess world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    if(te instanceof TileSmeltery) {
      return false; // todo
    }
    return false;
  }

  @Override
  public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    // set rotation
    return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
  }

  // METADATA

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
    return ((EnumFacing)state.getValue(FACING)).getIndex();
  }

  // RENDERING

  @Override
  public int getRenderType() {
    return 3;
  }

  @Override
  public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
    if (isActive(worldIn, pos))
    {
      EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
      double d0 = (double)pos.getX() + 0.5D;
      double d1 = (double)pos.getY() + 0.5D + (rand.nextFloat() * 6F) / 16F;
      double d2 = (double)pos.getZ() + 0.5D;
      double d3 = 0.52D;
      double d4 = rand.nextDouble() * 0.6D - 0.3D;

      switch (enumfacing)
      {
        case WEST:
          worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 - d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
          worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 - d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
          break;
        case EAST:
          worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
          worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
          break;
        case NORTH:
          worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 - d3, 0.0D, 0.0D, 0.0D);
          worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 - d3, 0.0D, 0.0D, 0.0D);
          break;
        case SOUTH:
          worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 + d3, 0.0D, 0.0D, 0.0D);
          worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 + d3, 0.0D, 0.0D, 0.0D);
      }
    }
  }
}
