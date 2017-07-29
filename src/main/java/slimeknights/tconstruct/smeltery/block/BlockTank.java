package slimeknights.tconstruct.smeltery.block;

import com.google.common.collect.Lists;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.annotation.Nonnull;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.library.smeltery.IFaucetDepth;
import slimeknights.tconstruct.smeltery.tileentity.TileTank;

public class BlockTank extends BlockEnumSmeltery<BlockTank.TankType> implements IFaucetDepth {

  public static final PropertyEnum<TankType> TYPE = PropertyEnum.create("type", TankType.class);
  public static final PropertyBool KNOB = PropertyBool.create("has_knob");

  public BlockTank() {
    super(TYPE, TankType.class);

    setDefaultState(this.blockState.getBaseState().withProperty(KNOB, false));
  }

  @Nonnull
  @Override
  public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
    return new TileTank();
  }

  @Nonnull
  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, TYPE, KNOB);
  }

  @Nonnull
  @Override
  public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    boolean hasKnob = (state.getValue(TYPE)) == TankType.TANK && worldIn.isAirBlock(pos.up());
    return super.getActualState(state, worldIn, pos).withProperty(KNOB, hasKnob);
  }

  @Nonnull
  @Override
  public IBlockState getStateFromMeta(int meta) {
    IBlockState state = super.getStateFromMeta(meta);
    if(meta == TankType.TANK.getMeta()) {
      state = state.withProperty(KNOB, true);
    }
    return state;
  }

  @Override
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    TileEntity te = worldIn.getTileEntity(pos);
    if(te == null || !te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing)) {
      return false;
    }

    IFluidHandler fluidHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
    ItemStack heldItem = playerIn.getHeldItem(hand);
    if(FluidUtil.interactWithFluidHandler(playerIn, hand, fluidHandler)) {
      return true; // return true as we did something
    }

    // prevent interaction so stuff like buckets and other things don't place the liquid block
    return FluidUtil.getFluidHandler(heldItem) != null;
  }

  /* Block breaking retains the liquid */

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    TileEntity te = world.getTileEntity(pos);
    if(te instanceof TileTank && stack != null && stack.hasTagCompound()) {
      ((TileTank) te).readTankFromNBT(stack.getTagCompound());
    }
  }

  @Nonnull
  @Override
  public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {
    // standard drop logic
    List<ItemStack> ret = Lists.newArrayList();
    Random rand = world instanceof World ? ((World) world).rand : RANDOM;
    Item item = this.getItemDropped(state, rand, fortune);
    ItemStack stack = ItemStack.EMPTY;
    if(item != Items.AIR) {
      stack = new ItemStack(item, 1, this.damageDropped(state));
      ret.add(stack);
    }

    // save liquid data on the stack
    TileEntity te = world.getTileEntity(pos);
    if(te instanceof TileTank && !stack.isEmpty()) {
      if(((TileTank) te).containsFluid()) {
        NBTTagCompound tag = new NBTTagCompound();
        ((TileTank) te).writeTankToNBT(tag);
        stack.setTagCompound(tag);
      }
    }
    return ret;
  }

  // fix blockbreak logic order. Needed to have the tile entity when getting the drops

  @Override
  public boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
    // we pull up a few calls to this point in time because we still have the TE here
    // the execution otherwise is equivalent to vanilla order
    this.onBlockDestroyedByPlayer(world, pos, state);
    if(willHarvest) {
      this.harvestBlock(world, player, pos, state, world.getTileEntity(pos), player.getHeldItemMainhand());
    }

    world.setBlockToAir(pos);
    // return false to prevent the above called functions to be called again
    // side effect of this is that no xp will be dropped. but it shoudln't anyway
    return false;
  }

  /* Rendering stuff etc */

  @Override
  public int getLightValue(@Nonnull IBlockState state, IBlockAccess world, @Nonnull BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    if(!(te instanceof TileTank)) {
      return 0;
    }
    TileTank tank = (TileTank) te;
    return tank.getBrightness();
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

  @Override
  public boolean hasComparatorInputOverride(IBlockState state) {
    return true;
  }

  @Override
  public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    if(!(te instanceof TileTank)) {
      return 0;
    }

    return ((TileTank) te).comparatorStrength();
  }

  @Override
  public float getFlowDepth(World world, BlockPos pos, IBlockState state) {
    return 1;
  }

  public enum TankType implements IStringSerializable, EnumBlock.IEnumMeta {
    TANK,
    GAUGE,
    WINDOW;

    public final int meta;

    TankType() {
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
