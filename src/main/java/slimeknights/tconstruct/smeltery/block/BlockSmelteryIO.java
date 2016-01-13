package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.common.PlayerHelper;
import slimeknights.tconstruct.library.utils.FluidUtil;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileDrain;

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

  @Override
  public int damageDropped(IBlockState state) {
    return state.getValue(prop).getMeta(); // no rotation in the dropped drain
  }

  @Override
  public TileEntity createNewTileEntity(World worldIn, int meta) {
    return new TileDrain();
  }

  @Override
  public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    EnumFacing side = facing;
    if(side == EnumFacing.UP || side == EnumFacing.DOWN) {
      side = placer.getHorizontalFacing().getOpposite();
    }
    // set rotation
    return this.getDefaultState().withProperty(FACING, side);
  }

  @Override
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
    // we allow to insert buckets into the smeltery
    TileEntity te = worldIn.getTileEntity(pos);
    if(!(te instanceof IFluidHandler)) {
      return false;
    }
    IFluidHandler tank = (IFluidHandler) te;
    side = side.getOpposite();

    ItemStack stack = player.getHeldItem();
    if(stack == null) {
      return false;
    }

    // regular bucket
    ItemStack result = FluidUtil.tryEmptyBucket(stack, tank, side);
    if(result != null) {
      if(!player.capabilities.isCreativeMode) {
        player.inventory.decrStackSize(player.inventory.currentItem, 1);
        PlayerHelper.spawnItemAtPlayer(player, result);
      }
      return true;
    }

    // universal bucket
    ItemStack copy = stack.copy();
    if(FluidUtil.tryEmptyFluidContainerItem(stack, tank, side, player)) {
      if(player.capabilities.isCreativeMode) {
        // reset the stack that got modified
        player.inventory.setInventorySlotContents(player.inventory.currentItem, copy);
      }
      return true;
    }


    // prevent interaction of the item if it's a fluidcontainer. Prevents placing liquids when interacting with the tank
    return FluidContainerRegistry.isFilledContainer(stack) || stack.getItem() instanceof IFluidContainerItem;
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
