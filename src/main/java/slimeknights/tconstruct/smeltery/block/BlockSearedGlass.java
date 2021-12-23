package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Locale;

import javax.annotation.Nonnull;

import slimeknights.mantle.block.BlockConnectedTexture;
import slimeknights.mantle.block.EnumBlock;

public class BlockSearedGlass extends BlockEnumSmeltery<BlockSearedGlass.GlassType> {

  public static final PropertyEnum<GlassType> TYPE = PropertyEnum.create("type", GlassType.class);

  public BlockSearedGlass() {
    super(TYPE, GlassType.class);

    this.setDefaultState(this.blockState.getBaseState()
                                        .withProperty(BlockConnectedTexture.CONNECTED_DOWN, Boolean.FALSE)
                                        .withProperty(BlockConnectedTexture.CONNECTED_EAST, Boolean.FALSE)
                                        .withProperty(BlockConnectedTexture.CONNECTED_NORTH, Boolean.FALSE)
                                        .withProperty(BlockConnectedTexture.CONNECTED_SOUTH, Boolean.FALSE)
                                        .withProperty(BlockConnectedTexture.CONNECTED_UP, Boolean.FALSE)
                                        .withProperty(BlockConnectedTexture.CONNECTED_WEST, Boolean.FALSE));
  }

  @Nonnull
  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, TYPE, BlockConnectedTexture.CONNECTED_DOWN, BlockConnectedTexture.CONNECTED_UP, BlockConnectedTexture.CONNECTED_NORTH, BlockConnectedTexture.CONNECTED_SOUTH, BlockConnectedTexture.CONNECTED_WEST, BlockConnectedTexture.CONNECTED_EAST);
  }

  @Nonnull
  @Override
  public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess world, BlockPos position) {
    // Creates the state to use for the block. This is where we check if every side is
    // connectable or not.
    return state.withProperty(BlockConnectedTexture.CONNECTED_DOWN,  this.isSideConnectable(state, world, position, EnumFacing.DOWN))
                .withProperty(BlockConnectedTexture.CONNECTED_EAST,  this.isSideConnectable(state, world, position, EnumFacing.EAST))
                .withProperty(BlockConnectedTexture.CONNECTED_NORTH, this.isSideConnectable(state, world, position, EnumFacing.NORTH))
                .withProperty(BlockConnectedTexture.CONNECTED_SOUTH, this.isSideConnectable(state, world, position, EnumFacing.SOUTH))
                .withProperty(BlockConnectedTexture.CONNECTED_UP,    this.isSideConnectable(state, world, position, EnumFacing.UP))
                .withProperty(BlockConnectedTexture.CONNECTED_WEST,  this.isSideConnectable(state, world, position, EnumFacing.WEST));
  }

  private boolean isSideConnectable(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
    final IBlockState connected = world.getBlockState(pos.offset(side));
    return canConnect(state, connected);
  }

  private boolean canConnect(IBlockState original, IBlockState connected) {
    return connected.getBlock() == original.getBlock() && original.getPropertyKeys().contains(prop) && connected.getValue(prop) == original.getValue(prop);
  }

  @Nonnull
  @Override
  @SideOnly(Side.CLIENT)
  public BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.CUTOUT;
  }

  @Override
  public boolean isFullCube(IBlockState state) {
    return false;
  }

  @Override
  public boolean isOpaqueCube(IBlockState state) {
    return false;
  }

  // only one type, but we are forced to use an enum to extend BlockEnumSmeltery (which has all the smeltery multiblock logic)
  public enum GlassType implements IStringSerializable, EnumBlock.IEnumMeta {
    GLASS;

    public final int meta;

    GlassType() {
      meta = ordinal();
    }

    @Override
    public String getName() {
      return this.toString().toLowerCase(Locale.US);
    }

    @Override
    public int getMeta() {
      return meta;
    }
  }
}
