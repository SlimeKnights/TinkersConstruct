package slimeknights.tconstruct.library.client.model.tools;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.QuadTransformers;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import slimeknights.mantle.client.model.util.ColoredBlockModel;
import slimeknights.mantle.client.model.util.MantleItemLayerModel;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.mapping.CompactLoadable;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.ReversedListBuilder;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo.TintedSprite;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.client.modifiers.IBakedModifierModel;
import slimeknights.tconstruct.library.client.modifiers.ModifierModelManager;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.worktable.ModifierSetWorktableRecipe;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Model handling all tools, both multipart and non.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ToolModel implements IUnbakedGeometry<ToolModel> {
  /** Shared loader instance */
  public static final IGeometryLoader<ToolModel> LOADER = ToolModel::deserialize;
  /** Set of transform types that make tools render small */
  private static final BitSet SMALL_TOOL_TYPES = new BitSet();

  /** Registers a new small tool transform type */
  public static synchronized void registerSmallTool(ItemDisplayContext type) {
    SMALL_TOOL_TYPES.set(type.ordinal());
  }

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
        List<ModifierEntry> modifiers = (overrides.showTraits ? tool.getModifiers() : tool.getUpgrades()).getModifiers();
        ModifierEntry[] firsts = new ModifierEntry[overrides.firstModifiers.size()];
        for (int i = modifiers.size() - 1; i >= 0; i--) {
          ModifierEntry entry = modifiers.get(i);
          ModifierId id = entry.getId();
          int firstIndex = FirstModifier.indexOf(overrides.firstModifiers, id);
          if (firstIndex != -1) {
            firsts[firstIndex] = entry;
          } else {
            // colors are assumed to not be sensitive to the model's large status
            IBakedModifierModel modifierModel = overrides.modifierModels.get(entry.getId());
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
        // first, add the first modifier tints
        for (int i = firsts.length - 1; i >= 0; i--) {
          ModifierEntry entry = firsts[i];
          FirstModifier first = overrides.firstModifiers.get(i);
          if (entry != null || first.forced) {
            IBakedModifierModel model = overrides.modifierModels.get(first.id);
            if (model != null) {
              int modelIndexes = model.getTintIndexes();
              if (localIndex + modelIndexes > index) {
                if (entry == null) {
                  entry = new ModifierEntry(first.id, 0);
                }
                return model.getTint(tool, entry, index - localIndex);
              }
              localIndex += modelIndexes;
            }
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

  /** Gets an offset from JSON, or the default value if absent */
  private static Vec2 getOffset(JsonObject parent, String key) {
    if (parent.has(key)) {
      return MaterialModel.getVec2(parent, key);
    }
    return Vec2.ZERO;
  }

  /** Deserializes the model from JSON */
  public static ToolModel deserialize(JsonObject json, JsonDeserializationContext context) {
    List<ToolPart> parts = Collections.emptyList();
    if (json.has("parts")) {
      parts = JsonHelper.parseList(json, "parts", ToolPart::read);
    }
    boolean isLarge = GsonHelper.getAsBoolean(json, "large", false);
    boolean showTraits = GsonHelper.getAsBoolean(json, "show_traits", false);
    Vec2 offset = getOffset(json, "large_offset");
    // modifier root fetching
    List<ResourceLocation> smallModifierRoots = Collections.emptyList();
    List<ResourceLocation> largeModifierRoots = Collections.emptyList();
    if (json.has("modifier_roots")) {
      // large model requires an object
      if (isLarge) {
        JsonObject modifierRoots = GsonHelper.getAsJsonObject(json, "modifier_roots");
        smallModifierRoots = JsonHelper.parseList(modifierRoots, "small", Loadables.RESOURCE_LOCATION);
        largeModifierRoots = JsonHelper.parseList(modifierRoots, "large", Loadables.RESOURCE_LOCATION);
      } else {
        // small requires an array
        smallModifierRoots = JsonHelper.parseList(json, "modifier_roots", Loadables.RESOURCE_LOCATION);
      }
    }
    // fetch data related to ammo display
    ResourceLocation ammoKey = null;
    Vec2 smallAmmoOffset = Vec2.ZERO;
    Vec2 largeAmmoOffset = Vec2.ZERO;
    boolean flipAmmo = false;
    boolean leftAmmo = false;
    if (json.has("ammo")) {
      JsonObject ammo = GsonHelper.getAsJsonObject(json, "ammo");
      ammoKey = JsonHelper.getResourceLocation(ammo, "key");
      flipAmmo = GsonHelper.getAsBoolean(ammo, "flip");
      leftAmmo = GsonHelper.getAsBoolean(ammo, "left");
      // large has an offset for both models
      if (isLarge) {
        if (!ammo.has("small_offset") && !ammo.has("large_offset")) {
          throw new JsonSyntaxException("Ammo must either have a small or large offset provided");
        }
        smallAmmoOffset = getOffset(ammo, "small_offset");
        largeAmmoOffset = getOffset(ammo, "large_offset");
      } else {
        // no large is just a single offset, and is required
        smallAmmoOffset = MaterialModel.getVec2(ammo, "offset");
      }
    }

    // modifiers first
    List<FirstModifier> firstModifiers = FirstModifier.LOADABLE.getOrDefault(json, "first_modifiers", List.of());
    return new ToolModel(parts, isLarge, offset, smallModifierRoots, largeModifierRoots, firstModifiers, ammoKey, flipAmmo, leftAmmo, smallAmmoOffset, largeAmmoOffset, showTraits);
  }

  /** List of tool parts in this model */
  private final List<ToolPart> toolParts;
  /** If true, this is a large tool and uses double resolution textures in hand */
  private final boolean isLarge;
  /** Transform matrix to apply to child parts */
  private final Vec2 offset;
  /** Location to fetch modifier textures for small variant */
  private final List<ResourceLocation> smallModifierRoots;
  /** Location to fetch modifier textures for large variant */
  private final List<ResourceLocation> largeModifierRoots;
  /** Modifiers that show first on tools, bypassing normal sort order */
  private final List<FirstModifier> firstModifiers;
  /** Location in tool NBT to find the ammo */
  @Nullable
  private final ResourceLocation ammoKey;
  /** If true, flips the ammo horizontally, as most bows have opposite orientation from ammo */
  private final boolean flipAmmo;
  /** If true, left handed models should have the ammo shifted to the left */
  private final boolean leftAmmo;
  /** Offset to apply to ammo quads for the small model */
  private final Vec2 smallAmmoOffset;
  /** Offset to apply to ammo quads for the large model */
  private final Vec2 largeAmmoOffset;
  /** If true, traits are displayed on the tool model. If false, just modifiers. */
  private final boolean showTraits;

  /**
   * adds quads for relevant modifiers
   * @param spriteGetter    Sprite getter instance
   * @param modifierModels  Map of modifier models
   * @param tool            Tool instance
   * @param quadConsumer    Consumer for finished quads
   * @param transforms      Transforms to apply
   * @param isLarge         If true, the quads are for a large tool
   */
  private static void addModifierQuads(Function<Material, TextureAtlasSprite> spriteGetter, Map<ModifierId,IBakedModifierModel> modifierModels, List<FirstModifier> firstModifiers, boolean showTraits, IToolStackView tool, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels, Transformation transforms, boolean isLarge) {
    if (!modifierModels.isEmpty()) {
      // keep a running tint index so models know where they should start, currently starts at 0 as the main model does not use tint indexes
      int modelIndex = 0;
      // reversed order to ensure the pixels is updated correctly
      List<ModifierEntry> modifiers = (showTraits ? tool.getModifiers() : tool.getUpgrades()).getModifiers();
      // keep track of the entry for each first modifier, as that may impact how it renders
      ModifierEntry[] firsts = new ModifierEntry[firstModifiers.size()];
      if (!modifiers.isEmpty()) {
        // last, add all regular modifiers
        Set<ModifierId> hidden = ModifierSetWorktableRecipe.getModifierSet(tool.getPersistentData(), TConstruct.getResource("invisible_modifiers"));
        for (int i = modifiers.size() - 1; i >= 0; i--) {
          ModifierEntry entry = modifiers.get(i);
          ModifierId modifier = entry.getModifier().getId();
          int index = FirstModifier.indexOf(firstModifiers, modifier);
          if (index != -1) {
            // handle first modifiers later
            firsts[index] = entry;
          } else if (!hidden.contains(modifier)) {
            IBakedModifierModel model = modifierModels.get(modifier);
            if (model != null) {
              // if the modifier is in the list, delay adding its quads, but keep the expected tint index
              model.addQuads(tool, entry, spriteGetter, transforms, isLarge, modelIndex, quadConsumer, pixels);
              modelIndex += model.getTintIndexes();
            }
          }
        }
      }
      // first, add the first modifiers
      for (int i = firsts.length - 1; i >= 0; i--) {
        ModifierEntry entry = firsts[i];
        FirstModifier first = firstModifiers.get(i);
        if (entry != null || first.forced) {
          IBakedModifierModel model = modifierModels.get(first.id);
          if (model != null) {
            if (entry == null) {
              entry = new ModifierEntry(first.id, 0);
            }
            model.addQuads(tool, entry, spriteGetter, transforms, isLarge, modelIndex, quadConsumer, pixels);
            modelIndex += model.getTintIndexes();
          }
        }
      }
    }
  }

  /** Modifier that may be forced */
  private record FirstModifier(ModifierId id, boolean forced) {
    private static final Loadable<List<FirstModifier>> LOADABLE = CompactLoadable.of(
      RecordLoadable.create(ModifierId.PARSER.requiredField("name", FirstModifier::id), BooleanLoadable.INSTANCE.defaultField("forced", false, false, FirstModifier::forced), FirstModifier::new),
      ModifierId.PARSER.flatXmap(id -> new FirstModifier(id, false), FirstModifier::id),
      f -> !f.forced).list(0);

    /** Gets the index of a modifier in the list */
    public static int indexOf(List<FirstModifier> list, ModifierId id) {
      for (int i = 0; i < list.size(); i++) {
        if (list.get(i).id.equals(id)) {
          return i;
        }
      }
      return -1;
    }
  }

  /** Makes a model builder for the given context and overrides */
  private static IModelBuilder<?> makeModelBuilder(IGeometryBakingContext context, ItemOverrides overrides, TextureAtlasSprite particle) {
    return IModelBuilder.of(context.useAmbientOcclusion(), false, false, context.getTransforms(), overrides, particle, MantleItemLayerModel.getDefaultRenderType(context));
  }

  /**
   * Same as {@link #bake(IGeometryBakingContext, ModelBaker, Function, ModelState, ItemOverrides, ResourceLocation)}, but uses fewer arguments and does not require an instance
   * @param owner           Model configuration
   * @param spriteGetter    Sprite getter function
   * @param largeTransforms Transform to apply to the large parts. If null, only generates small parts
   * @param parts           List of tool parts in this tool
   * @param modifierModels  Map of modifier models for this tool
   * @param firstModifiers  List of modifiers to show first on the tool
   * @param showTraits      If true, shows the traits on the tool. False shows just crafted modifiers.
   * @param materials       Materials to use for the parts
   * @param tool            Tool instance for modifier parsing
   * @param overrides       Override instance to use, will either be empty or {@link MaterialOverrideHandler}
   * @param smallExtraQuads Additional quads to add to the small model. Should already be transformed as desired.
   * @param largeExtraQuads Additional quads to add to the large model. Should already be transformed as desired.
   * @param leftExtraQuads  Additional quads to add to the left-handed model. Will be a large model if {@code largeTransforms} is non-null, otherwise small.
   * @return  Baked model
   */
  private static BakedModel bakeInternal(IGeometryBakingContext owner, Function<Material, TextureAtlasSprite> spriteGetter, @Nullable Transformation largeTransforms,
                                         List<ToolPart> parts, Map<ModifierId,IBakedModifierModel> modifierModels, List<FirstModifier> firstModifiers, boolean showTraits,
                                         List<MaterialVariantId> materials, @Nullable IToolStackView tool, ItemOverrides overrides,
                                         Collection<BakedQuad> smallExtraQuads, Collection<BakedQuad> largeExtraQuads, Collection<BakedQuad> leftExtraQuads) {
    Transformation smallTransforms = Transformation.identity();

    // TODO: would be nice to support render types per material/per modifier
    // small model is used in GUIs (though that will be filtered to just front faces) and in small tool contexts like casting blocks
    ReversedListBuilder<Collection<BakedQuad>> smallQuads = new ReversedListBuilder<>();
    ItemLayerPixels smallPixels = new ItemLayerPixels();
    // large model is used in all other places
    ReversedListBuilder<Collection<BakedQuad>> largeQuads = largeTransforms != null ? new ReversedListBuilder<>() : smallQuads;
    ItemLayerPixels largePixels = largeTransforms != null ? new ItemLayerPixels() : smallPixels;

    // add quads for all modifiers first, for the sake of the item layer pixels
    if (tool != null && !modifierModels.isEmpty()) {
      addModifierQuads(spriteGetter, modifierModels, firstModifiers, showTraits, tool, smallQuads::add, smallPixels, smallTransforms, false);
      // if we have a large model, that means we will fetch models twice, once for large then again for small
      if (largeTransforms != null) {
        addModifierQuads(spriteGetter, modifierModels, firstModifiers, showTraits, tool, largeQuads::add, largePixels, largeTransforms, true);
      }
    }

    // add quads for all parts
    TextureAtlasSprite particle = null;
    for (int i = parts.size() - 1; i >= 0; i--) {
      ToolPart part = parts.get(i);

      // part with materials
      if (part.hasMaterials()) {
        // start by fetching the material we are rendering at this position, should only be null on invalid tools or during the initial bake
        int index = part.index();
        MaterialVariantId material = index < materials.size() ? materials.get(index) : IMaterial.UNKNOWN_ID;
        TintedSprite materialSprite = MaterialModel.getMaterialSprite(spriteGetter, owner.getMaterial(part.getName(false)), material);
        particle = materialSprite.sprite();

        // need full quads for both as small is directly rendered in a few non-GUI cases
        smallQuads.add(MantleItemLayerModel.getQuadsForSprite(materialSprite.color(), -1, materialSprite.sprite(), smallTransforms, materialSprite.emissivity(), smallPixels));
        if (largeTransforms != null) {
          largeQuads.add(MaterialModel.getQuadsForMaterial(spriteGetter, owner.getMaterial(part.getName(true)), material, -1, largeTransforms, largePixels));
        }
      } else {
        // part without materials
        particle = spriteGetter.apply(owner.getMaterial(part.getName(false)));
        // same drill as above
        smallQuads.add(MantleItemLayerModel.getQuadsForSprite(-1, -1, particle, smallTransforms, 0, smallPixels));
        if (largeTransforms != null) {
          largeQuads.add(MantleItemLayerModel.getQuadsForSprite(-1, -1, spriteGetter.apply(owner.getMaterial(part.getName(true))), largeTransforms, 0, largePixels));
        }
      }
    }
    // should never happen, but just in case prevents a NPE
    if (particle == null) {
      particle = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation()));
      TConstruct.LOG.error("Created tool model without a particle sprite, this means it somehow has no parts. This should not be possible");
    }

    // time to bake the model. Opted for a small amount of redundant code to minimize operations as this is run dynamically
    IModelBuilder<?> smallBuilder = makeModelBuilder(owner, overrides, particle);
    IModelBuilder<?> guiBuilder = makeModelBuilder(owner, overrides, particle);
    IModelBuilder<?> leftBuilder = !leftExtraQuads.isEmpty() ? makeModelBuilder(owner, overrides, particle) : null;

    // if we lack a large model, feed left builder with small quads
    if (largeTransforms == null && leftBuilder != null) {
      smallQuads.build(quads -> quads.forEach(quad -> {
        smallBuilder.addUnculledFace(quad);
        leftBuilder.addUnculledFace(quad);
        if (quad.getDirection() == Direction.SOUTH) {
          guiBuilder.addUnculledFace(quad);
        }
      }));
    } else {
      smallQuads.build(quads -> quads.forEach(quad -> {
        smallBuilder.addUnculledFace(quad);
        if (quad.getDirection() == Direction.SOUTH) {
          guiBuilder.addUnculledFace(quad);
        }
      }));
    }

    // smallExtraQuads are never added to the leftBuilder
    for (BakedQuad quad : smallExtraQuads) {
      smallBuilder.addUnculledFace(quad);
      if (quad.getDirection() == Direction.SOUTH) {
        guiBuilder.addUnculledFace(quad);
      }
    }
    BakedModel small = smallBuilder.build();
    BakedModel gui = guiBuilder.build();

    // if no large, use small for right
    BakedModel right;
    if (largeTransforms != null) {
      IModelBuilder<?> largeBuilder = makeModelBuilder(owner, overrides, particle);
      // if left, fill with large quads
      if (leftBuilder != null) {
        largeQuads.build(quads -> quads.forEach(quad -> {
          largeBuilder.addUnculledFace(quad);
          leftBuilder.addUnculledFace(quad);
        }));
      } else {
        largeQuads.build(quads -> quads.forEach(largeBuilder::addUnculledFace));
      }
      // left has not received any extra quads
      for (BakedQuad quad : largeExtraQuads) {
        largeBuilder.addUnculledFace(quad);
      }
      right = largeBuilder.build();
    } else {
      right = small;
    }

    // if no left, use right for left
    BakedModel left;
    if (leftBuilder != null) {
      for (BakedQuad quad : leftExtraQuads) {
        leftBuilder.addUnculledFace(quad);
      }
      left = leftBuilder.build();
    } else {
      left = right;
    }

    // finish baking model
    return new BakedToolModel(right, left, small, gui);
  }

  @Override
  public BakedModel bake(IGeometryBakingContext owner, ModelBaker baker, Function<Material,TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
    // default is just a single part named tool, no material
    List<ToolPart> toolParts = this.toolParts;
    if (toolParts.isEmpty()) {
      toolParts = ToolPart.DEFAULT_PARTS;
    }
    // if requested, check all that all material textures exist
    if (Config.CLIENT.logMissingMaterialTextures.get()) {
      for (ToolPart part : toolParts) {
        if (part.hasMaterials()) {
          MaterialModel.validateMaterialTextures(owner, spriteGetter, part.getName(false), null);
          if (isLarge) {
            MaterialModel.validateMaterialTextures(owner, spriteGetter, part.getName(true), null);
          }
        }
      }
    }
    // load modifier models
    Map<ModifierId,IBakedModifierModel> modifierModels = ModifierModelManager.getModelsForTool(spriteGetter, smallModifierRoots, isLarge ? largeModifierRoots : Collections.emptyList());

    // build transforms for various states
    // large tools are stretched in X and Y by 200%, and get a special offset
    Transformation largeTransforms = isLarge ? new Transformation(new Vector3f((offset.x - 8) / 32, (-offset.y - 8) / 32, 0), null, new Vector3f(2, 2, 1), null) : null;
    // arrow quads are flipped, and also shifted
    Transformation smallAmmoTransforms = null;
    Transformation largeAmmoTransforms = null;
    Transformation leftAmmoTransforms = null;
    if (ammoKey != null) {
      // flipping rotates it 180 degrees, but because the origin is 0,0 instead of 0.5,0,5 it gets shifted
      // I could do some composition to shift the orgin, but its faster to just correct for it below
      Quaternionf ammoRotation = flipAmmo ? Axis.YP.rotationDegrees(-180) : null;
      float flipOffset = flipAmmo ? 1 : 0;

      // left if requested is based on either small or right, reusing a variable allows us to keep the one that ended up used.
      Vector3f translation = new Vector3f(
        smallAmmoOffset.x / 16 + flipOffset,
        -smallAmmoOffset.y / 16,
        1f / 16 + flipOffset
      );
      smallAmmoTransforms = new Transformation(translation, ammoRotation, null, null);
      if (isLarge) {
        translation = new Vector3f(
          (offset.x/2 + largeAmmoOffset.x + 4f) / 16 + flipOffset,
          (-offset.y/2 - largeAmmoOffset.y + 4f) / 16,
          1f / 16 + flipOffset
        );
        largeAmmoTransforms = new Transformation(translation, ammoRotation, null, null);
      }
      // if we want left ammo, it copies whichever is the most recent from the two offsets
      if (leftAmmo) {
        translation = new Vector3f(translation);
        translation.z = -1f / 16 + flipOffset;
        leftAmmoTransforms = new Transformation(translation, ammoRotation, null, null);
      }
    }
    overrides = new MaterialOverrideHandler(owner, toolParts, firstModifiers, showTraits, largeTransforms, modifierModels, overrides, ammoKey, flipAmmo, smallAmmoTransforms, largeAmmoTransforms, leftAmmoTransforms);
    // bake the original with no tool, meaning it will skip modifiers and materials
    return bakeInternal(owner, spriteGetter, largeTransforms, toolParts, modifierModels, firstModifiers, showTraits, List.of(), null, overrides, List.of(), List.of(), List.of());
  }

  /** Swaps out the large model for the small or gui model as needed */
  private static class BakedToolModel extends BakedModelWrapper<BakedModel> {
    /** Model to use for left-handed display. May be same as right-handed; ammo makes different */
    private final BakedModel left;
    /** Model to use for contexts requesting small models, such as smeltery blocks. Same as right if the tool has no large. */
    private final BakedModel small;
    /** Model to use in the GUI */
    private final BakedModel gui;
    public BakedToolModel(BakedModel right, BakedModel left, BakedModel small, BakedModel gui) {
      super(right);
      this.left = left;
      this.small = small;
      this.gui = gui;
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext cameraTransformType, PoseStack mat, boolean applyLeftHandTransform) {
      BakedModel model = originalModel;
      if (cameraTransformType == ItemDisplayContext.GUI) {
        model = gui;
      } else if (cameraTransformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || cameraTransformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
        model = left;
      } else if (originalModel != small && SMALL_TOOL_TYPES.get(cameraTransformType.ordinal())) {
        model = small;
      }
      return model.applyTransform(cameraTransformType, mat, applyLeftHandTransform);
    }
  }

  /**
   * Data class for a single tool part
   */
  private record ToolPart(String name, int index) {
    /** Default tool part instance for breakable textures */
    public static final ToolPart DEFAULT = new ToolPart("tool", -1);
    /** Default tool part list if one is not defined */
    public static final List<ToolPart> DEFAULT_PARTS = List.of(DEFAULT);

    /**
     * If true, this part has material variants
     */
    public boolean hasMaterials() {
      return index >= 0;
    }

    /**
     * Gets the name for this part
     * @param isLarge  If true, rendering a large tool
     * @return Texture name for this part
     */
    public String getName(boolean isLarge) {
      if (isLarge) {
        return "large_" + name;
      }
      return name;
    }

    /**
     * Reads a part from JSON
     */
    public static ToolPart read(JsonObject json) {
      String name = GsonHelper.getAsString(json, "name");
      int index = GsonHelper.getAsInt(json, "index", -1);
      return new ToolPart(name, index);
    }
  }

  /**
   * Dynamic override handler to swap in the material texture
   */
  public static final class MaterialOverrideHandler extends NestedOverrides {

    // contains all the baked models since they'll never change, cleared automatically as the baked model is discarded
    private final Cache<ToolCacheKey, BakedModel> cache = CacheBuilder
      .newBuilder()
      // ensure we can display every single tool that shows in JEI, plus a couple extra
      .maximumSize(MaterialRenderInfoLoader.INSTANCE.getAllRenderInfos().size() * 3L / 2)
      .build();

    // parameters needed for rebaking
    private final IGeometryBakingContext owner;
    private final List<ToolPart> toolParts;
    private final List<FirstModifier> firstModifiers;
    private final boolean showTraits;
    @Nullable
    private final Transformation largeTransforms;
    private final Map<ModifierId,IBakedModifierModel> modifierModels;
    @Nullable
    private final ResourceLocation ammoKey;
    private final boolean flipAmmo;
    @Nullable
    private final Transformation smallAmmoTransforms;
    @Nullable
    private final Transformation largeAmmoTransforms;
    @Nullable
    private final Transformation leftAmmoTransforms;

    private MaterialOverrideHandler(IGeometryBakingContext owner, List<ToolPart> toolParts, List<FirstModifier> firstModifiers, boolean showTraits, @Nullable Transformation largeTransforms, Map<ModifierId, IBakedModifierModel> modifierModels, ItemOverrides nested, @Nullable ResourceLocation ammoKey, boolean flipAmmo, @Nullable Transformation smallAmmoTransforms, @Nullable Transformation largeAmmoTransforms, @Nullable Transformation leftAmmoTransforms) {
      super(nested);
      this.owner = owner;
      this.toolParts = toolParts;
      this.firstModifiers = firstModifiers;
      this.showTraits = showTraits;
      this.largeTransforms = largeTransforms;
      this.modifierModels = modifierModels;
      this.ammoKey = ammoKey;
      this.flipAmmo = flipAmmo;
      this.smallAmmoTransforms = smallAmmoTransforms;
      this.largeAmmoTransforms = largeAmmoTransforms;
      this.leftAmmoTransforms = leftAmmoTransforms;
    }

    /**
     * Bakes a copy of this model using the given material
     * @param materials  New materials for the model
     * @return  Baked model
     */
    private BakedModel bakeDynamic(List<MaterialVariantId> materials, IToolStackView tool, ItemStack ammo, int seed) {
      // start by finding ammo quads
      List<BakedQuad> smallAmmoQuads = List.of();
      List<BakedQuad> largeAmmoQuads = List.of();
      List<BakedQuad> leftAmmoQuads = List.of();
      if (!ammo.isEmpty()) {
        // find ammo model
        BakedModel ammoModel = Minecraft.getInstance().getItemRenderer().getModel(ammo, null, null, seed);
        if (ammoModel != Minecraft.getInstance().getModelManager().getMissingModel()) {
          // resolve ammo model (for materials and such)
          ammoModel = ammoModel.getOverrides().resolve(ammoModel, ammo, null, null, seed);
          if (ammoModel != null) {
            // get all the quads for the ammo model
            List<BakedQuad> ammoQuads = new ArrayList<>();
            RandomSource rand = RandomSource.create();
            for (Direction direction : Direction.values()) {
              ammoQuads.addAll(ammoModel.getQuads(null, direction, rand, ModelData.EMPTY, null));
            }
            ammoQuads.addAll(ammoModel.getQuads(null, null, rand, ModelData.EMPTY, null));

            // bake tints into static colors; saves us having to redirect item colors which is slow
            Int2IntMap tints = new Int2IntArrayMap();
            ItemColors colors = Minecraft.getInstance().getItemColors();
            Int2IntFunction colorGetter = tint -> ColoredBlockModel.swapColorRedBlue(colors.getColor(ammo, tint));
            ammoQuads = ammoQuads.stream().map(quad -> {
              if (quad.isTinted() || (flipAmmo && quad.getDirection().getAxis() != Direction.Axis.Y)) {
                int[] vertices = quad.getVertices();
                if (quad.isTinted()) {
                  int abgr = 0xFF000000 | tints.computeIfAbsent(quad.getTintIndex(), colorGetter);
                  vertices = Arrays.copyOf(vertices, vertices.length);
                  for (int i = 0; i < 4; i++) {
                    vertices[i * IQuadTransformer.STRIDE + IQuadTransformer.COLOR] = abgr;
                  }
                }
                Direction direction = quad.getDirection();
                if (flipAmmo && direction.getAxis() != Direction.Axis.Y) {
                  direction = direction.getOpposite();
                }
                return new BakedQuad(vertices, -1, direction, quad.getSprite(), quad.isShade(), quad.hasAmbientOcclusion());
              }
              return quad;
            }).toList();
            // got our quads, now we need to offset them for the model
            if (smallAmmoTransforms != null) {
              smallAmmoQuads = QuadTransformers.applying(smallAmmoTransforms).process(ammoQuads);
            }
            if (largeAmmoTransforms != null) {
              largeAmmoQuads = QuadTransformers.applying(largeAmmoTransforms).process(ammoQuads);
            }
            if (leftAmmoTransforms != null) {
              leftAmmoQuads = QuadTransformers.applying(leftAmmoTransforms).process(ammoQuads);
            }
          }
        }
      }

      // bake internal does not require an instance to bake, we can pass in whatever material we want
      // use empty override list as the sub model never calls overrides, and already has a material
      return bakeInternal(owner, Material::sprite, largeTransforms, toolParts, modifierModels, firstModifiers, showTraits, materials, tool, ItemOverrides.EMPTY, smallAmmoQuads, largeAmmoQuads, leftAmmoQuads);
    }

    @Nullable
    @Override
    public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
      BakedModel resolved = super.resolve(originalModel, stack, world, entity, seed);
      if (resolved != originalModel) {
        return resolved;
      }
      
      // use material IDs for the sake of internal rendering materials
      List<MaterialVariantId> materialIds = MaterialIdNBT.from(stack).getMaterials();
      IToolStackView tool = ToolStack.from(stack);

      // if nothing unique, render original
      ModifierNBT modifiers = showTraits ? tool.getModifiers() : tool.getUpgrades();
      skip:
      if (materialIds.isEmpty() && modifiers.isEmpty()) {
        for (FirstModifier modifier : firstModifiers) {
          if (modifier.forced) {
            break skip;
          }
        }
        return originalModel;
      }

      // build the cache key for the modifiers, based on what the modifier requests
      // for many, it is just the modifier entry, but they can have more complex keys if needed
      ImmutableList.Builder<Object> builder = ImmutableList.builder();
      Set<ModifierId> hidden = ModifierSetWorktableRecipe.getModifierSet(tool.getPersistentData(), TConstruct.getResource("invisible_modifiers"));
      ModifierEntry[] firstEntries = new ModifierEntry[firstModifiers.size()];
      for (ModifierEntry entry : modifiers) {
        ModifierId id = entry.getId();
        int index = FirstModifier.indexOf(firstModifiers, id);
        if (index != -1) {
          // handle all the first entries together, keeps their order consistent
          firstEntries[index] = entry;
        } else if (!hidden.contains(id)) {
          IBakedModifierModel model = modifierModels.get(id);
          if (model != null) {
            Object cacheKey = model.getCacheKey(tool, entry);
            if (cacheKey != null) {
              builder.add(cacheKey);
            }
          }
        }
      }
      for (int i = 0; i < firstModifiers.size(); i++) {
        FirstModifier modifier = firstModifiers.get(i);
        ModifierEntry entry = firstEntries[i];
        if (entry != null || modifier.forced) {
          IBakedModifierModel model = modifierModels.get(modifier.id);
          if (model != null) {
            if (entry == null) {
              entry = new ModifierEntry(modifier.id, 0);
            }
            Object cacheKey = model.getCacheKey(tool, entry);
            if (cacheKey != null) {
              builder.add(cacheKey);
            }
          }
        }
      }
      // fetch ammo info from the stack
      ItemStack ammo;
      ModDataNBT persistentData = tool.getPersistentData();
      if (ammoKey != null && persistentData.contains(ammoKey, Tag.TAG_COMPOUND)) {
        ammo = ItemStack.of(persistentData.getCompound(ammoKey));
        builder.add(ammo.getItem());
        CompoundTag tag = ammo.getTag();
        if (tag != null) {
          builder.add(tag);
        }
      } else {
        ammo = ItemStack.EMPTY;
      }

      // render special model
      try {
        return cache.get(new ToolCacheKey(materialIds, builder.build()), () -> bakeDynamic(materialIds, tool, ammo, seed));
      } catch (ExecutionException e) {
        TConstruct.LOG.error("Failed to get tool model from cache", e);
        return originalModel;
      }
    }
  }

  /** Simple data class to cache built tool modifiers, contains everything unique in the textures */
  private record ToolCacheKey(List<MaterialVariantId> materials, List<Object> modifierData) {}
}
