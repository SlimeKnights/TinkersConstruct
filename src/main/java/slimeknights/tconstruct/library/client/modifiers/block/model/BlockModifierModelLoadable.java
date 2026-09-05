package slimeknights.tconstruct.library.client.modifiers.block.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.primitive.ResourceLocationLoadable;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.library.client.modifiers.block.BlockModifierModelMapManager;

/** Alternative loadables for {@link BlockModifierModel}. */
public enum BlockModifierModelLoadable implements Loadable<BlockModifierModel> {
  /**
   * Serializes {@link CompoundBlockModifierModel} as an array, allows leaving off
   * the type if its {@link ElementBlockModifierModel} and named model references
   * as a string.
   */
  DEFAULT;

  @Override
  @SuppressWarnings({ "deprecated", "removal" })
  public BlockModifierModel convert(JsonElement element, String key, TypedMap context) {
    // array? load as compound
    if (element.isJsonArray()) {
      return CompoundBlockModifierModel.create(CompoundBlockModifierModel.LIST_LOADABLE.convert(element, key, context));
    }
    // primitive? load as a reference to a named model
    if (element.isJsonPrimitive()) {
      BlockModifierModel model = BlockModifierModelMapManager.INSTANCE
          .getModel(new ResourceLocation(element.getAsString()));
      if (model != null) {
        return model;
      }
      throw new ResourceLocationException("Unknown block modifier model " + element.getAsString());
    }

    // if the object has no type, default to element
    JsonObject json = GsonHelper.convertToJsonObject(element, key);
    if (!json.has("type")) {
      return ElementBlockModifierModel.LOADER.deserialize(json, context);
    }
    return BlockModifierModel.LOADER.deserialize(json, context);
  }

  @Override
  public JsonElement serialize(BlockModifierModel model) {
    ResourceLocation id = BlockModifierModelMapManager.INSTANCE.getId(model);
    if(id != null) {
      return ResourceLocationLoadable.DEFAULT.serialize(id);
    }
    // if it's a compound, serialize as a list
    if (model instanceof CompoundBlockModifierModel compound) {
      return CompoundBlockModifierModel.LIST_LOADABLE.serialize(compound.models());
    }
    // not compound, serialize using the full type form
    return BlockModifierModel.LOADER.serialize(model);
  }

  @Override
  public BlockModifierModel decode(FriendlyByteBuf buffer, TypedMap context) {
    return BlockModifierModel.LOADER.decode(buffer, context);
  }

  @Override
  public void encode(FriendlyByteBuf buffer, BlockModifierModel value) {
    BlockModifierModel.LOADER.encode(buffer, value);
  }
}
