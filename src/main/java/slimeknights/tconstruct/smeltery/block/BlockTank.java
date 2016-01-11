package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.common.PlayerHelper;
import slimeknights.tconstruct.library.utils.FluidUtil;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileTank;

public class BlockTank extends BlockEnumSmeltery<BlockTank.TankType> {

  public static final PropertyEnum<TankType> TYPE = PropertyEnum.create("type", TankType.class);
  public static final PropertyBool KNOB = PropertyBool.create("has_knob");

  public BlockTank() {
    super(TYPE, TankType.class);

    setDefaultState(this.blockState.getBaseState().withProperty(KNOB, false));
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

  @Override
  public IBlockState getStateFromMeta(int meta) {
    IBlockState state = super.getStateFromMeta(meta);
    if(meta == TankType.TANK.getMeta()) {
      state = state.withProperty(KNOB, true);
    }
    return state;
  }

  @Override
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
    //if(worldIn.isRemote) return true;
    // we do it both client and server side, because the client gets animations from this
    // todo: check if it works properly with pipes n stuff

    TileEntity te = worldIn.getTileEntity(pos);
    if(!(te instanceof IFluidHandler)) {
      return false;
    }
    IFluidHandler tank = (IFluidHandler) te;
    side = side.getOpposite();

    ItemStack stack = playerIn.getHeldItem();
    if(stack == null) {
      return false;
    }

    // do the thing with the tank and the buckets
    if(FluidUtil.interactWithTank(stack, playerIn, tank, side)) {
      return true;
    }

    // prevent interaction of the item if it's a fluidcontainer. Prevents placing liquids when interacting with the tank
    return FluidContainerRegistry.isFilledContainer(stack) || stack.getItem() instanceof IFluidContainerItem;
  }

  @Override
  public int getLightValue(IBlockAccess world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    if(!(te instanceof TileTank)) {
      return 0;
    }
    TileTank tank = (TileTank) te;
    return tank.getBrightness();
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
