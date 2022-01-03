package slimeknights.tconstruct.test;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.library.tools.definition.harvest.IHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.GenericLoaderRegistry.IGenericLoader;

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
