package slimeknights.tconstruct.smeltery.block;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.library.utils.NBTTags;
import slimeknights.tconstruct.smeltery.block.entity.ITankBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.LanternBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.component.TankBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.component.TankBlockEntity.ITankBlock;

import javax.annotation.Nullable;

public class SearedLanternBlock extends LanternBlock implements ITankBlock, EntityBlock {
  @Getter
  private final int capacity;
  public SearedLanternBlock(Properties properties, int capacity) {
    super(properties);
    this.capacity = capacity;
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new LanternBlockEntity(pos, state, this);
  }

  @Override
  public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
    BlockEntity te = world.getBlockEntity(pos);
    if (te instanceof TankBlockEntity) {
      FluidStack fluid = ((TankBlockEntity) te).getTank().getFluid();
      return fluid.getFluid().getAttributes().getLuminosity(fluid);
    }
    return 0;
  }

  @Override
  public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    CompoundTag nbt = stack.getTag();
    if (nbt != null) {
      BlockEntityHelper.get(TankBlockEntity.class, worldIn, pos).ifPresent(te -> te.updateTank(nbt.getCompound(NBTTags.TANK)));
    }
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public boolean hasAnalogOutputSignal(BlockState state) {
    return true;
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
    return ITankBlockEntity.getComparatorInputOverride(worldIn, pos);
  }

  @Override
  public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
    ItemStack stack = new ItemStack(this);
    BlockEntityHelper.get(TankBlockEntity.class, world, pos).ifPresent(te -> te.setTankTag(stack));
    return stack;
  }
}
