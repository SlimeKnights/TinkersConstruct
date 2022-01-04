package slimeknights.tconstruct.test;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.tools.definition.harvest.IHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

@RequiredArgsConstructor
public class BlockHarvestLogic implements IHarvestLogic {
  private final Block block;

  @Override
  public boolean isEffective(IModifierToolStack tool, BlockState state) {
    return state.getBlock() == block;
  }

  @Override
  public IGenericLoader<?> getLoader() {
    throw new UnsupportedOperationException();
  }
}
