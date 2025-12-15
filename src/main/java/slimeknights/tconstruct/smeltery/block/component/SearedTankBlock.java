package slimeknights.tconstruct.smeltery.block.component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.fluid.FluidTransferHelper;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.utils.NBTTags;
import slimeknights.tconstruct.smeltery.block.entity.ITankBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.component.TankBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.component.TankBlockEntity.ITankBlock;
import slimeknights.tconstruct.smeltery.item.TankItem;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.function.ToIntFunction;

public class SearedTankBlock extends SearedBlock implements ITankBlock, EntityBlock {
  public static final IntegerProperty LIGHT = IntegerProperty.create("light", 0, 15);
  public static final ToIntFunction<BlockState> LIGHT_GETTER = state -> state.getValue(SearedTankBlock.LIGHT);

  @Getter
  private final int capacity;
  private final PushReaction pushReaction;
  public SearedTankBlock(Properties properties, int capacity, PushReaction pushReaction) {
    super(properties, true);
    this.capacity = capacity;
    this.pushReaction = pushReaction;
    registerDefaultState(defaultBlockState().setValue(LIGHT, 0));
  }

  public SearedTankBlock(Properties properties, int capacity) {
    this(properties, capacity, PushReaction.BLOCK);
  }

  @Override
  protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(LIGHT);
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
    return new TankBlockEntity(pPos, pState, this);
  }

  @Deprecated
  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    if (FluidTransferHelper.interactWithTank(world, pos, player, hand, hit)) {
      return InteractionResult.SUCCESS;
    }
    return super.use(state, world, pos, player, hand, hit);
  }

  /** Helper for setting the light level on placement */
  public static BlockState setLightLevel(BlockState state, BlockPlaceContext context) {
    ItemStack stack = context.getItemInHand();
    FluidStack fluid = TankItem.getTank(stack, 1).getFluid();
    if (!fluid.isEmpty()) {
      state = state.setValue(LIGHT, fluid.getFluid().getFluidType().getLightLevel(fluid));
    }
    return state;
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return setLightLevel(this.defaultBlockState(), context);
  }

  @Override
  public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    CompoundTag nbt = stack.getTag();
    if (nbt != null && world.getBlockEntity(pos) instanceof TankBlockEntity tank) {
      tank.updateTank(nbt.getCompound(NBTTags.TANK));
    }
    super.setPlacedBy(world, pos, state, placer, stack);
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
    BlockEntityHelper.get(TankBlockEntity.class, world, pos).ifPresent(te -> te.setTankTag(stack));
    return stack;
  }

  @AllArgsConstructor
  public enum TankType implements StringRepresentable {
    FUEL_TANK(TankBlockEntity.DEFAULT_CAPACITY),
    FUEL_GAUGE(TankBlockEntity.DEFAULT_CAPACITY),
    INGOT_TANK(FluidValues.INGOT * 48),
    INGOT_GAUGE(FluidValues.INGOT * 48);

    @Getter
    private final int capacity;

    @Override
    public String getSerializedName() {
      return this.toString().toLowerCase(Locale.US);
    }
  }
}
