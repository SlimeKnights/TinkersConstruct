package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.shared.block.TableBlock;
import slimeknights.tconstruct.smeltery.tileentity.CastingTileEntity;

public abstract class AbstractCastingBlock extends TableBlock {
  protected AbstractCastingBlock(Settings builder) {
    super(builder);
  }

  @Deprecated
  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult rayTraceResult) {
    if (player.isSneaking()) {
      return ActionResult.PASS;
    }
    BlockEntity te = world.getBlockEntity(pos);
    if (te instanceof CastingTileEntity) {
      ((CastingTileEntity) te).interact(player, hand);
      return ActionResult.SUCCESS;
    }
    return super.onUse(state, world, pos, player, hand, rayTraceResult);
  }

  @Override
  protected boolean openGui(PlayerEntity playerEntity, World world, BlockPos blockPos) {
    return false;
  }

  @Override
  public boolean hasComparatorOutput(BlockState state) {
    return true;
  }

  @Override
  public int getComparatorOutput(BlockState blockState, World worldIn, BlockPos pos) {
    return TileEntityHelper.getTile(CastingTileEntity.class, worldIn, pos).map(te -> {
      if (te.isStackInSlot(CastingTileEntity.OUTPUT)) {
        return 15;
      }
      if (te.isStackInSlot(CastingTileEntity.INPUT)) {
        return 1;
      }
      return 0;
    }).orElse(0);
  }
}
