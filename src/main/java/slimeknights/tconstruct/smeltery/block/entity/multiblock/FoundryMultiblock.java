package slimeknights.tconstruct.smeltery.block.entity.multiblock;

import net.minecraft.world.level.block.Block;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.smeltery.block.entity.controller.FoundryBlockEntity;

public class FoundryMultiblock extends HeatingStructureMultiblock<FoundryBlockEntity> {
  public FoundryMultiblock(FoundryBlockEntity foundry) {
    super(foundry, true, true, false);
  }

  @Override
  protected boolean isValidBlock(Block block) {
    return TinkerTags.Blocks.FOUNDRY.contains(block);
  }

  @Override
  protected boolean isValidFloor(Block block) {
    return TinkerTags.Blocks.FOUNDRY_FLOOR.contains(block);
  }

  @Override
  protected boolean isValidTank(Block block) {
    return TinkerTags.Blocks.FOUNDRY_TANKS.contains(block);
  }

  @Override
  protected boolean isValidWall(Block block) {
    return TinkerTags.Blocks.FOUNDRY_WALL.contains(block);
  }
}
