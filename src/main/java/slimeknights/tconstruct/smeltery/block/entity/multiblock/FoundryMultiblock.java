package slimeknights.tconstruct.smeltery.block.entity.multiblock;

import net.minecraft.world.level.block.Block;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.smeltery.block.entity.controller.FoundryBlockEntity;

public class FoundryMultiblock extends HeatingStructureMultiblock<FoundryBlockEntity> {
  public FoundryMultiblock(FoundryBlockEntity foundry) {
    super(foundry, true, true, false);
  }

  @SuppressWarnings("deprecation")
  @Override
  protected boolean isValidBlock(Block block) {
    return block.builtInRegistryHolder().is(TinkerTags.Blocks.FOUNDRY);
  }

  @SuppressWarnings("deprecation")
  @Override
  protected boolean isValidFloor(Block block) {
    return block.builtInRegistryHolder().is(TinkerTags.Blocks.FOUNDRY_FLOOR);
  }

  @SuppressWarnings("deprecation")
  @Override
  protected boolean isValidTank(Block block) {
    return block.builtInRegistryHolder().is(TinkerTags.Blocks.FOUNDRY_TANKS);
  }

  @SuppressWarnings("deprecation")
  @Override
  protected boolean isValidWall(Block block) {
    return block.builtInRegistryHolder().is(TinkerTags.Blocks.FOUNDRY_WALL);
  }
}
