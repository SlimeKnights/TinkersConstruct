package slimeknights.tconstruct.library.client.model.tools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.AffineTransformation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.BlockRenderView;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.model.JsonModelResourceProvider;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo.TintedSprite;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.client.model.ItemLayerModel;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.shared.TinkerClient;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MaterialModel implements TinkerModelGeometry {
  /** Shared loader instance */
  public static final Loader LOADER = new Loader();
  private static final Logger log = LogManager.getLogger(MaterialModel.class);

  /** If null, uses dynamic material */
  @Nullable
  private final MaterialId material;
  /** Tint index and index of part in tool */
  private final int index;
  /** Transform matrix to apply to child parts */
  private final Vec2f offset;

  public MaterialModel(@Nullable MaterialId material, int index, Vec2f offset) {
    this.material = material;
    this.index = index;
    this.offset = offset;
  }

  /**
   * Gets a consumer to add textures to the given collection
   * @param texture      Texture base
   * @param allTextures  Collection of textures
   * @return  Texture consumer
   */
  public static Consumer<SpriteIdentifier> getTextureAdder(SpriteIdentifier texture, Collection<SpriteIdentifier> allTextures) {
    if (texture.getTextureId().getPath().startsWith("item/tool")) {
      // keep track of skipped textures, so we do not debug print the same resource twice
      Set<Identifier> skipped = new HashSet<>();
      return mat -> {
        // either must be non-blocks, or must exist. We have fallbacks if it does not exist
        Identifier loc = mat.getTextureId();
        if (!PlayerScreenHandler.BLOCK_ATLAS_TEXTURE.equals(mat.getAtlasId()) || TinkerClient.textureValidator.test(loc)) {
          allTextures.add(mat);
        } else if (!skipped.contains(loc)) {
          skipped.add(loc);
          log.debug("Skipping loading texture '{}' as it does not exist in the resource pack", loc);
        }
      };
    } else {
      // just directly add with no filter, nothing we can do
      log.error("Texture '{}' is not in item/tool, unable to safely validate optional material textures", texture.getTextureId());
      return allTextures::add;
    }
  }

  /**
   * Gets the list of material textures for the given owner texture
   * @param allTextures  Collection of textures
   * @param textureName  Texture name to add
   * @param material     List of materials
   */
  public static void getMaterialTextures(Collection<SpriteIdentifier> allTextures, String textureName, @Nullable MaterialId material) {
    SpriteIdentifier texture = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.tryParse(textureName));
    allTextures.add(texture);

    // if the texture is missing, stop here
    if (!MissingSprite.getMissingSpriteId().equals(texture.getTextureId())) {
      // texture should exist in item/tool, or the validator cannot handle them
      Consumer<SpriteIdentifier> textureAdder = getTextureAdder(texture, allTextures);
      // if no specific material is set, load all materials as dependencies. If just one material, use just that one
      if (material == null) {
        MaterialRenderInfoLoader.INSTANCE.getAllRenderInfos().forEach(info -> info.getTextureDependencies(textureAdder, texture));
      } else {
        MaterialRenderInfoLoader.INSTANCE.getRenderInfo(material).ifPresent(info -> info.getTextureDependencies(textureAdder, texture));
      }
    }
  }

  /**
   * Gets the quads for a material for the given texture
   * @param owner         Model owner
   * @param spriteGetter  Sprite getter
   * @param transform     Model transform
   * @param name          Sprite name
   * @param index         Sprite tint index
   * @param material      Material to use
   * @return  Model quads
   */
  public static Sprite getPartQuads(Consumer<ImmutableList<BakedQuad>> quadConsumer, ModelLoader owner, Function<SpriteIdentifier, Sprite> spriteGetter, AffineTransformation transform, String name, int index, @Nullable MaterialId material) {
    SpriteIdentifier texture = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.tryParse(name));
    int tintIndex = -1;
    Sprite finalSprite = null;
    // if the base material is non-null, try to find the sprite for that material
    if (material != null) {
      // first, find a render info
      Optional<MaterialRenderInfo> renderInfo = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(material);
      if(renderInfo.isPresent()) {
        // determine the texture to use and whether or not to tint it
        TintedSprite sprite = renderInfo.get().getSprite(texture, spriteGetter);
        finalSprite = sprite.getSprite();
        if(sprite.isTinted()) {
          tintIndex = index;
        }
      }
    }

    // if we have no material, or the material failed to fetch, use the default sprite and tint index
    if (finalSprite == null) {
      finalSprite = spriteGetter.apply(texture);
      tintIndex = index;
    }

    // get quads
    quadConsumer.accept(ItemLayerModel.getQuadsForSprite(tintIndex, finalSprite, transform));

    // return sprite
    return finalSprite;
  }

  /**
   * Same as {@link #bake(ModelLoader, Function, ModelBakeSettings, Identifier)}, but uses fewer arguments and does not require an instance
   * @param owner          Model configuration
   * @param spriteGetter   Sprite getter function
   * @param transform      Transform to apply to the quad fetching. Should not include rotation or it will look wrong in UIs
   * @param material       Material used, if null uses default
   * @param index          Tint index to use if tinted sprite is used
   * @param overrides      Override instance to use, will either be empty or {@link MaterialOverrideHandler}
   * @return  Baked model
   */
  private static BakedModel bakeInternal(ModelLoader owner, Function<SpriteIdentifier, Sprite> spriteGetter, AffineTransformation transform, @Nullable MaterialId material, int index, ModelOverrideList overrides) {
    // small hack to reduce the need to create a second immutable list
    MutableObject<ImmutableList<BakedQuad>> mutableList = new MutableObject<>();
    Sprite particle = getPartQuads(mutableList::setValue, owner, spriteGetter, transform, "texture", index, material);

    // bake model - while the transform may not be identity, it never has rotation so its safe to say untransformed
//    ImmutableMap<Mode, AffineTransformation> transformMap = PerspectiveMapWrapper.getTransforms(owner.getCombinedTransform());
    return new BasicBakedModel(mutableList.getValue(), Collections.emptyMap(), true, true, true, particle, ModelTransformation.NONE, overrides);
  }

  @Override
  public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
    // create transforms from offset
    AffineTransformation transforms;
    if (Vec2f.ZERO.equals(offset)) {
      transforms = AffineTransformation.identity();
    } else {
      // divide by 16 to convert from pixels to base values
      // negate Y as positive is up for transforms but down for pixels
      transforms = new AffineTransformation(new Vector3f(offset.x / 16, -offset.y / 16, 0), null, null, null);
    }

    // if the material is already set, no need to set overrides
    ModelOverrideList overrides = ModelOverrideList.EMPTY;
    if (material == null) {
      overrides = new MaterialOverrideHandler(loader, index, transforms);
    }

    // after that its base logic
    return bakeInternal(loader, textureGetter, transforms, material, index, overrides);
  }

  @Override
  public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
    return Collections.emptyList();
  }

  @Override
  public boolean useAmbientOcclusion() {
    return false;
  }

  @Override
  public boolean hasDepth() {
    return false;
  }

  @Override
  public boolean isSideLit() {
    return false;
  }

  @Override
  public boolean isBuiltin() {
    return false;
  }

  @Override
  public Sprite getSprite() {
    return null;
  }

  @Override
  public ModelTransformation getTransformation() {
    return null;
  }

  @Override
  public ModelOverrideList getOverrides() {
    return null;
  }

  @Override
  public Collection<Identifier> getModelDependencies() {
    return Collections.emptySet();
  }

  @Override
  public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
    Set<SpriteIdentifier> allTextures = Sets.newHashSet();
    getMaterialTextures(allTextures, "texture", material);
    return allTextures;
  }

  /**
   * Dynamic override handler to swap in the material texture
   */
  private static final class MaterialOverrideHandler extends ModelOverrideList {
    // contains all the baked models since they'll never change, cleared automatically as the baked model is discarded
    private final Map<MaterialId, BakedModel> cache = new HashMap<>();

    // parameters needed for rebaking
    private final ModelLoader owner;
    private final int index;
    private final AffineTransformation itemTransform;

    private MaterialOverrideHandler(ModelLoader owner, int index, AffineTransformation itemTransform) {
      super();
      this.owner = owner;
      this.index = index;
      this.itemTransform = itemTransform;
    }

    @Override
    public BakedModel apply(BakedModel originalModel, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
      // fetch the material from the stack
      MaterialId material = IMaterialItem.getMaterialIdFromStack(stack);
      // cache all baked material models, they will not need to be recreated as materials will not change
      return cache.computeIfAbsent(material, this::bakeDynamic);
    }

    /**
     * Bakes a copy of this model using the given material
     * @param material  New material for the model
     * @return  Baked model
     */
    private BakedModel bakeDynamic(MaterialId material) {
      // bake internal does not require an instance to bake, we can pass in whatever material we want
      // use empty override list as the sub model never calls overrides, and already has a material
      return bakeInternal(owner, SpriteIdentifier::getSprite, itemTransform, material, index, ModelOverrideList.EMPTY);
    }
  }

  /**
   * Model loader logic, use {@link #LOADER} to access instance
   */
  private static class Loader extends JsonModelResourceProvider {

    public Loader() {
      super(Util.getResource("material").toString());
    }

    @Override
    public UnbakedModel loadJsonModelResource(Identifier resourceId, JsonObject modelContents, ModelProviderContext context) {
      // need tint index for tool models, doubles as part index
      int index = JsonHelper.getInt(modelContents, "index", 0);

      // static material can be defined, if unset uses dynamic material
      MaterialId material = null;
      if (modelContents.has("material")) {
        material = new MaterialId(JsonHelper.getString(modelContents, "material"));
      }

      Vec2f offset = Vec2f.ZERO;
      if (modelContents.has("offset")) {
        offset = arrayToObject(modelContents, "offset");
      }

      return new MaterialModel(material, index, offset);
    }
  }


  /* Helpers */

  /**
   * Converts a JSON float array to the specified object
   * @param json    JSON object
   * @param name    Name of the array in the object to fetch
   * @return  Vector3f of data
   * @throws JsonParseException  If there is no array or the length is wrong
   */
  public static Vec2f arrayToObject(JsonObject json, String name) {
    JsonArray array = JsonHelper.getArray(json, name);
    if (array.size() != 2) {
      throw new JsonParseException("Expected " + 2 + " " + name + " values, found: " + array.size());
    }
    float[] vec = new float[2];
    for(int i = 0; i < 2; ++i) {
      vec[i] = JsonHelper.asFloat(array.get(i), name + "[" + i + "]");
    }
    return new Vec2f(vec[0], vec[1]);
  }
}
