package slimeknights.tconstruct.library.tools.definition.harvest;

import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.GenericLoaderRegistry;
import slimeknights.tconstruct.library.utils.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.utils.GenericLoaderRegistry.IHaveLoader;

/** Logic to determine the harvest speed of a block */
public interface IHarvestLogic extends IHaveLoader {
  /** Default harvest logic instance */
  IHarvestLogic DEFAULT = new IHarvestLogic() {
    @Override
    public boolean isEffective(IModifierToolStack tool, BlockState state) {
      return false;
    }

    @Override
    public IGenericLoader<?> getLoader() {
      throw new UnsupportedOperationException("Cannot serialize the default harvest logic");
    }
  };
  /** Harvest logic loader registry */
  GenericLoaderRegistry<IHarvestLogic> LOADER = new GenericLoaderRegistry<>(DEFAULT);

  /** Determines if the tool is effective against the given block */
  boolean isEffective(IModifierToolStack tool, BlockState state);

  /** Gets the destroy speed against the given block */
  default float getDestroySpeed(IModifierToolStack tool, BlockState state) {
    return isEffective(tool, state) ? tool.getStats().get(ToolStats.MINING_SPEED) : 1.0f;
  }
}
