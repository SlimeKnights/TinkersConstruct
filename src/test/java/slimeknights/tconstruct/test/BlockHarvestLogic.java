package slimeknights.tconstruct.test;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.tools.definition.harvest.IHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/** Block based harvest logic implementation. Included here rather than in library as block harvest logic use is discouraged, its just easy for testing */
public record BlockHarvestLogic(Block block) implements IHarvestLogic {
  public static final RecordLoadable<BlockHarvestLogic> LOADER = RecordLoadable.create(Loadables.BLOCK.requiredField("block", BlockHarvestLogic::block), BlockHarvestLogic::new);

  @Override
  public boolean isEffective(IToolStackView tool, BlockState state) {
    return state.getBlock() == block;
  }

  @Override
  public IGenericLoader<? extends IHarvestLogic> getLoader() {
    return LOADER;
  }
}
