package slimeknights.tconstruct.library.client.modifiers.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.resources.model.Material;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.library.client.modifiers.NormalModifierModel;

import java.util.List;

/** Alternative loadables for {@link ModifierModel}. */
public enum ModifierModelLoadable implements Loadable<ModifierModel> {
  /** Serializes {@link CompoundModifierModel} as an array, and {@link NormalModifierModel} with just small as a string. */
  COMPACT,
  /** Serializes as an object, but allows leaving off the type if its {@link NormalModifierModel}. */
  OPTIONAL_TYPE;

  /** Loadable for a list of {@link #OPTIONAL_TYPE} of size 2 or more. */
  public static final Loadable<List<ModifierModel>> LIST = OPTIONAL_TYPE.list(2);

  @SuppressWarnings("removal")
  @Override
  public ModifierModel convert(JsonElement element, String key, TypedMap context) {
    // array? load as compound
    if (this == COMPACT) {
      if (element.isJsonArray()) {
        JsonArray array = element.getAsJsonArray();
        // if it's an empty array, replace with the empty model
        if (array.isEmpty()) {
          return ModifierModel.EMPTY;
        }
        // if it's a size 1 array, just parse that directly, but stick to what is valid in a list
        if (array.size() == 1) {
          return OPTIONAL_TYPE.convert(array.get(0), key + "[0]", context);
        }
        // otherwise parse as a compound
        return CompoundModifierModel.create(LIST.convert(element, key, context));
      }
      // primitive? load as the texture for normal
      if (element.isJsonPrimitive()) {
        return new NormalModifierModel(ModifierModel.blockAtlas(new ResourceLocation(element.getAsString())), null);
      }
    }
    // if the object has no type, default to normal
    JsonObject json = GsonHelper.convertToJsonObject(element, key);
    if (!json.has("type")) {
      return NormalModifierModel.LOADER.deserialize(json, context);
    }
    return ModifierModel.LOADER.deserialize(json, context);
  }

  @Override
  public JsonElement serialize(ModifierModel model) {
    // if it's a compound, serialize as a list
    if (this == COMPACT) {
      // if its empty, serialize as an empty list
      if (model == ModifierModel.EMPTY) {
        return new JsonArray();
      }
      // if it's a compound, serialize as a list with stuff
      // note this will error if the compound has 0 or 1 elements; why use a compound then?
      if (model instanceof CompoundModifierModel compound) {
        return LIST.serialize(compound.models());
      }
    }
    // if normal, leave off the type when serializing. Want exact type match as that is extendable
    if (model.getLoader() == NormalModifierModel.LOADER) {
      NormalModifierModel normal = (NormalModifierModel) model;
      if (this == COMPACT) {
        // if everything is default, we can serialize as string
        Material small = normal.small();
        if (small != null && normal.large() == null && normal.luminosity() == 0 && normal.color() == -1) {
          return new JsonPrimitive(small.texture().toString());
        }
      }
      return NormalModifierModel.LOADER.serialize(normal);
    }
    // not compound, not normal, serialize using the full type form
    return ModifierModel.LOADER.serialize(model);
  }

  @Override
  public ModifierModel decode(FriendlyByteBuf buffer, TypedMap context) {
    return ModifierModel.LOADER.decode(buffer, context);
  }

  @Override
  public void encode(FriendlyByteBuf buffer, ModifierModel value) {
    ModifierModel.LOADER.encode(buffer, value);
  }
}
