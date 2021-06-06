package slimeknights.tconstruct.library.client.model.tools;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.TinkerRegistries;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Log4j2
@RequiredArgsConstructor
public class ToolModel implements IModelGeometry<ToolModel> {
  /** Shared loader instance */
  public static final Loader LOADER = new Loader();

  /** List of tool parts in this model */
  private final List<ToolPart> toolParts;
  /** If true, this is a large tool and uses double resolution textures in hand */
  private final boolean isLarge;
  /** Transform matrix to apply to child parts */
  private final Vector2f offset;
  /** Location to fetch modifier textures for small variant */
  private final List<ResourceLocation> smallModifierRoots;
  /** Location to fetch modifier textures for large variant */
  private final List<ResourceLocation> largeModifierRoots;

  /** Modifier textures fetched during texture loading */
  @Nullable
  private Map<ResourceLocation,RenderMaterial> smallModifiers = null;
  /** Modifier textures fetched during texture loading */
  @Nullable
  private Map<ResourceLocation,RenderMaterial> largeModifiers = null;

  /**
   * Gets the path to the texture for a given modifier
   * @param modifierRoot  Modifier root location
   * @param modifierId    Specific modifier ID
   * @return  Path to the modifier
   */
  private static RenderMaterial getModifierTexture(ResourceLocation modifierRoot, ResourceLocation modifierId) {
    return ForgeHooksClient.getBlockMaterial(new ResourceLocation(modifierRoot.getNamespace(), modifierRoot.getPath() + modifierId.getNamespace() + "_" + modifierId.getPath()));
  }

  /**
   * Fetches the map of modifier textures
   * @param modifierRoots  List of modifier roots
   * @param allTextures    Texture list for output
   * @return  Map of textures, null if no roots
   */
  @Nullable
  private static Map<ResourceLocation,RenderMaterial> fetchModifierTextures(List<ResourceLocation> modifierRoots, Collection<RenderMaterial> allTextures) {
    if (!modifierRoots.isEmpty()) {
      Map<ResourceLocation,RenderMaterial> textures = new HashMap<>();
      // create two texture adders, so we only log on the final option if missing
      Predicate<RenderMaterial> firstTextureAddder = MaterialModel.getTextureAdder(modifierRoots.get(0), allTextures, false);
      ResourceLocation lastRoot = modifierRoots.get(modifierRoots.size() - 1);
      Predicate<RenderMaterial> lastTextureAdder = MaterialModel.getTextureAdder(lastRoot, allTextures, Config.CLIENT.logMissingModifierTextures.get());
      // loop through all modifiers
      for (ResourceLocation modifier : TinkerRegistries.MODIFIERS.getKeys()) {
        for (ResourceLocation root : modifierRoots) {
          RenderMaterial modifierTexture = getModifierTexture(root, modifier);
          Predicate<RenderMaterial> textureAdder = (root == lastRoot) ? lastTextureAdder : firstTextureAddder;
          // as soon as one root matches, cache that and return
          if (textureAdder.test(modifierTexture)) {
            textures.put(modifier, modifierTexture);
            break;
          }
        }
      }
      return textures;
    } else {
      return null;
    }
  }

  @Override
  public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation,IUnbakedModel> modelGetter, Set<Pair<String,String>> missingTextureErrors) {
    Set<RenderMaterial> allTextures = Sets.newHashSet();
    for (ToolPart part : toolParts) {
      MaterialModel.getMaterialTextures(allTextures, owner, part.getName(false, false), null);
      if (part.hasBroken()) {
        MaterialModel.getMaterialTextures(allTextures, owner, part.getName(true, false), null);
      }
      if (isLarge) {
        MaterialModel.getMaterialTextures(allTextures, owner, part.getName(false, true), null);
        if (part.hasBroken()) {
          MaterialModel.getMaterialTextures(allTextures, owner, part.getName(true, true), null);
        }
      }
    }
    // load in modifier textures
    smallModifiers = fetchModifierTextures(smallModifierRoots, allTextures);
    if (isLarge) {
      largeModifiers = fetchModifierTextures(largeModifierRoots, allTextures);
    } else {
      largeModifiers = null;
    }

    return allTextures;
  }

  /**
   * Adds quads for all relevant modifiers
   * @param spriteGetter      Sprite getter
   * @param modifierTextures  Map of modifier textures
   * @param modifiers         Active modifiers
   * @param builder           Quad list builder
   */
  private static void addModifierQuads(Function<RenderMaterial, TextureAtlasSprite> spriteGetter, @Nullable Map<ResourceLocation,RenderMaterial> modifierTextures, List<ModifierEntry> modifiers, ImmutableList.Builder<BakedQuad> builder, TransformationMatrix transforms) {
    if (modifierTextures != null) {
      for (ModifierEntry entry : modifiers) {
        RenderMaterial modifierTexture = modifierTextures.get(entry.getModifier().getId());
        if (modifierTexture != null) {
          builder.addAll(ItemLayerModel.getQuadsForSprite(-1, spriteGetter.apply(modifierTexture), transforms));
        }
      }
    }
  }

  /**
   * Same as {@link #bake(IModelConfiguration, ModelBakery, Function, IModelTransform, ItemOverrideList, ResourceLocation)}, but uses fewer arguments and does not require an instance
   * @param owner           Model configuration
   * @param spriteGetter    Sprite getter function
   * @param largeTransforms Transform to apply to the large parts. If null, only generates small parts
   * @param parts           List of tool parts in this tool
   * @param materials       Materials to use for the parts
   * @param isBroken        If true, generates broken model
   * @param overrides       Override instance to use, will either be empty or {@link MaterialOverrideHandler}
   * @return  Baked model
   */
  private static IBakedModel bakeInternal(IModelConfiguration owner, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, @Nullable TransformationMatrix largeTransforms,
                                          List<ToolPart> parts, @Nullable Map<ResourceLocation,RenderMaterial> smallModifiers, @Nullable Map<ResourceLocation,RenderMaterial> largeModifiers,
                                          List<MaterialId> materials, List<ModifierEntry> modifiers, boolean isBroken, ItemOverrideList overrides) {
    TextureAtlasSprite particle = null;
    // we create both builders always, though large may be unused
    ImmutableList.Builder<BakedQuad> smallBuilder = ImmutableList.builder();
    ImmutableList.Builder<BakedQuad> largeBuilder = ImmutableList.builder();
    Consumer<ImmutableList<BakedQuad>> smallConsumer;
    Consumer<ImmutableList<BakedQuad>> largeConsumer = largeBuilder::addAll;
    // for large tools, we don't need non-south small quads
    if (largeTransforms != null) {
      smallConsumer = quads -> {
        for (BakedQuad quad : quads) {
          if (quad.getFace() == Direction.SOUTH) {
            smallBuilder.add(quad);
          }
        }
      };
    } else {
      smallConsumer = smallBuilder::addAll;
    }

    // add quads for all parts
    for (ToolPart part : parts) {
      int index = part.getIndex();
      MaterialId material = null;
      if (index < materials.size()) {
        material = materials.get(index);
      }
      // add needed quads
      particle = MaterialModel.getPartQuads(smallConsumer, owner, spriteGetter, TransformationMatrix.identity(), part.getName(isBroken, false), index, material);
      if (largeTransforms != null) {
        MaterialModel.getPartQuads(largeConsumer, owner, spriteGetter, largeTransforms, part.getName(isBroken, true), index, material);
      }
    }
    assert particle != null;

    // add quads for all modifiers
    addModifierQuads(spriteGetter, smallModifiers, modifiers, smallBuilder, TransformationMatrix.identity());
    if (largeTransforms != null) {
      addModifierQuads(spriteGetter, largeModifiers, modifiers, largeBuilder, largeTransforms);
    }

    // bake model - while the transform may not be identity, it never has rotation so its safe to say untransformed
    ImmutableMap<TransformType, TransformationMatrix> transformMap = Maps.immutableEnumMap(PerspectiveMapWrapper.getTransforms(owner.getCombinedTransform()));

    // large models use a custom model here
    if (largeTransforms != null) {
      return new BakedLargeToolModel(largeBuilder.build(), smallBuilder.build(), particle, transformMap, overrides, owner.isSideLit());
    }
    // for small, we leave out the large quads, so the baked item model logic is sufficient
    return new BakedItemModel(smallBuilder.build(), particle, transformMap, overrides, true, owner.isSideLit());
  }

  @Override
  public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial,TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
    TransformationMatrix largeTransforms = isLarge ? new TransformationMatrix(new Vector3f((offset.x - 8) / 32, (-offset.y - 8) / 32, 0), null, new Vector3f(2, 2, 1), null) : null;
    overrides = new MaterialOverrideHandler(owner, toolParts, largeTransforms, smallModifiers, largeModifiers); // TODO: nest original overrides?
    // bake the original with no modifiers or materials
    return bakeInternal(owner, spriteGetter, largeTransforms,
                        toolParts, smallModifiers, largeModifiers,
                        Collections.emptyList(), Collections.emptyList(), false, overrides);
  }

  /** Data class for a single tool part */
  @RequiredArgsConstructor
  private static class ToolPart {
    private final String name;
    @Getter
    private final int index;
    @Nullable
    private final String broken;

    /** If true, this part has a broken texture */
    public boolean hasBroken() {
      return broken != null;
    }

    /**
     * Gets the name for this part
     * @param isBroken  If true, this part is broken
     * @param isLarge   If true, rendering a large tool
     * @return  Texture name for this part
     */
    public String getName(boolean isBroken, boolean isLarge) {
      String name = this.name;
      if (isBroken && broken != null) {
        name = broken;
      }
      if (isLarge) {
        name = "large_" + name;
      }
      return name;
    }

    /** Reads a part from JSON */
    public static ToolPart read(JsonObject json) {
      String name = JSONUtils.getString(json, "name");
      int index = JSONUtils.getInt(json, "index");
      String broken = null;
      if (json.has("broken")) {
        broken = JSONUtils.getString(json, "broken");
      }
      return new ToolPart(name, index, broken);
    }
  }

  /**
   * Dynamic override handler to swap in the material texture
   */
  private static final class MaterialOverrideHandler extends ItemOverrideList {
    // contains all the baked models since they'll never change, cleared automatically as the baked model is discarded
    private final Cache<ToolCacheKey, IBakedModel> cache = CacheBuilder
      .newBuilder()
      .maximumSize(128)
      .build();

    // parameters needed for rebaking
    private final IModelConfiguration owner;
    private final List<ToolPart> toolParts;
    @Nullable
    private final TransformationMatrix largeTransforms;
    @Nullable
    private final Map<ResourceLocation,RenderMaterial> smallModifiers;
    @Nullable
    private final Map<ResourceLocation,RenderMaterial> largeModifiers;

    private MaterialOverrideHandler(IModelConfiguration owner, List<ToolPart> toolParts, @Nullable TransformationMatrix largeTransforms, @Nullable Map<ResourceLocation,RenderMaterial> smallModifiers, @Nullable Map<ResourceLocation,RenderMaterial> largeModifiers) {
      this.owner = owner;
      this.toolParts = toolParts;
      this.largeTransforms = largeTransforms;
      this.smallModifiers = smallModifiers;
      this.largeModifiers = largeModifiers;
    }

    /**
     * Bakes a copy of this model using the given material
     * @param materials  New materials for the model
     * @return  Baked model
     */
    private IBakedModel bakeDynamic(List<MaterialId> materials, List<ModifierEntry> modifiers, boolean isBroken) {
      // bake internal does not require an instance to bake, we can pass in whatever material we want
      // use empty override list as the sub model never calls overrides, and already has a material
      return bakeInternal(owner, ModelLoader.defaultTextureGetter(), largeTransforms,
                          toolParts, smallModifiers, largeModifiers,
                          materials, modifiers, isBroken, ItemOverrideList.EMPTY);
    }

    @Override
    public IBakedModel getOverrideModel(IBakedModel originalModel, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
      List<MaterialId> materialIds = MaterialIdNBT.from(stack).getMaterials();
      List<ModifierEntry> modifiers = ModifierNBT.fromUpgrades(stack).getModifiers();
      boolean broken = ToolDamageUtil.isBroken(stack);

      // if nothing unique, render original
      if (!broken && materialIds.isEmpty() && modifiers.isEmpty()) {
        return originalModel;
      }
      // render special model
      try {
        return cache.get(new ToolCacheKey(materialIds, modifiers, broken), () -> bakeDynamic(materialIds, modifiers, broken));
      } catch (ExecutionException e) {
        log.error(e);
        return originalModel;
      }
    }
  }

  /** Baked model for large tools, has separate quads in GUIs */
  private static class BakedLargeToolModel implements IBakedModel {
    private final ImmutableList<BakedQuad> largeQuads;
    @Getter
    private final TextureAtlasSprite particleTexture;
    private final ImmutableMap<TransformType, TransformationMatrix> transforms;
    @Getter
    private final ItemOverrideList overrides;
    @Getter
    private final boolean isSideLit;
    private final IBakedModel guiModel;

    private BakedLargeToolModel(ImmutableList<BakedQuad> largeQuads, ImmutableList<BakedQuad> smallQuads, TextureAtlasSprite particle, ImmutableMap<TransformType,TransformationMatrix> transforms, ItemOverrideList overrides, boolean isSideLit) {
      this.largeQuads = largeQuads;
      this.particleTexture = particle;
      this.transforms = transforms;
      this.overrides = overrides;
      this.isSideLit = isSideLit;
      this.guiModel = new BakedLargeToolGui(this, smallQuads);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
      if (side == null) {
        return largeQuads;
      }
      return ImmutableList.of();
    }

    @Override
    public IBakedModel handlePerspective(TransformType type, MatrixStack mat) {
      if (type == TransformType.GUI) {
        return this.guiModel.handlePerspective(type, mat);
      }
      return PerspectiveMapWrapper.handlePerspective(this, transforms, type, mat);
    }

    /* Misc properties */

    @Override
    public boolean isAmbientOcclusion() {
      return true;
    }

    @Override
    public boolean isGui3d() {
      return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
      return false;
    }
  }

  /** Baked model for large tools in the GUI, small quads */
  private static class BakedLargeToolGui extends BakedModelWrapper<BakedLargeToolModel> {
    private final List<BakedQuad> guiQuads;
    public BakedLargeToolGui(BakedLargeToolModel model, List<BakedQuad> guiQuads) {
      super(model);
      this.guiQuads = guiQuads;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
      // these quads are only shown in handle perspective for GUI
      if (side == null) {
        return guiQuads;
      }
      return ImmutableList.of();
    }

    @Override
    public boolean doesHandlePerspectives() {
      return true;
    }

    @Override
    public IBakedModel handlePerspective(TransformType transform, MatrixStack mat) {
      return PerspectiveMapWrapper.handlePerspective(this, originalModel.transforms, transform, mat);
    }
  }

  /**
   * Model loader logic, use {@link #LOADER} to access instance
   */
  private static class Loader implements IModelLoader<ToolModel> {
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {}

    @Override
    public ToolModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
      List<ToolPart> parts = JsonHelper.parseList(modelContents, "parts", ToolPart::read);
      boolean isLarge = JSONUtils.getBoolean(modelContents, "large", false);
      Vector2f offset = Vector2f.ZERO;
      if (modelContents.has("large_offset")) {
        offset = MaterialModel.arrayToObject(modelContents, "large_offset");
      }
      // modifier root fetching
      List<ResourceLocation> smallModifierRoots = Collections.emptyList();
      List<ResourceLocation> largeModifierRoots = Collections.emptyList();
      if (modelContents.has("modifier_roots")) {
        // large model requires an object
        if (isLarge) {
          JsonObject modifierRoots = JSONUtils.getJsonObject(modelContents, "modifier_roots");
          BiFunction<JsonElement,String,ResourceLocation> parser = (element, string) -> new ResourceLocation(JSONUtils.getString(element, string));
          smallModifierRoots = JsonHelper.parseList(modifierRoots, "small", parser);
          largeModifierRoots = JsonHelper.parseList(modifierRoots, "large", parser);
        } else {
          // small requires an array
          smallModifierRoots = JsonHelper.parseList(modelContents, "modifier_roots", (element, string) -> new ResourceLocation(JSONUtils.getString(element, string)));
        }
      }
      return new ToolModel(parts, isLarge, offset, smallModifierRoots, largeModifierRoots);
    }
  }

  /** Simple data class to cache built tool modifiers, contains everything unique in the textures */
  @Data
  private static class ToolCacheKey {
    private final List<MaterialId> materials;
    private final List<ModifierEntry> modifiers;
    private final boolean broken;
  }
}
