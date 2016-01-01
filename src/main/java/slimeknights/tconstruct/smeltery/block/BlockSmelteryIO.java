package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

import slimeknights.mantle.block.EnumBlock;

public class BlockSmelteryIO extends BlockEnumSmeltery<BlockSmelteryIO.IOType> {

  public final static PropertyEnum<IOType> TYPE = PropertyEnum.create("type", IOType.class);
  public static PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

  public BlockSmelteryIO() {
    super(TYPE, IOType.class);
  }

  @Override
  protected BlockState createBlockState() {
    return new BlockState(this, TYPE, FACING);
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    int horIndex = (meta >> 2) & 0xF;
    return this.getDefaultState().withProperty(prop, fromMeta(meta)).withProperty(FACING, EnumFacing.HORIZONTALS[horIndex]);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    // 4 direction states -> upper 2 bit for rotation
    return state.getValue(prop).getMeta() | (state.getValue(FACING).getHorizontalIndex() << 2);
  }

  // at most 4
  public enum IOType implements IStringSerializable, EnumBlock.IEnumMeta {
    DRAIN;

    public final int meta;

    IOType() {
      meta = ordinal();
    }

    @Override
    public String getName() {
      return this.toString();
    }

    @Override
    public int getMeta() {
      return meta;
    }
  }
}
