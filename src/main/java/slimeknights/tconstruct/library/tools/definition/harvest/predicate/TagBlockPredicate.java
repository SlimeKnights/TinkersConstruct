package slimeknights.tconstruct.library.tools.definition.harvest.predicate;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.LazyTag;

/**
 * Modifier matching a tag
 */
@RequiredArgsConstructor
public class TagBlockPredicate implements BlockPredicate {
  private final LazyTag<Block> tag;

  public TagBlockPredicate(Tag.Named<Block> tag) {
    this(LazyTag.of(tag));
  }

  @Override
  public boolean matches(BlockState state) {
    return state.is(tag);
  }

  @Override
  public IGenericLoader<?> getLoader() {
    return LOADER;
  }

  /**
   * Loader for this predicate
   */
  public static final IGenericLoader<TagBlockPredicate> LOADER = new IGenericLoader<>() {
    @Override
    public TagBlockPredicate deserialize(JsonObject json) {
      return new TagBlockPredicate(LazyTag.fromJson(Registry.BLOCK_REGISTRY, json, "tag"));
    }

    @Override
    public TagBlockPredicate fromNetwork(FriendlyByteBuf buffer) {
      return new TagBlockPredicate(LazyTag.fromNetwork(Registry.BLOCK_REGISTRY, buffer));
    }

    @Override
    public void serialize(TagBlockPredicate object, JsonObject json) {
      json.addProperty("tag", object.tag.getName().toString());
    }

    @Override
    public void toNetwork(TagBlockPredicate object, FriendlyByteBuf buffer) {
      object.tag.toNetwork(buffer);
    }
  };
}
