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
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import slimeknights.tconstruct.library.client.materials.IMaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.shared.TinkerClient;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

@AllArgsConstructor
@Log4j2
public class MaterialModel implements IModelGeometry<MaterialModel> {
  /** Shared loader instance */
  public static final Loader LOADER = new Loader();

  /** If null, uses dynamic material */
  @Nullable
  private final MaterialId material;
  /** Tint index and index of part in tool */
  private final int index;
  /** Transform matrix to apply to child parts */
  private final Vector2f offset;

  @Override
  public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation,IUnbakedModel> modelGetter, Set<Pair<String,String>> missingTextureErrors) {
    Set<RenderMaterial> allTextures = Sets.newHashSet();
    RenderMaterial texture = owner.resolveTexture("texture");
    allTextures.add(texture);
    // texture should exist in item/tool, or the validator cannot handle them
    Consumer<RenderMaterial> textureAdder;
    if (texture.getTextureLocation().getPath().startsWith("item/tool")) {
      // keep track of skipped textures, so we do not debug print the same resource twice
      Set<ResourceLocation> skipped = new HashSet<>();
      textureAdder = mat -> {
        // either must be non-blocks, or must exist. We have fallbacks if it does not exist
        ResourceLocation loc = mat.getTextureLocation();
        if (!PlayerContainer.LOCATION_BLOCKS_TEXTURE.equals(mat.getAtlasLocation()) || TinkerClient.textureValidator.test(loc)) {
          allTextures.add(mat);
        } else if (!skipped.contains(loc)) {
          skipped.add(loc);
          log.debug("Skipping loading texture '{}' as it does not exist in the resource pack", loc);
        }
      };
    } else {
      // just directly add with no filter, nothing we can do
      textureAdder = allTextures::add;
      log.error("Texture '{}' is not in item/tool, unable to safely validate optional material textures", texture.getTextureLocation());
    }

    // if no specific material is set, load all materials as dependencies. If just one material, use just that one
    if (material == null) {
      MaterialRenderInfoLoader.INSTANCE.getAllRenderInfos().forEach(info -> info.getTextureDependencies(textureAdder, texture));
    } else {
      MaterialRenderInfoLoader.INSTANCE.getRenderInfo(material).ifPresent(info -> info.getTextureDependencies(textureAdder, texture));
    }

    // final list
    return allTextures;
  }

  /**
   * Same as {@link #bake(IModelConfiguration, ModelBakery, Function, IModelTransform, ItemOverrideList, ResourceLocation)}, but uses fewer arguments and does not require an instance
   * @param owner          Model configuration
   * @param spriteGetter   Sprite getter function
   * @param transform      Transform to apply to the quad fetching. Should not include rotation or it will look wrong in UIs
   * @param material       Material used, if null uses default
   * @param index          Tint index to use if tinted sprite is used
   * @param overrides      Override instance to use, will either be empty or {@link MaterialOverrideHandler}
   * @return  Baked model
   */
  private static IBakedModel bakeInternal(IModelConfiguration owner, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, TransformationMatrix transform, @Nullable MaterialId material, int index, ItemOverrideList overrides) {
    RenderMaterial texture = owner.resolveTexture("texture");
    int tintIndex = -1;
    TextureAtlasSprite finalSprite = null;
    // if the base material is non-null, try to find the sprite for that material
    if (material != null) {
      // first, find a render info
      Optional<IMaterialRenderInfo> renderInfo = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(material);
      if(renderInfo.isPresent()) {
        // get a list of texture options
        List<RenderMaterial> textureOptions = renderInfo.get().getTextureChoices(texture);
        // find first present texture
        Optional<TextureAtlasSprite> sprite = textureOptions.stream()
                                                            .map(spriteGetter)
                                                            .filter(s -> !MissingTextureSprite.getLocation().equals(s.getName()))
                                                            .findFirst();
        // if something was found, use that sprite
        if(sprite.isPresent()) {
          finalSprite = sprite.get();
          // if the sprite was not the first, set the tint index so it gets colored
          // TODO: want a cleaner way to do this, maybe embed in the texture sprite choices whether to tint?
          if(!finalSprite.getName().equals(textureOptions.get(0).getTextureLocation())) {
            tintIndex = index;
          }
        }
      }
    }

    // if we have no material, or the material failed to fetch, use the default sprite and tint index
    if (finalSprite == null) {
      finalSprite = spriteGetter.apply(texture);
      tintIndex = index;
    }

    // get transform data
    ImmutableMap<TransformType, TransformationMatrix> transformMap = PerspectiveMapWrapper.getTransforms(owner.getCombinedTransform());

    // get quads
    ImmutableList<BakedQuad> quads = ItemLayerModel.getQuadsForSprite(tintIndex, finalSprite, transform);

    // bake model - while the transform may not be identity, it never has rotation so its safe to say untransformed
    return new BakedItemModel(quads, finalSprite, Maps.immutableEnumMap(transformMap), overrides, true, owner.isSideLit());
  }

  @Override
  public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList vanillaOverrides, ResourceLocation modelLocation) {
    // create transforms from offset
    TransformationMatrix transforms;
    if (Vector2f.ZERO.equals(offset)) {
      transforms = TransformationMatrix.identity();
    } else {
      // divide by 16 to convert from pixels to base values
      // negate Y as positive is up for transforms but down for pixels
      transforms = new TransformationMatrix(new Vector3f(offset.x / 16, -offset.y / 16, 0), null, null, null);
    }

    // if the material is already set, no need to set overrides
    ItemOverrideList overrides = ItemOverrideList.EMPTY;
    if (material == null) {
      overrides = new MaterialOverrideHandler(owner, index, transforms);
    }

    // after that its base logic
    return bakeInternal(owner, spriteGetter, transforms, material, index, overrides);
  }

  /**
   * Dynamic override handler to swap in the material texture
   */
  private static final class MaterialOverrideHandler extends ItemOverrideList {
    // contains all the baked models since they'll never change, cleared automatically as the baked model is discarded
    private final Map<MaterialId, IBakedModel> cache = new HashMap<>();

    // parameters needed for rebaking
    private final IModelConfiguration owner;
    private final int index;
    private final TransformationMatrix itemTransform;
    private MaterialOverrideHandler(IModelConfiguration owner, int index, TransformationMatrix itemTransform) {
      this.owner = owner;
      this.index = index;
      this.itemTransform = itemTransform;
    }

    @Override
    public IBakedModel getOverrideModel(IBakedModel originalModel, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
      // fetch the material from the stack
      IMaterial material = IMaterialItem.getMaterialFromStack(stack);
      // if no material on the stack, try to fetch from the tool model
      // TODO: transfer into tool model to safe a ton of effort
      if (material == IMaterial.UNKNOWN) {
        // needs to have a valid index
        int index = this.index;
        if (index < 0) {
          return originalModel;
        }
        // fetch the tool material at the given index
        material = ToolStack.from(stack).getMaterial(index);

        // material must exist
        if (material == IMaterial.UNKNOWN) {
          return originalModel;
        }
      }
      // cache all baked material models, they will not need to be recreated as materials will not change
      return cache.computeIfAbsent(material.getIdentifier(), this::bakeDynamic);
    }

    /**
     * Bakes a copy of this model using the given material
     * @param material  New material for the model
     * @return  Baked model
     */
    private IBakedModel bakeDynamic(MaterialId material) {
      // bake internal does not require an instance to bake, we can pass in whatever material we want
      // use empty override list as the sub model never calls overrides, and already has a material
      return bakeInternal(owner, ModelLoader.defaultTextureGetter(), itemTransform, material, index, ItemOverrideList.EMPTY);
    }
  }

  /**
   * Model loader logic, use {@link #LOADER} to access instance
   */
  private static class Loader implements IModelLoader<MaterialModel> {
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {}

    @Override
    public MaterialModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
      // need tint index for tool models, doubles as part index
      int index = JSONUtils.getInt(modelContents, "index", 0);

      // static material can be defined, if unset uses dynamic material
      MaterialId material = null;
      if (modelContents.has("material")) {
        material = new MaterialId(JSONUtils.getString(modelContents, "material"));
      }

      Vector2f offset = Vector2f.ZERO;
      if (modelContents.has("offset")) {
        offset = arrayToObject(modelContents, "offset");
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
    private static Vector2f arrayToObject(JsonObject json, String name) {
      JsonArray array = JSONUtils.getJsonArray(json, name);
      if (array.size() != 2) {
        throw new JsonParseException("Expected " + 2 + " " + name + " values, found: " + array.size());
      }
      float[] vec = new float[2];
      for(int i = 0; i < 2; ++i) {
        vec[i] = JSONUtils.getFloat(array.get(i), name + "[" + i + "]");
      }
      return new Vector2f(vec[0], vec[1]);
    }
  }
}
