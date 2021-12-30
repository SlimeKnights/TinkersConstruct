package slimeknights.tconstruct.smeltery.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.shared.block.TableBlock;
import slimeknights.tconstruct.smeltery.block.entity.CastingBlockEntity;

public abstract class AbstractCastingBlock extends TableBlock {
  protected AbstractCastingBlock(Properties builder) {
    super(builder);
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
    return BlockEntityHelper.get(CastingBlockEntity.class, worldIn, pos).map(te -> {
      if (te.isStackInSlot(CastingBlockEntity.OUTPUT)) {
        return 15;
      }
      if (te.isStackInSlot(CastingBlockEntity.INPUT)) {
        return 1;
      }
      return 0;
    }).orElse(0);
  }
}
