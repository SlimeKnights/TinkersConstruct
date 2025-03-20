package slimeknights.tconstruct.smeltery.block;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.block.InventoryBlock;
import slimeknights.mantle.fluid.FluidTransferHelper;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.library.utils.NBTTags;
import slimeknights.tconstruct.smeltery.block.entity.CastingBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.CastingTankBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.CastingTankBlockEntity.ITankBlock;
import slimeknights.tconstruct.smeltery.block.entity.ITankBlockEntity;

import javax.annotation.Nullable;

public class CastingTankBlock extends InventoryBlock implements ITankBlock, EntityBlock {
  private final PushReaction pushReaction;
  public CastingTankBlock(Properties properties, PushReaction pushReaction) {
    super(properties);
    this.pushReaction = pushReaction;
  }

  public CastingTankBlock(Properties properties) {
    this(properties, PushReaction.BLOCK);
  }

  @Override
  protected boolean openGui(Player player, Level world, BlockPos pos) {
    return false;
  }

  @Override
  public PushReaction getPistonPushReaction(BlockState pState) {
    return pushReaction;
  }

  @Deprecated
  @Override
  public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
    return 1.0F;
  }

  @Override
  @Nullable
  public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
    return new CastingTankBlockEntity(pPos, pState, this);
  }

  @Deprecated
  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
//    if (FluidTransferHelper.interactWithTank(world, pos, player, hand, hit)) {  // TODO what should we do on the client side?
//      return InteractionResult.SUCCESS;
//    }

    if (player.isSuppressingBounce()) {
      return InteractionResult.PASS; // TODO do we want this logic
    }

    BlockEntity te = world.getBlockEntity(pos);
    if (te instanceof CastingTankBlockEntity) {
      ((CastingTankBlockEntity) te).interact(player, hand, hit);
      return InteractionResult.SUCCESS; // TODO we shouldn't do this if nothing happened
    }

    return super.use(state, world, pos, player, hand, hit);
  }

  @Override
  public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
    BlockEntity te = world.getBlockEntity(pos);
    if (te instanceof ITankBlockEntity) {
      FluidStack fluid = ((ITankBlockEntity) te).getTank().getFluid();
      return fluid.getFluid().getFluidType().getLightLevel(fluid);
    }
    return super.getLightEmission(state, world, pos);
  }

  @Override
  public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    CompoundTag nbt = stack.getTag();
    if (nbt != null) {
      BlockEntityHelper.get(CastingTankBlockEntity.class, worldIn, pos).ifPresent(te -> te.updateTank(nbt.getCompound(NBTTags.TANK)));
    }
    super.setPlacedBy(worldIn, pos, state, placer, stack);
  }

  @Deprecated
  @Override
  public boolean hasAnalogOutputSignal(BlockState state) {
    return true;
  }

  @Deprecated
  @Override
  public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
    return ITankBlockEntity.getComparatorInputOverride(worldIn, pos);
  }

  @Override
  public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
    ItemStack stack = new ItemStack(this);
    BlockEntityHelper.get(CastingTankBlockEntity.class, world, pos).ifPresent(te -> te.setTankTag(stack));
    return stack;
  }

  @Override
  public int getCapacity() {
    return CastingTankBlockEntity.DEFAULT_CAPACITY;
  }
}
