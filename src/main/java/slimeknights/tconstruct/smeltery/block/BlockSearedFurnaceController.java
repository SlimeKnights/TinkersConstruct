package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.smeltery.tileentity.TileSearedFurnace;

public class BlockSearedFurnaceController extends BlockMultiblockController {

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

  @Override
  public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
    if(isActive(world, pos)) {
      EnumFacing enumfacing = state.getValue(FACING);
      double x = pos.getX() + 0.5D;
      double y = pos.getY() + 0.375D + (rand.nextFloat() * 8F) / 16F;
      double z = pos.getZ() + 0.5D;
      double frontOffset = 0.52D;
      double sideOffset = rand.nextDouble() * 0.4D - 0.2D;

      spawnFireParticles(world, enumfacing, x, y, z, frontOffset, sideOffset);
    }
  }
}
