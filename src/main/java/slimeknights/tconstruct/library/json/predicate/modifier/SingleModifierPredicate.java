package slimeknights.tconstruct.library.json.predicate.modifier;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.ModifierId;

/** Predicate matching a single modifier */
public record SingleModifierPredicate(ModifierId modifier) implements ModifierPredicate {
  @Override
  public boolean matches(ModifierId input) {
    return input.equals(modifier);
  }

  @Override
  public IGenericLoader<? extends IJsonPredicate<ModifierId>> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<SingleModifierPredicate> LOADER = new IGenericLoader<>() {
    @Override
    public SingleModifierPredicate deserialize(JsonObject json) {
      return new SingleModifierPredicate(new ModifierId(JsonHelper.getResourceLocation(json, "modifier")));
    }

    @Override
    public SingleModifierPredicate fromNetwork(FriendlyByteBuf buffer) {
      return new SingleModifierPredicate(new ModifierId(buffer.readResourceLocation()));
    }

    @Override
    public void serialize(SingleModifierPredicate object, JsonObject json) {
      json.addProperty("modifier", object.modifier.toString());
    }

    @Override
    public void toNetwork(SingleModifierPredicate object, FriendlyByteBuf buffer) {
      buffer.writeResourceLocation(object.modifier);
    }
  };
}
