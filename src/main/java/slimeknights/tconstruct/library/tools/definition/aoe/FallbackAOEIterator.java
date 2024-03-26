package slimeknights.tconstruct.library.tools.definition.aoe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Iterator that tries one iterator, falling back to a second if the block does not match a tag
 * @param tag              Tag to check the block against
 * @param taggedIterator   Iterator to use if the block matches the tag
 * @param fallbackIterator Iterator to use if the block does not match the tag
 */
// TODO: use block predicate?
public record FallbackAOEIterator(TagKey<Block> tag, IAreaOfEffectIterator taggedIterator, IAreaOfEffectIterator fallbackIterator) implements IAreaOfEffectIterator {
  public static final RecordLoadable<FallbackAOEIterator> LOADER = RecordLoadable.create(
    Loadables.BLOCK_TAG.requiredField("tag", FallbackAOEIterator::tag),
    IAreaOfEffectIterator.LOADER.requiredField("if_matches", FallbackAOEIterator::taggedIterator),
    IAreaOfEffectIterator.LOADER.requiredField("fallback", FallbackAOEIterator::fallbackIterator),
    FallbackAOEIterator::new);

  @Override
  public IGenericLoader<? extends IAreaOfEffectIterator> getLoader() {
    return LOADER;
  }

  @Override
  public Iterable<BlockPos> getBlocks(IToolStackView tool, ItemStack stack, Player player, BlockState state, Level world, BlockPos origin, Direction sideHit, AOEMatchType matchType) {
    IAreaOfEffectIterator iterator = state.is(tag) ? taggedIterator : fallbackIterator;
    return iterator.getBlocks(tool, stack, player, state, world, origin, sideHit, matchType);
  }
}
