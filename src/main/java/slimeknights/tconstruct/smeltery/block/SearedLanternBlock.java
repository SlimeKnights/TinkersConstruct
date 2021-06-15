package slimeknights.tconstruct.smeltery.block;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.LanternBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.library.utils.NBTTags;
import slimeknights.tconstruct.smeltery.tileentity.ITankTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.LanternTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.TankTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.TankTileEntity.ITankBlock;

import javax.annotation.Nullable;

public class SearedLanternBlock extends LanternBlock implements ITankBlock {
  @Getter
  private final int capacity;
  public SearedLanternBlock(Properties properties, int capacity) {
    super(properties);
    this.capacity = capacity;
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader worldIn) {
    return new LanternTileEntity(this);
  }

  @Override
  public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof TankTileEntity) {
      FluidStack fluid = ((TankTileEntity) te).getTank().getFluid();
      return fluid.getFluid().getAttributes().getLuminosity(fluid);
    }
    return 0;
  }

  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    CompoundNBT nbt = stack.getTag();
    if (nbt != null) {
      TileEntityHelper.getTile(TankTileEntity.class, worldIn, pos).ifPresent(te -> te.updateTank(nbt.getCompound(NBTTags.TANK)));
    }
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public boolean hasComparatorInputOverride(BlockState state) {
    return true;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
    return ITankTileEntity.getComparatorInputOverride(worldIn, pos);
  }

  @Override
  public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
    ItemStack stack = new ItemStack(this);
    TileEntityHelper.getTile(TankTileEntity.class, world, pos).ifPresent(te -> te.setTankTag(stack));
    return stack;
  }
}
