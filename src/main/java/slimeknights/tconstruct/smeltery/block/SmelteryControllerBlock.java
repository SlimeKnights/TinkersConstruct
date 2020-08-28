package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryTileEntity;

public class SmelteryControllerBlock extends MultiblockControllerBlock {
  public SmelteryControllerBlock(Block.Properties builder) {
    super(builder);
  }

  @Override
  public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
    return new SmelteryTileEntity();
  }
}
