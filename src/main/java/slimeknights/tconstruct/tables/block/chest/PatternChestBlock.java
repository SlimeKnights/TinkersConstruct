package slimeknights.tconstruct.tables.block.chest;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import slimeknights.tconstruct.tables.block.TableTypes;
import slimeknights.tconstruct.tables.tileentity.chest.PatternChestTileEntity;

import javax.annotation.Nonnull;

public class PatternChestBlock extends TinkerChestBlock {

  public PatternChestBlock(Properties builder) {
    super(builder);
  }

  @Nonnull
  @Override
  public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
    return new PatternChestTileEntity();
  }

  @Override
  public TableTypes getType() {
    return TableTypes.PatternChest;
  }
}
