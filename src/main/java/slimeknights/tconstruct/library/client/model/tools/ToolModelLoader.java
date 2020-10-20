package slimeknights.tconstruct.library.client.model.tools;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.SimpleModelTransform;

import java.util.Map;

/**
 * Based upon CompositeModel
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ToolModelLoader implements IModelLoader<ToolModelGeometry> {
    public static final ToolModelLoader INSTANCE = new ToolModelLoader();

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {}

    @Override
    public ToolModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
      if (!modelContents.has("parts"))
        throw new RuntimeException("Composite model requires a \"parts\" element.");
      ImmutableMap.Builder<String, Submodel> parts = ImmutableMap.builder();
      for(Map.Entry<String, JsonElement> part : modelContents.get("parts").getAsJsonObject().entrySet()) {
        IUnbakedModel subPartModel = deserializationContext.deserialize(part.getValue(), BlockModel.class);
        IModelTransform modelTransform = SimpleModelTransform.IDENTITY;
        parts.put(part.getKey(), new Submodel(part.getKey(), subPartModel, modelTransform));
      }
      return new ToolModelGeometry(parts.build());
    }

  private IModelTransform getModelTransform(JsonDeserializationContext deserializationContext, JsonElement partJson) {
    JsonElement transform = partJson.getAsJsonObject().get("transform");
    if(transform == null) {
      return SimpleModelTransform.IDENTITY;
    }

    TransformationMatrix matrix = deserializationContext.deserialize(transform, TransformationMatrix.class);
    return new SimpleModelTransform(matrix);
  }

  @Data
    private static class SubPartJson {
      /**
       * texture implies an automatically generated model from a texture.
       * This is a shorthand for linking a model using forges default item as a parent.
       */
      private final String texture;
      /**
       * A resourceLocation referencing the model to use. This takes precedence over texture.
       * Note that this is a string instead of ResourceLocation
       * as the ModelLoader deserializer does not support ResourceLocation directly.
       */
      private final String model;
      /**
       * Transform for the model, how it shall be positioned inside the parent model.
       * This allows for relative positioning of the sub-model. It uses the vanilla format of:
       * {
       *   "rotation": [x,y,z],
       *   "translation": [x,y,z],
       *   "scale": [x,y,z]
       * }
       */
      private final TransformationMatrix transform;
    }
}
