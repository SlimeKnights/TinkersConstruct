package slimeknights.tconstruct.smeltery.block.entity.multiblock;

import net.minecraft.world.level.block.Block;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.smeltery.block.entity.controller.SmelteryBlockEntity;

public class SmelteryMultiblock extends HeatingStructureMultiblock<SmelteryBlockEntity> {
  public SmelteryMultiblock(SmelteryBlockEntity smeltery) {
    super(smeltery, true, false, false);
  }

  @Override
  protected boolean isValidBlock(Block block) {
    return block.builtInRegistryHolder().is(TinkerTags.Blocks.SMELTERY);
  }

  @Override
  protected boolean isValidFloor(Block block) {
    return block.builtInRegistryHolder().is(TinkerTags.Blocks.SMELTERY_FLOOR);
  }

  @Override
  protected boolean isValidTank(Block block) {
    return block.builtInRegistryHolder().is(TinkerTags.Blocks.SMELTERY_TANKS);
  }

  @Override
  protected boolean isValidWall(Block block) {
    return block.builtInRegistryHolder().is(TinkerTags.Blocks.SMELTERY_WALL);
  }
}
