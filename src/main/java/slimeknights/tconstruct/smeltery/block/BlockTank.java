package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.common.PlayerHelper;
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

  @Override
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
    if(worldIn.isRemote) return true;

    TileEntity te = worldIn.getTileEntity(pos);
    if(!(te instanceof IFluidHandler)) {
      return false;
    }
    IFluidHandler tank = (IFluidHandler) te;
    FluidTankInfo[] info = tank.getTankInfo(side);
    FluidStack inTank = null;
    if(info.length > 0) {
      inTank = info[0].fluid;
    }

    side = side.getOpposite();

    ItemStack stack = playerIn.getHeldItem();
    ItemStack result = null;
    // empty bucket?
    if(FluidContainerRegistry.isEmptyContainer(stack)) {
      FluidStack liquid = tank.drain(side, FluidContainerRegistry.getContainerCapacity(inTank, stack), false);
      if(liquid != null && liquid.amount > 0) {
        tank.drain(side, FluidContainerRegistry.getContainerCapacity(liquid, stack), true);
        result = FluidContainerRegistry.fillFluidContainer(liquid, stack);
      }
    }
    // filled bucket?
    else if(FluidContainerRegistry.isFilledContainer(stack)) {
      FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(stack);
      if(tank.canFill(side, liquid.getFluid())) {
        // how much can we put into the tank?
        int amount = tank.fill(side, liquid, false);
        // not everything?
        /*if(amount < liquid.amount) {
          // can we drain the container partially?
          ItemStack empty = FluidContainerRegistry.drainFluidContainer();
          FluidContainerRegistry.fillFluidContainer()
        }*/
        if(amount == liquid.amount) {
          tank.fill(side, liquid, true);
          result = FluidContainerRegistry.drainFluidContainer(stack);
        }
      }
    }

    if(result != null) {
      playerIn.inventory.decrStackSize(playerIn.inventory.currentItem, 1);
      PlayerHelper.spawnItemAtPlayer(playerIn, result);
      return true;
    }

    return false;
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
