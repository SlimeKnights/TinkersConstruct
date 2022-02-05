package slimeknights.tconstruct.library.tools.definition.harvest.predicate;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;

import java.util.List;
import java.util.function.Function;

/** Loader for AND and OR predicates */
@RequiredArgsConstructor
public class NestedBlockPredicateLoader<T extends BlockPredicate> implements IGenericLoader<T> {
  private final Function<List<BlockPredicate>,T> constructor;
  private final Function<T,List<BlockPredicate>> getter;

  @Override
  public T deserialize(JsonObject json) {
    return constructor.apply(JsonHelper.parseList(json, "predicates", BlockPredicate.LOADER::deserialize));
  }

  @Override
  public T fromNetwork(FriendlyByteBuf buffer) {
    int max = buffer.readVarInt();
    ImmutableList.Builder<BlockPredicate> builder = ImmutableList.builder();
    for (int i = 0; i < max; i++) {
      builder.add(BlockPredicate.LOADER.fromNetwork(buffer));
    }
    return constructor.apply(builder.build());
  }

  @Override
  public void serialize(T object, JsonObject json) {
    JsonArray array = new JsonArray();
    for (BlockPredicate predicate : getter.apply(object)) {
      array.add(BlockPredicate.LOADER.serialize(predicate));
    }
    json.add("predicates", array);
  }

  @Override
  public void toNetwork(T object, FriendlyByteBuf buffer) {
    List<BlockPredicate> list = getter.apply(object);
    buffer.writeVarInt(list.size());
    for (BlockPredicate predicate : list) {
      BlockPredicate.LOADER.toNetwork(predicate, buffer);
    }
  }
}
