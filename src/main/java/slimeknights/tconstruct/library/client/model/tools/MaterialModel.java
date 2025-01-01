package slimeknights.tconstruct.library.client.model.tools;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Transformation;
import lombok.AllArgsConstructor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.joml.Vector3f;
import slimeknights.mantle.client.model.util.MantleItemLayerModel;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo.TintedSprite;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Model for an item with material texture variants, such as tool parts. Used only for single material items, {@link ToolModel} is used for multi-material items.
 */
@AllArgsConstructor
public class MaterialModel implements IUnbakedGeometry<MaterialModel> {
  /** Shared loader instance */
  public static final IGeometryLoader<MaterialModel> LOADER = MaterialModel::deserialize;

  /** If null, uses dynamic material */
  @Nullable
  private final MaterialVariantId material;
  /** Tint index and index of part in tool */
  private final int index;
  /** Transform matrix to apply to child parts */
  private final Vec2 offset;

  /**
   * Checks that all unique material textures for the given part exist, logs any that are missing via the sprite getter function.
   * @param owner        Model owner
   * @param textureName  Texture name to add
   * @param material     List of materials
   */
  public static void validateMaterialTextures(IGeometryBakingContext owner, Function<Material, TextureAtlasSprite> spriteGetter, String textureName, @Nullable MaterialVariantId material) {
    Material texture = owner.getMaterial(textureName);

    // if the texture is missing, stop here with a warning for the root
    if (!MissingTextureAtlasSprite.getLocation().equals(texture.texture())) {
      // if no specific material is set, load all materials as dependencies. If just one material, use just that one
      if (material == null) {
        MaterialRenderInfoLoader.INSTANCE.getAllRenderInfos().forEach(info -> info.getSprite(texture, spriteGetter));
      } else {
        MaterialRenderInfoLoader.INSTANCE.getRenderInfo(material).ifPresent(info -> info.getSprite(texture, spriteGetter));
      }
    }
  }

  /**
   * Gets the tinted sprite info for the given material
   * @param spriteGetter  Sprite getter instance
   * @param texture       Base texture
   * @param material      Material variant
   * @return  Tinted sprite or fallback
   */
  @SuppressWarnings("OptionalIsPresent")
  public static TintedSprite getMaterialSprite(Function<Material, TextureAtlasSprite> spriteGetter, Material texture, MaterialVariantId material) {
    // if the base material is non-null, try to find the sprite for that material
    // first, find a render info
    Optional<MaterialRenderInfo> optional = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(material);
    if (optional.isPresent()) {
      return optional.get().getSprite(texture, spriteGetter);
    }
    return new TintedSprite(spriteGetter.apply(texture), -1, 0);
  }

  /**
   * Gets quads for the given material variant of the texture
   * @param spriteGetter    Sprite getter instance
   * @param texture         Base texture
   * @param material        Material variant
   * @param tintIndex       Tint index for quads
   * @param transformation  Transformation to apply
   * @param pixels          Pixels to prevent z-fighting for multiple layers
   * @return  Quad list
   */
  public static ImmutableList<BakedQuad> getQuadsForMaterial(Function<Material, TextureAtlasSprite> spriteGetter, Material texture, MaterialVariantId material, int tintIndex, Transformation transformation, @Nullable ItemLayerPixels pixels) {
    TintedSprite sprite = getMaterialSprite(spriteGetter, texture, material);
    return MantleItemLayerModel.getQuadsForSprite(sprite.color(), tintIndex, sprite.sprite(), transformation, sprite.emissivity(), pixels);
  }

  /**
   * Same as {@link #bake(IGeometryBakingContext, ModelBaker, Function, ModelState, ItemOverrides, ResourceLocation)} , but uses fewer arguments and does not require an instance
   * @param owner          Model configuration
   * @param spriteGetter   Sprite getter function
   * @param transform      Transform to apply to the quad fetching. Should not include rotation or it will look wrong in UIs
   * @param material       Material used, if null uses default
   * @param index          Tint index to use if tinted sprite is used
   * @param overrides      Override instance to use, will either be empty or {@link MaterialOverrideHandler}
   * @return  Baked model
   */
  private static BakedModel bakeInternal(IGeometryBakingContext owner, Function<Material, TextureAtlasSprite> spriteGetter, Transformation transform, MaterialVariantId material, int index, ItemOverrides overrides) {
    TintedSprite materialSprite = getMaterialSprite(spriteGetter, owner.getMaterial("texture"), material);
    CompositeModel.Baked.Builder builder = CompositeModel.Baked.builder(owner, materialSprite.sprite(), overrides, owner.getTransforms());
    // TODO: let material choose its render type
    builder.addQuads(MantleItemLayerModel.getDefaultRenderType(owner), MantleItemLayerModel.getQuadsForSprite(materialSprite.color(), index, materialSprite.sprite(), transform, materialSprite.emissivity()));
    return builder.build();
  }

  @Override
  public BakedModel bake(IGeometryBakingContext owner, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides vanillaOverrides, ResourceLocation modelLocation) {
    if (Config.CLIENT.logMissingMaterialTextures.get()) {
      validateMaterialTextures(owner, spriteGetter, "texture", material);
    }
    // create transforms from offset
    // TODO: figure out forge transforms, can I use them here?
    Transformation transforms;
    if (Vec2.ZERO.equals(offset)) {
      transforms = Transformation.identity();
    } else {
      // divide by 16 to convert from pixels to base values
      // negate Y as positive is up for transforms but down for pixels
      transforms = new Transformation(new Vector3f(offset.x / 16, -offset.y / 16, 0), null, null, null);
    }

    // if the material is already set, no need to set overrides
    ItemOverrides overrides = ItemOverrides.EMPTY;
    if (material == null) {
      overrides = new MaterialOverrideHandler(owner, index, transforms);
    }

    // after that its base logic
    return bakeInternal(owner, spriteGetter, transforms, Objects.requireNonNullElse(material, IMaterial.UNKNOWN_ID), index, overrides);
  }

  /**
   * Dynamic override handler to swap in the material texture
   */
  private static final class MaterialOverrideHandler extends ItemOverrides {
    // contains all the baked models since they'll never change, cleared automatically as the baked model is discarded
    private final Map<MaterialVariantId, BakedModel> cache = new ConcurrentHashMap<>();

    // parameters needed for rebaking
    private final IGeometryBakingContext owner;
    private final int index;
    private final Transformation itemTransform;
    private MaterialOverrideHandler(IGeometryBakingContext owner, int index, Transformation itemTransform) {
      this.owner = owner;
      this.index = index;
      this.itemTransform = itemTransform;
    }

    @Override
    public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
      // fetch the material from the stack
      MaterialVariantId material = IMaterialItem.getMaterialFromStack(stack);
      // cache all baked material models, they will not need to be recreated as materials will not change
      return cache.computeIfAbsent(material, this::bakeDynamic);
    }

    /**
     * Bakes a copy of this model using the given material
     * @param material  New material for the model
     * @return  Baked model
     */
    private BakedModel bakeDynamic(MaterialVariantId material) {
      // bake internal does not require an instance to bake, we can pass in whatever material we want
      // use empty override list as the sub model never calls overrides, and already has a material
      return bakeInternal(owner, Material::sprite, itemTransform, material, index, ItemOverrides.EMPTY);
    }
  }


  /* Helpers */

  /** Loads a material model from JSON */
  public static MaterialModel deserialize(JsonObject json, JsonDeserializationContext context) {
    // need tint index for tool models, doubles as part index
    int index = GsonHelper.getAsInt(json, "index", 0);

    // static material can be defined, if unset uses dynamic material
    MaterialVariantId material = null;
    if (json.has("material")) {
      material = MaterialVariantId.fromJson(json, "material");
    }

    Vec2 offset = Vec2.ZERO;
    if (json.has("offset")) {
      offset = getVec2(json, "offset");
    }

    return new MaterialModel(material, index, offset);
  }

  /**
   * Converts a JSON float array to the specified object
   * @param json    JSON object
   * @param name    Name of the array in the object to fetch
   * @return  Vector3f of data
   * @throws JsonParseException  If there is no array or the length is wrong
   */
  public static Vec2 getVec2(JsonObject json, String name) {
    JsonArray array = GsonHelper.getAsJsonArray(json, name);
    if (array.size() != 2) {
      throw new JsonParseException("Expected " + 2 + " " + name + " values, found: " + array.size());
    }
    float[] vec = new float[2];
    for(int i = 0; i < 2; ++i) {
      vec[i] = GsonHelper.convertToFloat(array.get(i), name + "[" + i + "]");
    }
    return new Vec2(vec[0], vec[1]);
  }
}
