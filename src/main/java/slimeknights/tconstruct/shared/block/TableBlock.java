package slimeknights.tconstruct.shared.block;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import slimeknights.mantle.block.InventoryBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TableBlock extends InventoryBlock {

  protected TableBlock(Properties builder) {
    super(builder);
  }

  @Nonnull
  @Override
  public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
    return null;
  }

  @Override
  protected boolean openGui(PlayerEntity playerEntity, World world, BlockPos blockPos) {
    return false;
  }

  @Nonnull
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }
}
