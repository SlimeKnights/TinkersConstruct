package slimeknights.tconstruct.tables.block.chest;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import slimeknights.tconstruct.tables.block.TableTypes;
import slimeknights.tconstruct.tables.tileentity.chest.ModifierChestTileEntity;

import javax.annotation.Nonnull;

public class ModifierChestBlock extends TinkerChestBlock {

  public ModifierChestBlock(Properties builder) {
    super(builder);
  }

  @Nonnull
  @Override
  public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
    return new ModifierChestTileEntity();
  }

  @Override
  public TableTypes getType() {
    return TableTypes.ModifierChest;
  }
}
