package slimeknights.tconstruct.library.client.model.tools;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import slimeknights.mantle.client.model.util.MantleItemLayerModel;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.ReversedListBuilder;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo.TintedSprite;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.client.model.BakedUniqueGuiModel;
import slimeknights.tconstruct.library.client.modifiers.IBakedModifierModel;
import slimeknights.tconstruct.library.client.modifiers.ModifierModelManager;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.worktable.ModifierSetWorktableRecipe;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Model handling all tools, both multipart and non.
 * TODO: migrate broken to an override
 */
public class ToolModel implements IUnbakedGeometry<ToolModel> {
  /** Shared loader instance */
  public static final IGeometryLoader<ToolModel> LOADER = ToolModel::deserialize;

  /** Color handler instance for all tools, handles both material and modifier colors */
  public static final ItemColor COLOR_HANDLER = (stack, index) -> {
    // TODO: reconsider material item colors, is there a usecase for dynamic colors as opposed to just an animated texture?
    if (index >= 0) {
      // for modifiers, we need the overrides instance to properly process
      BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(stack.getItem());
      if (itemModel != null && itemModel.getOverrides() instanceof MaterialOverrideHandler overrides) {
        ToolStack tool = ToolStack.from(stack);
        // modifier model indexes start at the last part
        int localIndex = 0;
        List<ModifierEntry> modifiers = tool.getUpgrades().getModifiers();
        for (int i = modifiers.size() - 1; i >= 0; i--) {
          ModifierEntry entry = modifiers.get(i);
          // colors are assumed to not be sensitive to the model's large status
          IBakedModifierModel modifierModel = overrides.getModifierModel(entry.getModifier());
          if (modifierModel != null) {
            // indexes from [0,modelIndexes) are passed to this model
            // if below the range, make the index model relative
            // if above the range, add the count and let the next model handle it
            int modelIndexes = modifierModel.getTintIndexes();
            if (localIndex + modelIndexes > index) {
              return modifierModel.getTint(tool, entry, index - localIndex);
            }
            localIndex += modelIndexes;
          }
        }
      }
    }
    return -1;
  };

  /**
   * Registers an item color handler for a part item
   * @param colors  Item colors instance
   * @param item    Material item
   */
  @SuppressWarnings("deprecation")  // yeah forge, you have nice event, this is happening during the event so its fine
  public static void registerItemColors(ItemColors colors, Supplier<? extends IModifiable> item) {
    colors.register(ToolModel.COLOR_HANDLER, item.get());
  }

  /** Deserializes the model from JSON */
  public static ToolModel deserialize(JsonObject json, JsonDeserializationContext context) {
    List<ToolPart> parts = Collections.emptyList();
    if (json.has("parts")) {
      parts = JsonHelper.parseList(json, "parts", ToolPart::read);
    }
    boolean isLarge = GsonHelper.getAsBoolean(json, "large", false);
    Vec2 offset = Vec2.ZERO;
    if (json.has("large_offset")) {
      offset = MaterialModel.getVec2(json, "large_offset");
    }
    // modifier root fetching
    List<ResourceLocation> smallModifierRoots = Collections.emptyList();
    List<ResourceLocation> largeModifierRoots = Collections.emptyList();
    if (json.has("modifier_roots")) {
      // large model requires an object
      if (isLarge) {
        JsonObject modifierRoots = GsonHelper.getAsJsonObject(json, "modifier_roots");
        BiFunction<JsonElement,String,ResourceLocation> parser = (element, string) -> new ResourceLocation(GsonHelper.convertToString(element, string));
        smallModifierRoots = JsonHelper.parseList(modifierRoots, "small", parser);
        largeModifierRoots = JsonHelper.parseList(modifierRoots, "large", parser);
      } else {
        // small requires an array
        smallModifierRoots = JsonHelper.parseList(json, "modifier_roots", (element, string) -> new ResourceLocation(GsonHelper.convertToString(element, string)));
      }
    }
    // modifiers first
    List<ModifierId> firstModifiers = Collections.emptyList();
    if (json.has("first_modifiers")) {
      firstModifiers = JsonHelper.parseList(json, "first_modifiers", ModifierId::convertFromJson);
    }
    return new ToolModel(parts, isLarge, offset, smallModifierRoots, largeModifierRoots, firstModifiers);
  }

  /** List of tool parts in this model */
  private List<ToolPart> toolParts;
  /** If true, this is a large tool and uses double resolution textures in hand */
  private final boolean isLarge;
  /** Transform matrix to apply to child parts */
  private final Vec2 offset;
  /** Location to fetch modifier textures for small variant */
  private final List<ResourceLocation> smallModifierRoots;
  /** Location to fetch modifier textures for large variant */
  private final List<ResourceLocation> largeModifierRoots;
  /** Modifiers that show first on tools, bypassing normal sort order */
  private final List<ModifierId> firstModifiers;
  /** Models for the relevant modifiers */
  private Map<ModifierId,IBakedModifierModel> modifierModels = Collections.emptyMap();

  public ToolModel(List<ToolPart> parts, boolean isLarge, Vec2 offset, List<ResourceLocation> smallModifierRoots, List<ResourceLocation> largeModifierRoots, List<ModifierId> firstModifiers) {
    this.toolParts = parts;
    this.isLarge = isLarge;
    this.offset = offset;
    this.smallModifierRoots = smallModifierRoots;
    this.largeModifierRoots = largeModifierRoots;
    this.firstModifiers = firstModifiers;
  }

  @Override
  public Collection<Material> getMaterials(IGeometryBakingContext owner, Function<ResourceLocation,UnbakedModel> modelGetter, Set<Pair<String,String>> missingTextureErrors) {
    Set<Material> allTextures = Sets.newHashSet();
    if (toolParts.isEmpty()) {
      // we assume if you provide small broken, you also want large broken to simplify things
      if (owner.hasMaterial(ToolPart.DEFAULT.broken)) {
        toolParts = ToolPart.DEFAULT_PARTS;
      } else {
        toolParts = ToolPart.NO_BROKEN_PARTS;
      }
    }

    // after the above condition, we always have parts, so just iterate them
    for (ToolPart part : toolParts) {
      // if material variants, fetch textures from the material model
      if (part.hasMaterials()) {
        MaterialModel.getMaterialTextures(allTextures, owner, part.getName(false, false), null);
        // TODO: move broken to an override instead of all this special casing
        if (part.hasBroken()) {
          MaterialModel.getMaterialTextures(allTextures, owner, part.getName(true, false), null);
        }
        if (isLarge) {
          MaterialModel.getMaterialTextures(allTextures, owner, part.getName(false, true), null);
          if (part.hasBroken()) {
            MaterialModel.getMaterialTextures(allTextures, owner, part.getName(true, true), null);
          }
        }
      } else {
        // static texture
        allTextures.add(owner.getMaterial(part.getName(false, false)));
        if (part.hasBroken()) {
          allTextures.add(owner.getMaterial(part.getName(true, false)));
        }
        if (isLarge) {
          allTextures.add(owner.getMaterial(part.getName(false, true)));
          if (part.hasBroken()) {
            allTextures.add(owner.getMaterial(part.getName(true, true)));
          }
        }
      }
    }
    // load modifier models
    modifierModels = ModifierModelManager.getModelsForTool(smallModifierRoots, isLarge ? largeModifierRoots : Collections.emptyList(), allTextures);

    return allTextures;
  }

  /**
   * adds quads for relevant modifiers
   * @param spriteGetter    Sprite getter instance
   * @param modifierModels  Map of modifier models
   * @param tool            Tool instance
   * @param quadConsumer    Consumer for finished quads
   * @param transforms      Transforms to apply
   * @param isLarge         If true, the quads are for a large tool
   */
  private static void addModifierQuads(Function<Material, TextureAtlasSprite> spriteGetter, Map<ModifierId,IBakedModifierModel> modifierModels, List<ModifierId> firstModifiers, IToolStackView tool, Consumer<ImmutableList<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels, Transformation transforms, boolean isLarge) {
    if (!modifierModels.isEmpty()) {
      // keep a running tint index so models know where they should start, currently starts at 0 as the main model does not use tint indexes
      int modelIndex = 0;
      // reversed order to ensure the pixels is updated correctly
      List<ModifierEntry> modifiers = tool.getUpgrades().getModifiers();
      if (!modifiers.isEmpty()) {
        // last, add all regular modifiers
        FirstModifier[] firsts = new FirstModifier[firstModifiers.size()];
        Set<ModifierId> hidden = ModifierSetWorktableRecipe.getModifierSet(tool.getPersistentData(), TConstruct.getResource("invisible_modifiers"));
        for (int i = modifiers.size() - 1; i >= 0; i--) {
          ModifierEntry entry = modifiers.get(i);
          ModifierId modifier = entry.getModifier().getId();
          if (!hidden.contains(modifier)) {
            IBakedModifierModel model = modifierModels.get(modifier);
            if (model != null) {
              // if the modifier is in the list, delay adding its quads, but keep the expected tint index
              int index = firstModifiers.indexOf(modifier);
              if (index == -1) {
                quadConsumer.accept(model.getQuads(tool, entry, spriteGetter, transforms, isLarge, modelIndex, pixels));
              } else {
                firsts[index] = new FirstModifier(entry, model, modelIndex);
              }
              modelIndex += model.getTintIndexes();
            }
          }
        }
        // first, add the first modifiers
        for (int i = firsts.length - 1; i >= 0; i--) {
          FirstModifier first = firsts[i];
          if (first != null) {
            quadConsumer.accept(first.model.getQuads(tool, first.entry, spriteGetter, transforms, isLarge, first.modelIndex, pixels));
          }
        }
      }
    }
  }

  /** Record for a first modifier in the model */
  private record FirstModifier(ModifierEntry entry, IBakedModifierModel model, int modelIndex) {}

  /** Filters the list of baked quads to just south quads to simplify the GUI */
  private static List<BakedQuad> filterToGuiQuads(List<BakedQuad> quads) {
    return quads.stream().filter(quad -> quad.getDirection() == Direction.SOUTH).toList();
  }

  /** Makes a model builder for the given context and overrides */
  private static IModelBuilder<?> makeModelBuilder(IGeometryBakingContext context, ItemOverrides overrides, TextureAtlasSprite particle) {
    return IModelBuilder.of(context.useAmbientOcclusion(), context.useBlockLight(), context.isGui3d(), context.getTransforms(), overrides, particle, MantleItemLayerModel.getDefaultRenderType(context));
  }

  /**
   * Same as {@link #bake(IGeometryBakingContext, ModelBakery, Function, ModelState, ItemOverrides, ResourceLocation)}, but uses fewer arguments and does not require an instance
   * @param owner           Model configuration
   * @param spriteGetter    Sprite getter function
   * @param largeTransforms Transform to apply to the large parts. If null, only generates small parts
   * @param parts           List of tool parts in this tool
   * @param modifierModels  Map of modifier models for this tool
   * @param materials       Materials to use for the parts
   * @param tool            Tool instance for modifier parsing
   * @param overrides       Override instance to use, will either be empty or {@link MaterialOverrideHandler}
   * @return  Baked model
   */
  private static BakedModel bakeInternal(IGeometryBakingContext owner, Function<Material, TextureAtlasSprite> spriteGetter, @Nullable Transformation largeTransforms,
                                         List<ToolPart> parts, Map<ModifierId,IBakedModifierModel> modifierModels, List<ModifierId> firstModifiers,
                                         List<MaterialVariantId> materials, @Nullable IToolStackView tool, ItemOverrides overrides) {
    Transformation smallTransforms = Transformation.identity();

    // TODO: would be nice to support render types per material/per modifier
    // builder for the GUI model, receives only south quads
    ReversedListBuilder<Collection<BakedQuad>> guiQuads = new ReversedListBuilder<>();
    // builder for the full model, receives all quads
    ReversedListBuilder<Collection<BakedQuad>> fullQuads = new ReversedListBuilder<>();
    // logic to track which pixels are used, only needed for the full model (which will be the large model if large is enabled)
    ItemLayerPixels pixels = new ItemLayerPixels();

    // add quads for all modifiers first, for the sake of the item layer pixels
    if (tool != null && !modifierModels.isEmpty()) {
      // TODO: tell the modifier models directly to not ship non-south quads instead of filtering
      Consumer<ImmutableList<BakedQuad>> guiConsumer = quads -> guiQuads.add(filterToGuiQuads(quads));
      // if we have a large model, that means we will fetech models twice, once for large then again for small
      if (largeTransforms != null) {
        addModifierQuads(spriteGetter, modifierModels, firstModifiers, tool, guiConsumer, null, smallTransforms, false);
        addModifierQuads(spriteGetter, modifierModels, firstModifiers, tool, fullQuads::add, pixels, largeTransforms, true);
      } else {
        // in small, we load a single set which both consumers receive
        addModifierQuads(spriteGetter, modifierModels, firstModifiers, tool, quads -> {
          guiConsumer.accept(quads);
          fullQuads.add(quads);
        }, null, Transformation.identity(), false);
      }
    }

    // add quads for all parts
    boolean isBroken = tool != null && tool.isBroken();
    TextureAtlasSprite particle = null;
    for (int i = parts.size() - 1; i >= 0; i--) {
      ToolPart part = parts.get(i);

      // part with materials
      if (part.hasMaterials()) {
        // start by fetching the material we are rendering at this position, should only be null on invalid tools or during the initial bake
        int index = part.index();
        MaterialVariantId material = index < materials.size() ? materials.get(index) : null;
        TintedSprite materialSprite = MaterialModel.getMaterialSprite(spriteGetter, owner.getMaterial(part.getName(isBroken, false)), material);
        particle = materialSprite.sprite();

        // same drill as above, large means more quad fetching but we can use a simplier variant for small
        if (largeTransforms != null) {
          guiQuads.add(List.of(MantleItemLayerModel.getQuadForGui(materialSprite.color(), -1, particle, smallTransforms, materialSprite.emissivity())));
          fullQuads.add(MaterialModel.getQuadsForMaterial(spriteGetter, owner.getMaterial(part.getName(isBroken, true)), material, -1, smallTransforms, pixels));
        } else {
          List<BakedQuad> quads = MantleItemLayerModel.getQuadsForSprite(materialSprite.color(), -1, particle, smallTransforms, 0, pixels);
          guiQuads.add(filterToGuiQuads(quads));
          fullQuads.add(quads);
        }
      } else {
        // part without materials
        particle = spriteGetter.apply(owner.getMaterial(part.getName(isBroken, false)));
        // same drill as above, large means more quad fetching but we can use a simplier variant for small
        if (largeTransforms != null) {
          guiQuads.add(List.of(MantleItemLayerModel.getQuadForGui(-1, -1, particle, smallTransforms, 0)));
          fullQuads.add(MantleItemLayerModel.getQuadsForSprite(-1, -1, spriteGetter.apply(owner.getMaterial(part.getName(isBroken, true))), largeTransforms, 0, pixels));
        } else {
          List<BakedQuad> quads = MantleItemLayerModel.getQuadsForSprite(-1, -1, particle, smallTransforms, 0, pixels);
          guiQuads.add(filterToGuiQuads(quads));
          fullQuads.add(quads);
        }
      }
    }
    // should never happen, but just in case prevents a NPE
    if (particle == null) {
      particle = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation()));
      TConstruct.LOG.error("Created tool model without a particle sprite, this means it somehow has no parts. This should not be possible");
    }

    // we need to build 2 models, one for the GUI and one for other perspectives. Then we combine the two into our final model
    IModelBuilder<?> guiModelBuilder = makeModelBuilder(owner, overrides, particle);
    guiQuads.build(quads -> quads.forEach(guiModelBuilder::addUnculledFace));
    IModelBuilder<?> fullModelBuilder = makeModelBuilder(owner, overrides, particle);
    fullQuads.build(quads -> quads.forEach(fullModelBuilder::addUnculledFace));
    return new BakedUniqueGuiModel(fullModelBuilder.build(), guiModelBuilder.build());
  }

  @Override
  public BakedModel bake(IGeometryBakingContext owner, ModelBakery bakery, Function<Material,TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
    Transformation largeTransforms = isLarge ? new Transformation(new Vector3f((offset.x - 8) / 32, (-offset.y - 8) / 32, 0), null, new Vector3f(2, 2, 1), null) : null;
    overrides = new MaterialOverrideHandler(owner, toolParts, firstModifiers, largeTransforms, modifierModels, overrides);
    // bake the original with no tool, meaning it will skip modifiers and materials
    return bakeInternal(owner, spriteGetter, largeTransforms, toolParts, modifierModels, firstModifiers, Collections.emptyList(), null, overrides);
  }

  /**
   * Data class for a single tool part
   */
  private record ToolPart(String name, int index, @Nullable String broken) {
    /** Default tool part instance for breakable textures */
    public static final ToolPart DEFAULT = new ToolPart("tool", -1, "broken");
    /** Default tool part list if one is not defined */
    public static final List<ToolPart> DEFAULT_PARTS = List.of(DEFAULT);
    /** Default parts list for when no broken texture is provided */
    public static final List<ToolPart> NO_BROKEN_PARTS = List.of(new ToolPart(DEFAULT.name, -1, null));

    /**
     * If true, this part has a broken texture
     */
    public boolean hasBroken() {
      return broken != null;
    }

    /**
     * If true, this part has material variants
     */
    public boolean hasMaterials() {
      return index >= 0;
    }

    /**
     * Gets the name for this part
     * @param isBroken If true, this part is broken
     * @param isLarge  If true, rendering a large tool
     * @return Texture name for this part
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

    /**
     * Reads a part from JSON
     */
    public static ToolPart read(JsonObject json) {
      String name = GsonHelper.getAsString(json, "name");
      int index = GsonHelper.getAsInt(json, "index", -1);
      String broken = null;
      if (json.has("broken")) {
        broken = GsonHelper.getAsString(json, "broken");
      }
      return new ToolPart(name, index, broken);
    }
  }

  /**
   * Dynamic override handler to swap in the material texture
   */
  public static final class MaterialOverrideHandler extends ItemOverrides {
    /** If true, we are currently resolving a nested model and should ignore further nesting */
    private static boolean ignoreNested = false;

    // contains all the baked models since they'll never change, cleared automatically as the baked model is discarded
    private final Cache<ToolCacheKey, BakedModel> cache = CacheBuilder
      .newBuilder()
      // ensure we can display every single tool that shows in JEI, plus a couple extra
      .maximumSize(MaterialRenderInfoLoader.INSTANCE.getAllRenderInfos().size() * 3L / 2)
      .build();

    // parameters needed for rebaking
    private final IGeometryBakingContext owner;
    private final List<ToolPart> toolParts;
    private final List<ModifierId> firstModifiers;
    @Nullable
    private final Transformation largeTransforms;
    private final Map<ModifierId,IBakedModifierModel> modifierModels;
    private final ItemOverrides nested;

    private MaterialOverrideHandler(IGeometryBakingContext owner, List<ToolPart> toolParts, List<ModifierId> firstModifiers, @Nullable Transformation largeTransforms, Map<ModifierId,IBakedModifierModel> modifierModels, ItemOverrides nested) {
      this.owner = owner;
      this.toolParts = toolParts;
      this.firstModifiers = firstModifiers;
      this.largeTransforms = largeTransforms;
      this.modifierModels = modifierModels;
      this.nested = nested;
    }

    /**
     * Gets the modifier model for this instance
     * @param modifier  Modifier
     * @return  Model for the modifier
     */
    @Nullable
    public IBakedModifierModel getModifierModel(Modifier modifier) {
      return modifierModels.get(modifier.getId());
    }

    /**
     * Bakes a copy of this model using the given material
     * @param materials  New materials for the model
     * @return  Baked model
     */
    private BakedModel bakeDynamic(List<MaterialVariantId> materials, IToolStackView tool) {
      // bake internal does not require an instance to bake, we can pass in whatever material we want
      // use empty override list as the sub model never calls overrides, and already has a material
      return bakeInternal(owner, Material::sprite, largeTransforms, toolParts, modifierModels, firstModifiers, materials, tool, ItemOverrides.EMPTY);
    }

    @Override
    public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
      // first, resolve the overrides
      // hack: we set a boolean flag to prevent that model from resolving its nested overrides, no nesting multiple deep
      if (!ignoreNested) {
        BakedModel overridden = nested.resolve(originalModel, stack, world, entity, seed);
        if (overridden != null && overridden != originalModel) {
          ignoreNested = true;
          // if the override does have a new model, make sure to fetch its overrides to handle the nested texture as its most likely a tool model
          BakedModel finalModel = overridden.getOverrides().resolve(overridden, stack, world, entity, seed);
          ignoreNested = false;
          return finalModel;
        }
      }
      // use material IDs for the sake of internal rendering materials
      List<MaterialVariantId> materialIds = MaterialIdNBT.from(stack).getMaterials();
      IToolStackView tool = ToolStack.from(stack);
      boolean broken = ToolDamageUtil.isBroken(stack);

      // if nothing unique, render original
      if (!broken && materialIds.isEmpty() && tool.getUpgrades().isEmpty()) {
        return originalModel;
      }

      // build the cache key for the modifiers, based on what the modifier requests
      // for many, it is just the modifier entry, but they can have more complex keys if needed
      ImmutableList.Builder<Object> builder = ImmutableList.builder();
      for (ModifierEntry entry : tool.getUpgrades().getModifiers()) {
        Set<ModifierId> hidden = ModifierSetWorktableRecipe.getModifierSet(tool.getPersistentData(), TConstruct.getResource("invisible_modifiers"));
        if (!hidden.contains(entry.getId())) {
          IBakedModifierModel model = getModifierModel(entry.getModifier());
          if (model != null) {
            Object cacheKey = model.getCacheKey(tool, entry);
            if (cacheKey != null) {
              builder.add(cacheKey);
            }
          }
        }
      }

      // render special model
      try {
        return cache.get(new ToolCacheKey(materialIds, builder.build(), broken), () -> bakeDynamic(materialIds, tool));
      } catch (ExecutionException e) {
        TConstruct.LOG.error("Failed to get tool model from cache", e);
        return originalModel;
      }
    }
  }

  /** Simple data class to cache built tool modifiers, contains everything unique in the textures */
  private record ToolCacheKey(List<MaterialVariantId> materials, List<Object> modifierData, boolean broken) {}
}
