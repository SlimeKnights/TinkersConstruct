package slimeknights.tconstruct.tables.block.chest;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import slimeknights.tconstruct.tables.tileentity.chest.PartChestTileEntity;

import javax.annotation.Nonnull;

public class PartChestBlock extends TinkerChestBlock {

  public PartChestBlock(Properties builder) {
    super(builder);
  }

  @Nonnull
  @Override
  public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
    return new PartChestTileEntity();
  }

  @Override
  public int getGuiNumber(BlockState state) {
    return 16;
  }
}
