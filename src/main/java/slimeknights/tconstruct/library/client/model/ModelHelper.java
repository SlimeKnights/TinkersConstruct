package slimeknights.tconstruct.library.client.model;

import com.google.common.base.Charsets;
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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.TRSRTransformation;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;

public class ModelHelper extends slimeknights.mantle.client.ModelHelper {

  static final Type maptype = new TypeToken<Map<String, String>>() {}.getType();
  static final Type offsettype = new TypeToken<Offset>() {}.getType();
  private static final Gson
      GSON =
      new GsonBuilder().registerTypeAdapter(maptype, ModelTextureDeserializer.INSTANCE).registerTypeAdapter(offsettype, OffsetDeserializer.INSTANCE).create();

  public static Reader getReaderForResource(ResourceLocation location) throws IOException {
    ResourceLocation file = new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + ".json");
    IResource iresource = Minecraft.getMinecraft().getResourceManager().getResource(file);
    return new BufferedReader(new InputStreamReader(iresource.getInputStream(), Charsets.UTF_8));
  }

  public static Map<String, String> loadTexturesFromJson(ResourceLocation location) throws IOException {
    Reader reader = getReaderForResource(location);
    try {
      return GSON.fromJson(reader, maptype);
    } finally {
      IOUtils.closeQuietly(reader);
    }
  }

  public static Offset loadOffsetFromJson(ResourceLocation location) throws IOException {
    Reader reader = getReaderForResource(location);
    try {
      return GSON.fromJson(reader, offsettype);
    } finally {
      IOUtils.closeQuietly(reader);
    }
  }

  public static ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> loadTransformFromJson(ResourceLocation location) throws IOException {
    Reader reader = getReaderForResource(location);
    try {
      // we abuse ModelBlock because all the deserializers are not accessible..
      ModelBlock modelBlock = ModelBlock.deserialize(reader);
      ItemCameraTransforms itemCameraTransforms = modelBlock.getAllTransforms();
      ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> builder = ImmutableMap.builder();
      for(ItemCameraTransforms.TransformType type : ItemCameraTransforms.TransformType.values()) {
        if(itemCameraTransforms.getTransform(type) != ItemTransformVec3f.DEFAULT) {
          builder.put(type, new TRSRTransformation(itemCameraTransforms.getTransform(type)));
        }
      }
      return builder.build();
    } finally {
      IOUtils.closeQuietly(reader);
    }
  }

  public static ImmutableList<ResourceLocation> loadTextureListFromJson(ResourceLocation location) throws IOException {
    ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();
    for(String s : loadTexturesFromJson(location).values()) {
      builder.add(new ResourceLocation(s));
    }

    return builder.build();
  }

  public static ResourceLocation getModelLocation(ResourceLocation location) {
    return new ResourceLocation(location.getResourceDomain(), "models/" + location.getResourcePath() + ".json");
  }

  /**
   * Deseralizes a json in the format of { "textures": { "foo": "texture",... }}
   * Ignores all invalid json
   */
  public static class ModelTextureDeserializer implements JsonDeserializer<Map<String, String>> {

    public static final ModelTextureDeserializer INSTANCE = new ModelTextureDeserializer();

    private static final Gson GSON = new Gson();

    @Override
    public Map<String, String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {

      JsonObject obj = json.getAsJsonObject();
      JsonElement texElem = obj.get("textures");

      if(texElem == null) {
        throw new JsonParseException("Missing textures entry in json");
      }

      return GSON.fromJson(texElem, maptype);
    }
  }

  /**
   * Deseralizes a json in the format of { "offset": { "x": 1, "y": 2 }}
   * Ignores all invalid json
   */
  public static class OffsetDeserializer implements JsonDeserializer<Offset> {

    public static final OffsetDeserializer INSTANCE = new OffsetDeserializer();

    private static final Gson GSON = new Gson();

    @Override
    public Offset deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {

      JsonObject obj = json.getAsJsonObject();
      JsonElement texElem = obj.get("offset");

      if(texElem == null) {
        return new Offset();
      }

      return GSON.fromJson(texElem, offsettype);
    }
  }

  public static class Offset {
    public int x;
    public int y;
  }
}
