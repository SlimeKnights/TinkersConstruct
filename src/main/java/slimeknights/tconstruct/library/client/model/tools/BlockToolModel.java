package slimeknights.tconstruct.library.client.model.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.model.util.ColoredBlockModel;
import slimeknights.mantle.client.model.util.DynamicBakedWrapper;
import slimeknights.mantle.client.model.util.ExtraTextureContext;
import slimeknights.mantle.client.model.util.SimpleBlockModel;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.array.ArrayLoadable;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo.TintedSprite;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.client.model.ModelProperties;
import slimeknights.tconstruct.library.client.modifiers.IBakedModifierModel;
import slimeknights.tconstruct.library.client.modifiers.ModifierModelMap;
import slimeknights.tconstruct.library.client.modifiers.block.BlockModifierModelMapManager;
import slimeknights.tconstruct.library.client.modifiers.model.ModifierModel;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.worktable.ModifierSetWorktableRecipe;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@SuppressWarnings({ "null" })
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public class BlockToolModel implements IUnbakedGeometry<BlockToolModel> {
  @Nonnull
  public static final IGeometryLoader<BlockToolModel> LOADER = BlockToolModel::deserialize;

  private static final Loadable<List<Set<String>>> PARTS = StringLoadable.DEFAULT.set(ArrayLoadable.COMPACT_OR_EMPTY)
      .list(1);

  public static BlockToolModel deserialize(JsonObject json, JsonDeserializationContext context) {
    SimpleBlockModel model = SimpleBlockModel.deserialize(json, context);
    List<Set<String>> parts = PARTS.getIfPresent(json, "parts");
    // modifier root fetching
    List<ResourceLocation> modifierModels = List.of();
    if (json.has("modifier_maps")) {
      modifierModels = JsonHelper.parseList(json, "modifier_maps", Loadables.RESOURCE_LOCATION);
    }
    boolean showTraits = GsonHelper.getAsBoolean(json, "show_traits", false);
    return new BlockToolModel(model, parts, modifierModels, showTraits);
  }

  private final SimpleBlockModel model;
  private final List<Set<String>> parts;
  private final List<ResourceLocation> modelLocation;
  private final boolean showTraits;

  @Override
  public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
    model.resolveParents(modelGetter, context);
  }

  @Override
  public BakedModel bake(IGeometryBakingContext owner, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation location) {
    BakedModel baked = model.bake(owner, baker, spriteGetter, transform, overrides, location);
    // load modifier models
    ModifierModelMap modifierModels = BlockModifierModelMapManager.INSTANCE.getModelsForTool(spriteGetter, modelLocation);
    boolean particleRetextured = parts.stream().anyMatch(set -> set.contains("particle"));
    return new BakedBlockTool(baked, owner, model, transform, parts, particleRetextured, overrides, modifierModels, showTraits);
  }

  private static BakedModel bakeInternal(Function<Material, TextureAtlasSprite> spriteGetter, BakedBlockTool baked, ModifierModelMap modifierModels, boolean showTraits, MaterialIdNBT materials, @Nullable IToolStackView tool) {
    Transformation transforms = Transformation.identity();
    ItemLayerPixels pixels = new ItemLayerPixels();
    SimpleBakedModel.Builder modelBuilder = baked.getBuilderWithMaterials(materials);
    if (tool != null && !modifierModels.isEmpty()) {
      // reversed order to ensure the pixels is updated correctly, not required for 3d tools then
      List<ModifierEntry> modifiers = (showTraits ? tool.getModifiers() : tool.getUpgrades()).getModifiers();
      // keep track of the entry for each first modifier, as that may impact how it renders
      if (!modifiers.isEmpty()) {
        // add all regular modifiers
        Set<ModifierId> hidden = ModifierSetWorktableRecipe.getModifierSet(tool.getPersistentData(), TConstruct.getResource("invisible_modifiers"));
        for (int i = modifiers.size() - 1; i >= 0; i--) {
          ModifierEntry entry = modifiers.get(i);
          ModifierId modifier = entry.getModifier().getId();
          if (!hidden.contains(modifier)) {
            IBakedModifierModel model = modifierModels.get(modifier);
            if (model != null) {
              baked.getCachedModifierModel(model.getCacheKey(tool, entry), () -> {
                List<BakedQuad> quads = new ArrayList<>();
                model.addQuads(tool, entry, spriteGetter, transforms, false, 0, quads::addAll, pixels);
                return quads;
              }).forEach(modelBuilder::addUnculledFace);
            }
          }
        }
      }
      // iterate the constant models in order for simplicity, you can just reverse the order yourself I guess
      for (ModifierModel model : modifierModels.constant().values()) {
        baked.getCachedModifierModel(model.getCacheKey(tool, ModifierEntry.EMPTY), () -> {
          List<BakedQuad> quads = new ArrayList<>();
          model.addQuads(tool, ModifierEntry.EMPTY, spriteGetter, transforms, false, 0, quads::addAll, pixels);
          return quads;
        }).forEach(modelBuilder::addUnculledFace);
      }
    }
    return modelBuilder.build(SimpleBlockModel.getRenderTypeGroup(baked.owner));
  }

  private static class BakedBlockTool extends DynamicBakedWrapper<BakedModel> {
    /** Dynamic overrides that rebake the model with material and modifier data from the ItemStack */
    // extends NestedOverrides so an override hit (e.g. placed model switching) also re-resolves the target model's
    // own overrides (guarded by ignoreNested), applying its material/modifier baking instead of dropping it
    private static class ToolOverrides extends NestedOverrides {
      private final BakedBlockTool baked;
      private final boolean showTraits;
      private final ModifierModelMap modifierModels;

      public ToolOverrides(BakedBlockTool baked, ItemOverrides nested, boolean showTraits, ModifierModelMap modifierModels) {
        super(nested);
        this.baked = baked;
        this.showTraits = showTraits;
        this.modifierModels = modifierModels;
      }

      @Nullable
      @Override
      public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
        BakedModel resolved = super.resolve(originalModel, stack, world, entity, seed);
        if (resolved != originalModel) {
          return resolved;
        }
        if (stack.isEmpty() || !stack.hasTag()) {
          return originalModel;
        }
        // fetch materials from the ItemStack
        MaterialIdNBT materials = MaterialIdNBT.from(stack);
        IToolStackView tool = ToolStack.from(stack);

        // skip rebaking if no special data
        ModifierNBT modifiers = showTraits ? tool.getModifiers() : tool.getUpgrades();
        if (materials.getMaterials().isEmpty() && modifiers.isEmpty()) {
          return originalModel;
        }
        // build cache key from modifier cache keys, each modifier provides its own
        ImmutableList.Builder<Object> cacheBuilder = ImmutableList.builder();
        Set<ModifierId> hidden = ModifierSetWorktableRecipe.getModifierSet(tool.getPersistentData(), TConstruct.getResource("invisible_modifiers"));
        for (ModifierEntry entry : modifiers) {
          ModifierId id = entry.getId();
          if (!hidden.contains(id)) {
            IBakedModifierModel bakedModifierModel = modifierModels.get(id);
            if (bakedModifierModel != null) {
              Object caches = bakedModifierModel.getCacheKey(tool, entry);
              if (caches != null) {
                cacheBuilder.add(caches);
              }
            }
          }
        }

        // rebake the special model
        try {
          return baked.thisCache.get(new ToolCacheKey(materials, cacheBuilder.build()),
              () -> bakeInternal(Material::sprite, baked, modifierModels, showTraits, materials, tool));
        } catch (ExecutionException e) {
          log.error("Failed to get tool model from cache", e);
          return originalModel;
        }
      }

    }

    /** Shared bake location used for dynamic rebaking */
    private static final ResourceLocation BAKE_LOCATION = Mantle.getResource("material_tool_dynamic");

    private final IGeometryBakingContext owner;
    private final SimpleBlockModel model;
    private final ModelState transform;
    private final List<Set<String>> parts;
    private final boolean particleRetextured;
    private final ItemOverrides overrides;
    private final Cache<ToolCacheKey, BakedModel> thisCache = CacheBuilder.newBuilder()
        .maximumSize(MaterialRenderInfoLoader.INSTANCE.getAllRenderInfos().size() * 3L / 2)
        .build();
    private final Cache<Object, List<BakedQuad>> modifierCache = CacheBuilder
        .newBuilder().maximumSize(MaterialRenderInfoLoader.INSTANCE.getAllRenderInfos().size() * 3L / 2)
        .build();

    public ItemOverrides getOverrides() {
      return overrides;
    }

    private SimpleBakedModel.Builder getBuilder(MaterialIdNBT materials, Function<Material, TextureAtlasSprite> spriteGetter, Map<String, TintedSprite> tints, IGeometryBakingContext retextureContext) {
      TextureAtlasSprite particle = spriteGetter.apply(owner.getMaterial("particle"));
      SimpleBakedModel.Builder builder = SimpleBlockModel.bakedBuilder(owner, originalModel.getOverrides()).particle(particle);
      List<BlockElement> elements = model.getElements();
      int size = elements.size();
      IQuadTransformer quadTransformer = SimpleBlockModel.applyTransform(transform, owner.getRootTransform());
      Transformation transformation = transform.getRotation();
      boolean uvlock = transform.isUvLocked();
      for (int i = 0; i < size; i++) {
        BlockElement part = elements.get(i);
        // check if any face of this element needs tinting
        TintedSprite tint = null;
        for (BlockElementFace face : part.faces.values()) {
          TintedSprite faceTint = tints.get(face.texture);
          if (faceTint != null) {
            tint = faceTint;
            break;
          }
        }
        if (tint != null) {
          IQuadTransformer partTransformer = tint.color() == -1 ? quadTransformer
              : quadTransformer.andThen(ColoredBlockModel.applyColorQuadTransformer(tint.color()));
          ColoredBlockModel.bakePart(builder, retextureContext, part, tint.emissivity(), spriteGetter,
              transformation, partTransformer, uvlock, BAKE_LOCATION);
        } else {
          SimpleBlockModel.bakePart(builder, retextureContext, part, spriteGetter, transform, quadTransformer,
              BAKE_LOCATION);
        }
      }
      return builder;
    }

    /** Data holder for material baking context. */
    private record MaterialBakingContext(Map<String, Material> replacements, Map<String, TintedSprite> tints, IGeometryBakingContext retextureContext) {}

    /** Fills replacements and tints from the given materials. */
    private MaterialBakingContext fillMaterials(MaterialIdNBT materials) {
      Map<String, Material> replacements = new HashMap<>();
      Map<String, TintedSprite> tints = new HashMap<>();
      Function<Material, TextureAtlasSprite> spriteGetter = Material::sprite;
      for (int i = 0; i < parts.size(); i++) {
        fetchMaterial(materials.getMaterial(i), parts.get(i), spriteGetter, replacements, tints);
      }
      return new MaterialBakingContext(replacements, tints, new ExtraTextureContext(owner, replacements));
    }

    private SimpleBakedModel.Builder getBuilderWithMaterials(MaterialIdNBT materials) {
      MaterialBakingContext ctx = fillMaterials(materials);
      return getBuilder(materials, Material::sprite, ctx.tints, ctx.retextureContext);
    }

    /**
     * Rebakes the model with the given materials
     *
     * @param materials materials to use for texture replacement
     * @return baked model with the given materials applied
     */
    private BakedModel bakeWithMaterials(MaterialIdNBT materials) {
      MaterialBakingContext ctx = fillMaterials(materials);
      if (ctx.replacements.isEmpty()) {
        return originalModel;
      }
      if (ctx.tints.isEmpty()) {
        return model.bakeDynamic(ctx.retextureContext, transform);
      }
      return getBuilder(materials, Material::sprite, ctx.tints, ctx.retextureContext).build(SimpleBlockModel.getRenderTypeGroup(owner));
    }

    /**
     * Gets or bakes a cached model for the given key
     *
     * @param key cache key
     * @return baked model
     */
    private BakedModel getCachedModel(ToolCacheKey key) {
      if (key.materials().getMaterials().isEmpty() && key.modifierData().isEmpty()) {
        return originalModel;
      }
      try {
        return thisCache.get(key, () -> bakeWithMaterials(key.materials()));
      } catch (ExecutionException e) {
        log.error("Failed to get tool model from cache", e);
        return originalModel;
      }
    }

    /**
     * Gets or computes cached quads for a modifier model
     *
     * @param key    cache key from the modifier model
     * @param supplier supplier to compute the quads if not cached
     * @return list of baked quads
     */
    private List<BakedQuad> getCachedModifierModel(Object key, Callable<List<BakedQuad>> supplier) {
      try {
        return modifierCache.get(key, supplier);
      } catch (ExecutionException e) {
        log.error("Failed to get modifier model from cache", e);
        return List.of();
      }
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data) {
      if (particleRetextured) {
        MaterialIdNBT mat = data.get(ModelProperties.MATERIALS);
        if (mat != null) {
          ToolCacheKey key = new ToolCacheKey(mat, List.of());
          return getCachedModel(key).getParticleIcon(data);
        }
      }
      return originalModel.getParticleIcon(data);
    }

    @Nonnull
    @Override
    public BakedModel applyTransform(@Nonnull ItemDisplayContext cameraTransformType, @Nonnull PoseStack poseStack, boolean applyLeftHandTransform) {
      super.applyTransform(cameraTransformType, poseStack, applyLeftHandTransform);
      return this;
    }

    @Override
    public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
      return List.of(this);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
      MaterialIdNBT mat = extraData.get(ModelProperties.MATERIALS);
      if (mat != null) {
        return getCachedModel(new ToolCacheKey(mat, List.of())).getQuads(state, side, rand, extraData, renderType);
      }
      return originalModel.getQuads(state, side, rand, extraData, renderType);
    }

    private void fetchMaterial(MaterialVariantId material, Set<String> retextured, Function<Material, TextureAtlasSprite> spriteGetter, Map<String, Material> replacements, Map<String, TintedSprite> tints) {
      Optional<MaterialRenderInfo> optional = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(material);
      if (optional.isPresent()) {
        MaterialRenderInfo info = optional.get();
        Map<Material, TintedSprite> seen = new HashMap<>();
        for (String name : retextured) {
          Material texture = owner.getMaterial(name);
          TintedSprite tinted = seen.get(texture);
          if (tinted == null) {
            tinted = info.getSprite(texture, spriteGetter);
            seen.put(texture, tinted);
          }
          TextureAtlasSprite sprite = tinted.sprite();
          replacements.put(name, new Material(sprite.atlasLocation(), sprite.contents().name()));
          if (tinted.color() != -1 || tinted.emissivity() > 0) {
            tints.put('#' + name, tinted);
          }
        }
      }
    }

    private BakedBlockTool(BakedModel original, IGeometryBakingContext owner, SimpleBlockModel model, ModelState transform, List<Set<String>> parts, boolean particleRetextured, ItemOverrides nestedOverrides, ModifierModelMap modifierModels, boolean showTraits) {
      super(original);
      this.owner = owner;
      this.model = model;
      this.transform = transform;
      this.parts = parts;
      this.particleRetextured = particleRetextured;

      this.overrides = new ToolOverrides(this, nestedOverrides, showTraits, modifierModels);
    }
  }

  private record ToolCacheKey(MaterialIdNBT materials, List<Object> modifierData) {
  }
}
