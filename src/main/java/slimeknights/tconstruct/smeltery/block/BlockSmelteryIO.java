package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.Locale;

import javax.annotation.Nonnull;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.smeltery.tileentity.TileDrain;

public class BlockSmelteryIO extends BlockEnumSmeltery<BlockSmelteryIO.IOType> {

  public final static PropertyEnum<IOType> TYPE = PropertyEnum.create("type", IOType.class);
  public static PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

  public BlockSmelteryIO() {
    super(TYPE, IOType.class);
  }

  @Nonnull
  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, TYPE, FACING);
  }

  @Nonnull
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

  @Override
  public int damageDropped(IBlockState state) {
    return state.getValue(prop).getMeta(); // no rotation in the dropped drain
  }

  @Nonnull
  @Override
  public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
    return new TileDrain();
  }

  @Override
  public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
    EnumFacing side = placer.getHorizontalFacing().getOpposite();
    // set rotation
    return this.getDefaultState().withProperty(FACING, side);
  }

  @Override
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    // we allow to insert buckets into the smeltery
    IFluidHandler fluidHandler = FluidUtil.getFluidHandler(worldIn, pos, null);
    if(fluidHandler == null) {
      return false;
    }

    ItemStack heldItem = playerIn.getHeldItem(hand);
    IItemHandler playerInventory = playerIn.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
    FluidActionResult result = FluidUtil.tryEmptyContainerAndStow(heldItem, fluidHandler, playerInventory, Fluid.BUCKET_VOLUME, playerIn);
    if(result.isSuccess()) {
      playerIn.setHeldItem(hand, result.getResult());
      return true;
    }

    // return true if it's a fluid handler to prevent in world interaction of the fluidhandler (bucket places liquids)
    return FluidUtil.getFluidHandler(heldItem) != null;
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
      return this.toString().toLowerCase(Locale.US);
    }

    @Override
    public int getMeta() {
      return meta;
    }
  }
}
