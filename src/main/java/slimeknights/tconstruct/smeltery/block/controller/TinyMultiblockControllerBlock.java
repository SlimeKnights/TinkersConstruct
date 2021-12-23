package slimeknights.tconstruct.smeltery.block.controller;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.fluid.FluidTransferUtil;
import slimeknights.tconstruct.smeltery.tileentity.ITankTileEntity;

public abstract class TinyMultiblockControllerBlock extends ControllerBlock {
  private static final ITextComponent NO_FUEL_TANK = TConstruct.makeTranslation("multiblock", "tiny.no_fuel_tank");

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
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    BlockState state = super.getStateForPlacement(context);
    if (state != null) {
      return state.with(IN_STRUCTURE, isValidFuelSource(context.getWorld().getBlockState(context.getPos().down())));
    }
    return null;
  }

  @Deprecated
  @Override
  public BlockState updatePostPlacement(BlockState state, Direction direction, BlockState neighbor, IWorld world, BlockPos pos, BlockPos neighborPos) {
    if (direction == Direction.DOWN) {
      return state.with(IN_STRUCTURE, isValidFuelSource(neighbor));
    }
    return state;
  }

  @Deprecated
  @Override
  public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
    if (FluidTransferUtil.interactWithTank(world, pos, player, hand, hit)) {
      return ActionResultType.SUCCESS;
    }
    return super.onBlockActivated(state, world, pos, player, hand, hit);
  }

  @Override
  protected boolean displayStatus(PlayerEntity player, World world, BlockPos pos, BlockState state) {
    if (!world.isRemote && !state.get(IN_STRUCTURE)) {
      player.sendStatusMessage(NO_FUEL_TANK, true);
    }
    return true;
  }


  /*
   * Comparator
   */

  @Deprecated
  @Override
  public boolean hasComparatorInputOverride(BlockState state) {
    return true;
  }

  @Deprecated
  @Override
  public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
    return ITankTileEntity.getComparatorInputOverride(worldIn, pos);
  }

}
