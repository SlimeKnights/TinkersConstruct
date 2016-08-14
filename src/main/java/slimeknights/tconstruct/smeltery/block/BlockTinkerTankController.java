package slimeknights.tconstruct.smeltery.block;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.smeltery.tileentity.TileTinkerTank;

public class BlockTinkerTankController extends BlockContainer {

  public static PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
  public static PropertyBool ACTIVE = PropertyBool.create("active");

  public BlockTinkerTankController() {
    super(Material.ROCK);
    this.setCreativeTab(TinkerRegistry.tabSmeltery);
    this.setHardness(3F);
    this.setResistance(20F);
    this.setSoundType(SoundType.METAL);

    this.setDefaultState(this.blockState.getBaseState().withProperty(ACTIVE, false));
  }

  @Nonnull
  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, FACING, ACTIVE);
  }

  @Nonnull
  @Override
  public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
    return new TileTinkerTank();
  }

  @Nonnull
  @Override
  public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    // active or inactive?
    return state.withProperty(ACTIVE, isActive(worldIn, pos));
  }

  public boolean isActive(IBlockAccess world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    if(te instanceof TileTinkerTank) {
      return ((TileTinkerTank) te).isActive();
    }
    return false;
  }

  // since we are not an inventory, the GUI opening code is here
  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side,
                                  float clickX, float clickY, float clickZ) {
    if(player.isSneaking()) {
      return false;
    }

    if(!world.isRemote) {
      if(!isActive(world, pos)) {
        return false;
      }

      player.openGui(TConstruct.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
    }

    return true;
  }

  @Nonnull
  @Override
  public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    // set rotation
    return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
  }

  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    // check structure
    TileEntity te = worldIn.getTileEntity(pos);
    if(te instanceof TileTinkerTank) {
      TileTinkerTank tank = (TileTinkerTank) te;
      tank.checkTankStructure();

      // adds the name from the stack
      if(stack.hasDisplayName()) {
        tank.setCustomName(stack.getDisplayName());
      }
    }

  }

  // METADATA

  @Nonnull
  @Override
  public IBlockState getStateFromMeta(int meta) {
    EnumFacing enumfacing = EnumFacing.getFront(meta);

    if(enumfacing.getAxis() == EnumFacing.Axis.Y) {
      enumfacing = EnumFacing.NORTH;
    }

    return this.getDefaultState().withProperty(FACING, enumfacing);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return (state.getValue(FACING)).getIndex();
  }

  // RENDERING

  @Nonnull
  @Override
  public EnumBlockRenderType getRenderType(IBlockState state) {
    return EnumBlockRenderType.MODEL;
  }
}
