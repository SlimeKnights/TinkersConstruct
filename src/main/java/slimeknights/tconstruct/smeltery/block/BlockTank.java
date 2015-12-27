package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.material.Material;
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
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.common.PlayerHelper;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileTank;

public class BlockTank extends BlockEnumSmeltery<BlockTank.TankType> {

  public static final PropertyEnum<TankType> TYPE = PropertyEnum.create("type", TankType.class);
  public static final PropertyBool KNOB = PropertyBool.create("has_knob");

  public BlockTank() {
    super(Material.rock, TYPE, TankType.class);
    this.setCreativeTab(TinkerRegistry.tabSmeltery);
    setHardness(3F);
    setResistance(20F);
    setStepSound(soundTypeMetal);

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
    FluidTankInfo[] info = tank.getTankInfo(side);
    FluidStack inTank = null;
    int capacityLeft = 0;
    if(info.length > 0) {
      inTank = info[0].fluid;
      capacityLeft = info[0].capacity;
      if(inTank != null) {
        capacityLeft -= inTank.amount;
      }
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
      else if(stack.getItem() == Items.bucket) {
        // replace the input bucket with an empty universal bucket
        stack = new ItemStack(TinkerSmeltery.bucket);
      }
    }
    // filled bucket?
    else if(FluidContainerRegistry.isFilledContainer(stack)) {
      FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(stack);
      if(tank.canFill(side, liquid.getFluid())) {
        // how much can we put into the tank?
        int amount = tank.fill(side, liquid, false);
        // not everything?
        if(amount == liquid.amount) {
          tank.fill(side, liquid, true);
          result = FluidContainerRegistry.drainFluidContainer(stack);
        }
      }
      else {
        // prevent placing liquids
        return true;
      }
    }

    // filled fluid container?
    if(result == null && stack.getItem() instanceof IFluidContainerItem) {
      IFluidContainerItem fluidContainer = (IFluidContainerItem) stack.getItem();

      // empty container
      if(fluidContainer.getFluid(stack) == null) {
        FluidStack liquid = tank.drain(side, fluidContainer.getCapacity(stack), false);
        if(liquid != null && liquid.amount > 0) {
          ItemStack toFill = stack.copy();
          toFill.stackSize = 1;

          int filled = fluidContainer.fill(toFill, liquid, true);
          tank.drain(side, filled, true);

          // only 1 item that got filled, replace it
          if(stack.stackSize == 1) {
            result = toFill;
          }
          else {
            // had multiple empty items, drop it at the player and keep the decreased stack
            PlayerHelper.spawnItemAtPlayer(playerIn, toFill);
            stack.stackSize--;
            result = stack;
          }
        }
      }
      // filled container
      else {
        // try draining
        FluidStack drained = fluidContainer.drain(stack, capacityLeft, false);
        if(drained != null && drained.amount <= capacityLeft) {
          // do the drain
          int amount = tank.fill(side, drained, true);
          if(amount > 0) {
            fluidContainer.drain(stack, amount, true);
            // emptying the container should be handled by .drain
            result = stack;
          }
          else {
            // prevent interaction
            return true;
          }
        }
      }
    }

    if(result != null) {
      if(!playerIn.capabilities.isCreativeMode) {
        playerIn.inventory.decrStackSize(playerIn.inventory.currentItem, 1);
        PlayerHelper.spawnItemAtPlayer(playerIn, result);
      }
      return true;
    }

    return false;
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
