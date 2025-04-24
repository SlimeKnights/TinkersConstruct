package slimeknights.tconstruct.library.json.predicate.material;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import java.util.function.Function;

/** Field for a material predicate that maps primitive strings to classic material predicates instead of unnamed predicates */
public record MaterialPredicateField<P>(String key, Function<P, IJsonPredicate<MaterialVariantId>> getter, boolean compact) implements LoadableField<IJsonPredicate<MaterialVariantId>,P> {
  public MaterialPredicateField(String key, Function<P, IJsonPredicate<MaterialVariantId>> getter) {
    this(key, getter, true);
  }

  @Override
  public IJsonPredicate<MaterialVariantId> get(JsonObject json, String key, TypedMap context) {
    if (json.has(key)) {
      JsonElement element = json.get(key);
      // primitive is a material ID
      if (element.isJsonPrimitive()) {
        return MaterialPredicate.variant(MaterialVariantId.LOADABLE.convert(element, key, context));
      } else {
        return MaterialPredicate.LOADER.convert(element, key, context);
      }
    }
    return MaterialPredicate.ANY;
  }

  @Override
  public void serialize(P parent, JsonObject json) {
    IJsonPredicate<MaterialVariantId> predicate = getter.apply(parent);
    if (predicate != MaterialPredicate.ANY) {
      // if compact serializing, serialize the legacy style predicate
      if (compact) {
        // ID predicate serializes to a raw ID
        if (predicate instanceof MaterialIdPredicate idPredicate && idPredicate.ids().size() == 1) {
          json.addProperty(key, idPredicate.ids().iterator().next().toString());
          return;
        }
        // single variant predicate maps to exact variant match
        if (predicate instanceof MaterialVariantPredicate variantPredicate && variantPredicate.values().size() == 1) {
          MaterialVariantId id = variantPredicate.values().iterator().next();
          // map empty variant to special default variant string
          if (id.getVariant().isEmpty()) {
            id = MaterialVariantId.create(id.getId(), MaterialVariantId.DEFAULT_VARIANT);
          }
          json.addProperty(key, id.toString());
          return;
        }
      }

      // force serialized form to be a json object, as compact is misleading
      JsonObject serialized = new JsonObject();
      MaterialPredicate.LOADER.serialize(predicate, serialized);
      json.add(key, serialized);
    }
  }

  @Override
  public IJsonPredicate<MaterialVariantId> decode(FriendlyByteBuf buffer, TypedMap context) {
    return MaterialPredicate.LOADER.decode(buffer, context);
  }

  @Override
  public void encode(FriendlyByteBuf buffer, P parent) {
    MaterialPredicate.LOADER.encode(buffer, getter.apply(parent));
  }
}
