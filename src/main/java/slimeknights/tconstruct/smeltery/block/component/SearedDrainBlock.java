package slimeknights.tconstruct.smeltery.block.component;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import slimeknights.tconstruct.smeltery.tileentity.DrainTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.FaucetTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.ITankTileEntity;

/**
 * Extension to include interaction behavior
 */
public class SearedDrainBlock extends SmelteryIOBlock{

  public SearedDrainBlock(Settings properties) {
    super(properties, DrainTileEntity::new);
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    // success if the item is a fluid handler, regardless of if fluid moved
    Direction face = hit.getSide();
    if (ITankTileEntity.interactWithBucket(world, pos, player, hand, face, state.get(FACING).getOpposite())) {
      return ActionResult.SUCCESS;
    }
    return ActionResult.PASS;
  }

  @Override
  public BlockEntity createBlockEntity(BlockView world) {
    return new DrainTileEntity();
  }
}
