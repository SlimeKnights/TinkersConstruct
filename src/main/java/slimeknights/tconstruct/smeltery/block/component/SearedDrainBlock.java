package slimeknights.tconstruct.smeltery.block.component;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.fluid.FluidTransferUtil;
import slimeknights.tconstruct.smeltery.tileentity.DrainTileEntity;

/** Extenson to include interaction behavior */
public class SearedDrainBlock extends OrientableSmelteryBlock {
  public SearedDrainBlock(Properties properties) {
    super(properties, DrainTileEntity::new);
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
    if (FluidTransferUtil.interactWithFluidItem(world, pos, player, hand, hit)) {
      return ActionResultType.SUCCESS;
    } else if (FluidTransferUtil.interactWithBucket(world, pos, player, hand, hit.getFace(), state.get(FACING).getOpposite())) {
      return ActionResultType.SUCCESS;
    }
    return ActionResultType.PASS;
  }
}
