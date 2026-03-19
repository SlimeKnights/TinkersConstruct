package slimeknights.tconstruct.smeltery.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.block.GaugeBlock;
import slimeknights.tconstruct.smeltery.block.entity.GaugeBlockEntity;

public class RenderingGaugeBlock extends GaugeBlock implements EntityBlock {
  public RenderingGaugeBlock(Properties builder) {
    super(builder);
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new GaugeBlockEntity(pos, state);
  }
}
