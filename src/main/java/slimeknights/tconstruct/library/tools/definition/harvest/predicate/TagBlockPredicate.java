package slimeknights.tconstruct.library.tools.definition.harvest.predicate;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;

/**
 * Modifier matching a tag
 */
@RequiredArgsConstructor
public class TagBlockPredicate implements BlockPredicate {
  private final TagKey<Block> tag;

  @Override
  public boolean matches(BlockState state) {
    return state.is(tag);
  }

  @Override
  public IGenericLoader<? extends BlockPredicate> getLoader() {
    return LOADER;
  }

  /**
   * Loader for this predicate
   */
  public static final IGenericLoader<TagBlockPredicate> LOADER = new IGenericLoader<>() {
    @Override
    public TagBlockPredicate deserialize(JsonObject json) {
      return new TagBlockPredicate(TagKey.create(Registry.BLOCK_REGISTRY, JsonHelper.getResourceLocation(json, "tag")));
    }

    @Override
    public TagBlockPredicate fromNetwork(FriendlyByteBuf buffer) {
      return new TagBlockPredicate(TagKey.create(Registry.BLOCK_REGISTRY, buffer.readResourceLocation()));
    }

    @Override
    public void serialize(TagBlockPredicate object, JsonObject json) {
      json.addProperty("tag", object.tag.location().toString());
    }

    @Override
    public void toNetwork(TagBlockPredicate object, FriendlyByteBuf buffer) {
      buffer.writeResourceLocation(object.tag.location());
    }
  };
}
