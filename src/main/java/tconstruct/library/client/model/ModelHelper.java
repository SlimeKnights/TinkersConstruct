package tconstruct.library.client.model;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IColoredBakedQuad;
import net.minecraftforge.client.model.IModelPart;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.IPerspectiveState;
import net.minecraftforge.client.model.TRSRTransformation;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;

import javax.vecmath.Vector3f;

public class ModelHelper {

  static final Type maptype = new TypeToken<Map<String, String>>() {}.getType();
  private static final Gson
      GSON =
      new GsonBuilder().registerTypeAdapter(maptype, ModelTextureDeserializer.INSTANCE).create();

  public static final IPerspectiveState DEFAULT_ITEM_STATE;
  public static final IPerspectiveState DEFAULT_TOOL_STATE;

  public static TextureAtlasSprite getTextureFromBlock(Block block, int meta) {
    IBlockState state = block.getStateFromMeta(meta);
    return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
  }

  public static TextureAtlasSprite getTextureFromBlockstate(IBlockState state) {
    return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
  }

  public static BakedQuad colorQuad(int color, BakedQuad quad) {
    int[] data = quad.getVertexData();

    int a = (color >> 24);
    if(a == 0) {
      a = 255;
    }

    int c = 0;
    c |= ((color >> 16) & 0xFF) << 0; // red
    c |= ((color >> 8) & 0xFF) << 8; // green
    c |= ((color >> 0) & 0xFF) << 16; // blue
    c |= (a & 0xFF) << 24; // alpha

    // update color in the data. all 4 Vertices.
    for(int i = 0; i < 4; i++) {
      data[i * 7 + 3] = c;
    }

    return new IColoredBakedQuad.ColoredBakedQuad(data, -1, quad.getFace());
  }

  public static Map<String, String> loadTexturesFromJson(ResourceLocation location) throws IOException {
    // get the json
    IResource
        iresource =
        Minecraft.getMinecraft().getResourceManager()
                 .getResource(new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + ".json"));
    Reader reader = new InputStreamReader(iresource.getInputStream(), Charsets.UTF_8);

    return GSON.fromJson(reader, maptype);
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

  public static ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> getTransformsFromState(IModelState state, IModelPart part) {
    if(state instanceof IPerspectiveState) {
      IPerspectiveState ps = (IPerspectiveState) state;
      Map<ItemCameraTransforms.TransformType, TRSRTransformation> map = Maps.newHashMap();
      for(ItemCameraTransforms.TransformType type : ItemCameraTransforms.TransformType.values()) {
        map.put(type, ps.forPerspective(type).apply(part));
      }
      return Maps.immutableEnumMap(map);
    }
    return ImmutableMap.of();
  }

  static {
    // equals forge:default-item
    IModelState thirdperson = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
        new Vector3f(0, 1f / 16, -3f / 16),
        TRSRTransformation.quatFromYXZDegrees(new Vector3f(-90, 0, 0)),
        new Vector3f(0.55f, 0.55f, 0.55f),
        null));
    IModelState firstperson = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
        new Vector3f(0, 4f / 16, 2f / 16),
        TRSRTransformation.quatFromYXZDegrees(new Vector3f(0, -135, 25)),
        new Vector3f(1.7f, 1.7f, 1.7f),
        null));
    DEFAULT_ITEM_STATE = new IPerspectiveState.Impl(TRSRTransformation.identity(), ImmutableMap
                                                        .of(ItemCameraTransforms.TransformType.THIRD_PERSON, thirdperson,
                                                            ItemCameraTransforms.TransformType.FIRST_PERSON, firstperson));

    thirdperson = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
        new Vector3f(0, 1.25f / 16, -3.5f / 16),
        TRSRTransformation.quatFromYXZDegrees(new Vector3f(0, 90, -35)),
        new Vector3f(0.85f, 0.85f, 0.85f),
        null));
    firstperson = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
        new Vector3f(0, 4f / 16, 2f / 16),
        TRSRTransformation.quatFromYXZDegrees(new Vector3f(0, -135, 25)),
        new Vector3f(1.7f, 1.7f, 1.7f),
        null));
    DEFAULT_TOOL_STATE = new IPerspectiveState.Impl(TRSRTransformation.identity(), ImmutableMap
        .of(ItemCameraTransforms.TransformType.THIRD_PERSON, thirdperson,
            ItemCameraTransforms.TransformType.FIRST_PERSON, firstperson));
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
}
