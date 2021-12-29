package slimeknights.tconstruct.smeltery.block.controller;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.fluid.FluidTransferUtil;
import slimeknights.tconstruct.smeltery.tileentity.ITankTileEntity;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public abstract class TinyMultiblockControllerBlock extends ControllerBlock {
  private static final Component NO_FUEL_TANK = TConstruct.makeTranslation("multiblock", "tiny.no_fuel_tank");

  protected TinyMultiblockControllerBlock(Properties builder) {
    super(builder);
  }


  /*
   * Fuel tank
   */

  /**
   * Checks if the given state is a valid melter fuel source
   * @param state  State instance
   * @return  True if its a valid fuel source
   */
  protected boolean isValidFuelSource(BlockState state) {
    return TinkerTags.Blocks.FUEL_TANKS.contains(state.getBlock());
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockState state = super.getStateForPlacement(context);
    if (state != null) {
      return state.setValue(IN_STRUCTURE, isValidFuelSource(context.getLevel().getBlockState(context.getClickedPos().below())));
    }
    return null;
  }

  @Deprecated
  @Override
  public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
    if (direction == Direction.DOWN) {
      return state.setValue(IN_STRUCTURE, isValidFuelSource(neighbor));
    }
    return state;
  }

  @Deprecated
  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    if (FluidTransferUtil.interactWithTank(world, pos, player, hand, hit)) {
      return InteractionResult.SUCCESS;
    }
    return super.use(state, world, pos, player, hand, hit);
  }

  @Override
  protected boolean displayStatus(Player player, Level world, BlockPos pos, BlockState state) {
    if (!world.isClientSide && !state.getValue(IN_STRUCTURE)) {
      player.displayClientMessage(NO_FUEL_TANK, true);
    }
    return true;
  }


  /*
   * Comparator
   */

  @Deprecated
  @Override
  public boolean hasAnalogOutputSignal(BlockState state) {
    return true;
  }

  @Deprecated
  @Override
  public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
    return ITankTileEntity.getComparatorInputOverride(worldIn, pos);
  }

}
