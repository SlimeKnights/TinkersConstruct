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
import slimeknights.tconstruct.smeltery.tileentity.AbstractCastingTileEntity;

import javax.annotation.Nonnull;

public abstract class AbstractCastingBlock extends InventoryBlock {

  protected AbstractCastingBlock(Properties builder) {
    super(builder);
  }

  @Nonnull
  @Override
  public abstract TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader);

  @Override
  public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
    if (player.isSneaking()) {
      return ActionResultType.PASS;
    }
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof AbstractCastingTileEntity) {
      ((AbstractCastingTileEntity) te).interact(player);
      return ActionResultType.SUCCESS;
    }
    return super.onBlockActivated(state, world, pos, player, hand, rayTraceResult);
  }

  @Override
  protected boolean openGui(PlayerEntity playerEntity, World world, BlockPos blockPos) {
    return false;
  }
}
