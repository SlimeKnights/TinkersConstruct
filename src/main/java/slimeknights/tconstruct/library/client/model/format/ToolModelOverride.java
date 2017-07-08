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

import gnu.trove.map.hash.TIntObjectHashMap;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Type;
import java.util.Map;

import slimeknights.tconstruct.library.client.deserializer.ItemCameraTransformsDeserializer;
import slimeknights.tconstruct.library.client.deserializer.ItemTransformVec3fDeserializer;
import slimeknights.tconstruct.library.client.model.MaterialModel;
import slimeknights.tconstruct.library.client.model.ModifierModel;

@SideOnly(Side.CLIENT)
public class ToolModelOverride {

  public final ImmutableMap<ResourceLocation, Float> predicates;
  public final ImmutableMap<String, String> textures;
  public final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms;
  public final AmmoPosition ammoPosition;

  public final String modifierSuffix;

  // those will be filled later on during the loading progress
  public final TIntObjectHashMap<MaterialModel> partModelReplacement = new TIntObjectHashMap<>();
  public final TIntObjectHashMap<MaterialModel> brokenPartModelReplacement = new TIntObjectHashMap<>();
  public ModifierModel overrideModifierModel;

  public ToolModelOverride(ImmutableMap<ResourceLocation, Float> predicates, ImmutableMap<String, String> textures, ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms, AmmoPosition ammoPosition, String modifierSuffix) {
    this.predicates = predicates;
    this.textures = textures;
    this.transforms = transforms;
    this.ammoPosition = ammoPosition;
    this.modifierSuffix = modifierSuffix;
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
        return ImmutableList.of(GSON.fromJson(texElem, ToolModelOverrideDeserializer.TYPE));
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
        .registerTypeAdapter(AmmoPosition.AmmoPositionDeserializer.TYPE, AmmoPosition.AmmoPositionDeserializer.INSTANCE)
        .create();

    @Override
    public ToolModelOverride deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
        throws JsonParseException {
      JsonObject json = jsonElement.getAsJsonObject();

      ImmutableMap<ResourceLocation, Float> predicates = GSON.fromJson(json, PredicateDeserializer.TYPE);

      ImmutableMap<String, String> textures;
      if(json.get("textures") != null) {
        final Map<String, String> map = GSON.fromJson(json, ModelTextureDeserializer.TYPE);
        textures = ImmutableMap.copyOf(map);
      }
      else {
        textures = ImmutableMap.of();
      }

      ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms;
      if(json.get("display") != null) {
        transforms = GSON.fromJson(json, TransformDeserializer.TYPE);
      }
      else {
        transforms = ImmutableMap.of();
      }

      AmmoPosition ammoPosition = GSON.fromJson(json, AmmoPosition.AmmoPositionDeserializer.TYPE);

      String modSuffix = null;
      JsonElement modSuffixElement = json.get("modifier_suffix");
      if(modSuffixElement != null) {
        modSuffix = modSuffixElement.getAsString();
      }

      return new ToolModelOverride(predicates, textures, transforms, ammoPosition, modSuffix);
    }
  }

}
