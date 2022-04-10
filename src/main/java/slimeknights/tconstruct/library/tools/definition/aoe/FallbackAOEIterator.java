package slimeknights.tconstruct.library.tools.definition.aoe;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/** Iterator that tries one iterator, falling back to a second if the block does not match a tag */
@RequiredArgsConstructor
public class FallbackAOEIterator implements IAreaOfEffectIterator {
  public static final Loader LOADER = new Loader();

  /** Tag to check the block against */
  private final TagKey<Block> tag;
  /** Iterator to use if the block matches the tag */
  private final IAreaOfEffectIterator taggedIterator;
  /** Iterator to use if the block does not match the tag */
  private final IAreaOfEffectIterator fallbackIterator;

  @Override
  public IGenericLoader<? extends IAreaOfEffectIterator> getLoader() {
    return LOADER;
  }

  @Override
  public Iterable<BlockPos> getBlocks(IToolStackView tool, ItemStack stack, Player player, BlockState state, Level world, BlockPos origin, Direction sideHit, AOEMatchType matchType) {
    IAreaOfEffectIterator iterator = state.is(tag) ? taggedIterator : fallbackIterator;
    return iterator.getBlocks(tool, stack, player, state, world, origin, sideHit, matchType);
  }

  private static class Loader implements IGenericLoader<FallbackAOEIterator> {
    @Override
    public FallbackAOEIterator deserialize(JsonObject json) {
      TagKey<Block> tag = TagKey.create(Registry.BLOCK_REGISTRY, JsonHelper.getResourceLocation(json, "tag"));
      IAreaOfEffectIterator tagged = IAreaOfEffectIterator.LOADER.deserialize(GsonHelper.getAsJsonObject(json, "if_matches"));
      IAreaOfEffectIterator fallback = IAreaOfEffectIterator.LOADER.deserialize(GsonHelper.getAsJsonObject(json, "fallback"));
      return new FallbackAOEIterator(tag, tagged, fallback);
    }

    @Override
    public FallbackAOEIterator fromNetwork(FriendlyByteBuf buffer) {
      TagKey<Block> tag = TagKey.create(Registry.BLOCK_REGISTRY, buffer.readResourceLocation());
      IAreaOfEffectIterator tagged = IAreaOfEffectIterator.LOADER.fromNetwork(buffer);
      IAreaOfEffectIterator fallback = IAreaOfEffectIterator.LOADER.fromNetwork(buffer);
      return new FallbackAOEIterator(tag, tagged, fallback);
    }

    @Override
    public void serialize(FallbackAOEIterator object, JsonObject json) {
      json.addProperty("tag", object.tag.location().toString());
      json.add("if_matches", IAreaOfEffectIterator.LOADER.serialize(object.taggedIterator));
      json.add("fallback", IAreaOfEffectIterator.LOADER.serialize(object.fallbackIterator));
    }

    @Override
    public void toNetwork(FallbackAOEIterator object, FriendlyByteBuf buffer) {
      buffer.writeResourceLocation(object.tag.location());
      IAreaOfEffectIterator.LOADER.toNetwork(object.taggedIterator, buffer);
      IAreaOfEffectIterator.LOADER.toNetwork(object.fallbackIterator, buffer);
    }
  }
}
