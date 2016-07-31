package slimeknights.tconstruct.library.client.model.format;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

import slimeknights.tconstruct.library.client.deserializer.ItemCameraTransformsDeserializer;
import slimeknights.tconstruct.library.client.deserializer.ItemTransformVec3fDeserializer;

@SideOnly(Side.CLIENT)
public class ToolModelOverride {

  public final ImmutableMap<ResourceLocation, Float> predicates;
  public final ImmutableMap<String, String> textures;
  public final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms;

  public ToolModelOverride(ImmutableMap<ResourceLocation, Float> predicates, ImmutableMap<String, String> textures, ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms) {
    this.predicates = predicates;
    this.textures = textures;
    this.transforms = transforms;
  }

  public static class ToolModelOverrideListDeserializer implements JsonDeserializer<ImmutableList<ToolModelOverride>> {

    public static final ToolModelOverrideListDeserializer INSTANCE = new ToolModelOverrideListDeserializer();
    public static final Type TYPE = new TypeToken<ImmutableList<ToolModelOverride>>() {}.getType();

    private static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(ToolModelOverrideDeserializer.TYPE, ToolModelOverrideDeserializer.INSTANCE)
        .create();

    @Override
    public ImmutableList<ToolModelOverride> deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext)
        throws JsonParseException {
      JsonObject obj = json.getAsJsonObject();
      JsonElement texElem = obj.get("overrides");

      if(texElem == null) {
        return ImmutableList.of();
      }

      if(texElem.isJsonObject()) {
        return ImmutableList.of((ToolModelOverride) GSON.fromJson(json, ToolModelOverrideDeserializer.TYPE));
      }

      ImmutableList.Builder<ToolModelOverride> builder = ImmutableList.builder();

      for(JsonElement jsonElement : texElem.getAsJsonArray()) {
        builder.add((ToolModelOverride) GSON.fromJson(jsonElement, ToolModelOverrideDeserializer.TYPE));
      }

      return builder.build();
    }
  }

  public static class ToolModelOverrideDeserializer implements JsonDeserializer<ToolModelOverride> {

    public static final ToolModelOverrideDeserializer INSTANCE = new ToolModelOverrideDeserializer();
    public static final Type TYPE = new TypeToken<ToolModelOverride>() {}.getType();

    private static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(ModelTextureDeserializer.TYPE, ModelTextureDeserializer.INSTANCE)
        .registerTypeAdapter(PredicateDeserializer.TYPE, PredicateDeserializer.INSTANCE)
        .registerTypeAdapter(TransformDeserializer.TYPE, TransformDeserializer.INSTANCE)
        .registerTypeAdapter(ItemCameraTransforms.class, ItemCameraTransformsDeserializer.INSTANCE)
        .registerTypeAdapter(ItemTransformVec3f.class, ItemTransformVec3fDeserializer.INSTANCE)
        .create();

    @Override
    public ToolModelOverride deserialize(JsonElement json, Type type, JsonDeserializationContext context)
        throws JsonParseException {
      ImmutableMap<ResourceLocation, Float> predicates = GSON.fromJson(json, PredicateDeserializer.TYPE);
      ImmutableMap<String, String> textures = ImmutableMap.copyOf((Map<String, String>)GSON.fromJson(json, ModelTextureDeserializer.TYPE));
      ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms = GSON.fromJson(json, TransformDeserializer.TYPE);

      return new ToolModelOverride(predicates, textures, transforms);
    }
  }

}
