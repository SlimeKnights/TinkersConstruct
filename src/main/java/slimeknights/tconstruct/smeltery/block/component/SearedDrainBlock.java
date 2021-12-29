package slimeknights.tconstruct.smeltery.block.component;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.fluid.FluidTransferUtil;
import slimeknights.tconstruct.smeltery.tileentity.component.DrainTileEntity;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

/** Extenson to include interaction behavior */
public class SearedDrainBlock extends OrientableSmelteryBlock {
  public SearedDrainBlock(Properties properties) {
    super(properties, DrainTileEntity::new);
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    if (FluidTransferUtil.interactWithFluidItem(world, pos, player, hand, hit)) {
      return InteractionResult.SUCCESS;
    } else if (FluidTransferUtil.interactWithBucket(world, pos, player, hand, hit.getDirection(), state.getValue(FACING).getOpposite())) {
      return InteractionResult.SUCCESS;
    }
    return InteractionResult.PASS;
  }
}
