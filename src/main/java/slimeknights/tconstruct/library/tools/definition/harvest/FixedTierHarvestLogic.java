package slimeknights.tconstruct.library.tools.definition.harvest;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.HarvestTiers;

/** Harvest logic that is effective if the tool has the correct tag with a fixed tier */
// TODO: use block predicate?
public record FixedTierHarvestLogic(TagKey<Block> tag, Tier tier) implements IHarvestLogic {
  public static final RecordLoadable<FixedTierHarvestLogic> LOADER = RecordLoadable.create(
    Loadables.BLOCK_TAG.requiredField("effective", FixedTierHarvestLogic::tag),
    TinkerLoadables.TIER.requiredField("tier", FixedTierHarvestLogic::tier),
    FixedTierHarvestLogic::new);

  @Override
  public boolean isEffective(IToolStackView tool, BlockState state) {
    return state.is(tag) && TierSortingRegistry.isCorrectTierForDrops(getTier(tool), state);
  }

  @Override
  public Tier getTier(IToolStackView tool) {
    return HarvestTiers.min(this.tier, tool.getStats().get(ToolStats.HARVEST_TIER));
  }

  @Override
  public IGenericLoader<? extends IHarvestLogic> getLoader() {
    return LOADER;
  }
}
