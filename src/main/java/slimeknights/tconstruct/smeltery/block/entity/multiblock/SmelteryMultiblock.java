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
    return TinkerTags.Blocks.SMELTERY.contains(block);
  }

  @Override
  protected boolean isValidFloor(Block block) {
    return TinkerTags.Blocks.SMELTERY_FLOOR.contains(block);
  }

  @Override
  protected boolean isValidTank(Block block) {
    return TinkerTags.Blocks.SMELTERY_TANKS.contains(block);
  }

  @Override
  protected boolean isValidWall(Block block) {
    return TinkerTags.Blocks.SMELTERY_WALL.contains(block);
  }
}
