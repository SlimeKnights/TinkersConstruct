package slimeknights.tconstruct.smeltery.block;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.shared.block.TableBlock;
import slimeknights.tconstruct.smeltery.block.entity.CastingBlockEntity;

import javax.annotation.Nullable;

public abstract class AbstractCastingBlock extends TableBlock {
  @Getter
  private final boolean requireCast;
  protected AbstractCastingBlock(Properties builder, boolean requireCast) {
    super(builder);
    this.requireCast = requireCast;
  }

  @Override
  @Deprecated
  @Nullable
  public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
    return null;
  }

  @Deprecated
  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult) {
    if (player.isShiftKeyDown()) {
      return InteractionResult.PASS;
    }
    BlockEntity te = world.getBlockEntity(pos);
    if (te instanceof CastingBlockEntity) {
      ((CastingBlockEntity) te).interact(player, hand);
      return InteractionResult.SUCCESS;
    }
    return super.use(state, world, pos, player, hand, rayTraceResult);
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
    if (worldIn.isClientSide()) {
      return;
    }
    BlockEntityHelper.get(CastingBlockEntity.class, worldIn, pos).ifPresent(casting -> casting.handleRedstone(worldIn.hasNeighborSignal(pos)));
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
    BlockEntityHelper.get(CastingBlockEntity.class, worldIn, pos).ifPresent(CastingBlockEntity::swap);
  }

  @Override
  protected boolean openGui(Player playerEntity, Level world, BlockPos blockPos) {
    return false;
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState state) {
    return true;
  }

  @Override
  public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
    return BlockEntityHelper.get(CastingBlockEntity.class, worldIn, pos).map(CastingBlockEntity::getAnalogSignal).orElse(0);
  }
}
