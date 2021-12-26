package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.shared.block.TableBlock;
import slimeknights.tconstruct.smeltery.tileentity.CastingTileEntity;

import net.minecraft.block.AbstractBlock.Properties;

public abstract class AbstractCastingBlock extends TableBlock {
  protected AbstractCastingBlock(Properties builder) {
    super(builder);
  }

  @Deprecated
  @Override
  public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
    if (player.isShiftKeyDown()) {
      return ActionResultType.PASS;
    }
    TileEntity te = world.getBlockEntity(pos);
    if (te instanceof CastingTileEntity) {
      ((CastingTileEntity) te).interact(player, hand);
      return ActionResultType.SUCCESS;
    }
    return super.use(state, world, pos, player, hand, rayTraceResult);
  }

  @Override
  protected boolean openGui(PlayerEntity playerEntity, World world, BlockPos blockPos) {
    return false;
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState state) {
    return true;
  }

  @Override
  public int getAnalogOutputSignal(BlockState blockState, World worldIn, BlockPos pos) {
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
