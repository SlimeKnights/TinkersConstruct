package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import slimeknights.mantle.block.InventoryBlock;
import slimeknights.tconstruct.library.smeltery.IFaucetDepth;
import slimeknights.tconstruct.smeltery.tileentity.CastingTileEntity;

import javax.annotation.Nonnull;

public class CastingBlock extends InventoryBlock implements IFaucetDepth {

  protected CastingBlock(Properties builder) {
    super(builder);
  }

  @Nonnull
  @Override
  public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
    return null;
  }

  @Override
  public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
    if (player.isSneaking()) {
      return ActionResultType.PASS;
    }
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof CastingTileEntity) {
      ((CastingTileEntity) te).interact(player);
      return ActionResultType.SUCCESS;
    }
    return super.onBlockActivated(state, world, pos, player, hand, rayTraceResult);
  }

  @Override
  protected boolean openGui(PlayerEntity playerEntity, World world, BlockPos blockPos) {
    return false;
  }
}
