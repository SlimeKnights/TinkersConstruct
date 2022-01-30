package slimeknights.tconstruct.library.tools.definition.harvest.predicate;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;

/** Predicate that inverts the condition */
@RequiredArgsConstructor
public class InvertedBlockPredicate implements BlockPredicate {
  private final BlockPredicate base;

  @Override
  public boolean matches(BlockState state) {
    return !base.matches(state);
  }

  @Override
  public IGenericLoader<?> getLoader() {
    return LOADER;
  }

  @Override
  public BlockPredicate inverted() {
    return base;
  }

  public static final IGenericLoader<InvertedBlockPredicate> LOADER = new IGenericLoader<>() {
    @Override
    public InvertedBlockPredicate deserialize(JsonObject json) {
      return new InvertedBlockPredicate(BlockPredicate.LOADER.deserialize(GsonHelper.getAsJsonObject(json, "predicate")));
    }

    @Override
    public InvertedBlockPredicate fromNetwork(FriendlyByteBuf buffer) {
      return new InvertedBlockPredicate(BlockPredicate.LOADER.fromNetwork(buffer));
    }

    @Override
    public void serialize(InvertedBlockPredicate object, JsonObject json) {
      json.add("predicate", BlockPredicate.LOADER.serialize(object.base));
    }

    @Override
    public void toNetwork(InvertedBlockPredicate object, FriendlyByteBuf buffer) {
      BlockPredicate.LOADER.toNetwork(object.base, buffer);
    }
  };
}
