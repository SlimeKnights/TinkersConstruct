package slimeknights.tconstruct.library.tools.definition.harvest;

import lombok.RequiredArgsConstructor;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

/** Harvest logic that is effective if the tool has the correct tag */
// TODO: block predicate
@RequiredArgsConstructor
public class TagHarvestLogic implements IHarvestLogic {
  public static final RecordLoadable<TagHarvestLogic> LOADER = RecordLoadable.create(Loadables.BLOCK_TAG.requiredField("effective", h -> h.tag), TagHarvestLogic::new);

  protected final TagKey<Block> tag;

  @Override
  public boolean isEffective(IToolStackView tool, BlockState state) {
    return state.is(tag) && TierSortingRegistry.isCorrectTierForDrops(tool.getStats().get(ToolStats.HARVEST_TIER), state);
  }

  @Override
  public float getDestroySpeed(IToolStackView tool, BlockState state) {
    // destroy speed does not require right tier to boost
    return state.is(tag) ? tool.getStats().get(ToolStats.MINING_SPEED) : 1.0f;
  }

  @Override
  public IGenericLoader<? extends IHarvestLogic> getLoader() {
    return LOADER;
  }
}
