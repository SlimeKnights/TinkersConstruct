package slimeknights.tconstruct.gadgets.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import slimeknights.tconstruct.tables.block.TableBlock;

import javax.annotation.Nonnull;

public class RackBlock extends TableBlock {

  protected RackBlock(Properties builder) {
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
}
