package slimeknights.tconstruct.library.json.predicate;

import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.tconstruct.library.json.TinkerLoadables;

/** Block predicate matching anything minable by the given tier */
public record HarvestTierPredicate(Tier tier) implements BlockPredicate {
  public static final RecordLoadable<HarvestTierPredicate> LOADER = RecordLoadable.create(TinkerLoadables.TIER.requiredField("tier", HarvestTierPredicate::tier), HarvestTierPredicate::new);

  @Override
  public boolean matches(BlockState state) {
    return TierSortingRegistry.isCorrectTierForDrops(tier, state);
  }

  @Override
  public RecordLoadable<? extends IJsonPredicate<BlockState>> getLoader() {
    return LOADER;
  }
}
