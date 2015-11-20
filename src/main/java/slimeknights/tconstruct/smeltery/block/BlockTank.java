package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.smeltery.tileentity.TileTank;

public class BlockTank extends BlockEnumSmeltery<BlockTank.TankType> {

  public static final PropertyEnum TYPE = PropertyEnum.create("type", TankType.class);
  public static final PropertyBool KNOB = PropertyBool.create("has_knob");

  public BlockTank() {
    super(Material.rock, TYPE, TankType.class);
    this.setCreativeTab(TinkerRegistry.tabSmeltery);
    setHardness(3F);
    setResistance(20F);
    setStepSound(soundTypeMetal);

    setDefaultState(this.blockState.getBaseState().withProperty(KNOB, true));
  }

  @Override
  public TileEntity createNewTileEntity(World worldIn, int meta) {
    return new TileTank();
  }

  @Override
  protected BlockState createBlockState() {
    return new BlockState(this, TYPE, KNOB);
  }

  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    boolean hasKnob = (state.getValue(TYPE)) == TankType.TANK && worldIn.isAirBlock(pos.up());
    return super.getActualState(state, worldIn, pos).withProperty(KNOB, hasKnob);
  }

  @SideOnly(Side.CLIENT)
  public EnumWorldBlockLayer getBlockLayer()
  {
    return EnumWorldBlockLayer.CUTOUT;
  }

  public boolean isFullCube()
  {
    return false;
  }

  public boolean isOpaqueCube()
  {
    return false;
  }

  public enum TankType implements IStringSerializable, EnumBlock.IEnumMeta {
    TANK,
    GAUGE,
    WINDOW;

    public  final int meta;

    TankType() {
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
