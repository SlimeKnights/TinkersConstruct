package slimeknights.tconstruct.library.client.model.tools;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.math.Transformation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.model.RetexturedModel;
import slimeknights.mantle.client.model.RetexturedModel.RetexturedContext;
import slimeknights.mantle.client.model.util.ColoredBlockModel;
import slimeknights.mantle.client.model.util.DynamicBakedWrapper;
import slimeknights.mantle.client.model.util.ExtraTextureContext;
import slimeknights.mantle.client.model.util.ModelHelper;
import slimeknights.mantle.client.model.util.SimpleBlockModel;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.array.ArrayLoadable;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.mantle.util.RetexturedHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo.TintedSprite;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.client.model.ModelProperties;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * Model that handles dynamic materials using the block model elements style.
 * When used for tools, notably does not handle modifier models, just materials are considered.
 * @see MaterialModel
 * @see ToolModel
 */
@RequiredArgsConstructor
public class MaterialBlockModel implements IUnbakedGeometry<MaterialBlockModel> {
  /** Location for dynamic baking */
  private static final ResourceLocation BAKE_LOCATION = Mantle.getResource("material_block_dynamic");
  /** Loadable for the list of material textures */
  private static final Loadable<Set<String>> MATERIAL = StringLoadable.DEFAULT.set(ArrayLoadable.COMPACT);
  /** Loadable for the list of parts, each a list of material textures */
  private static final Loadable<List<Set<String>>> PARTS = StringLoadable.DEFAULT.set(ArrayLoadable.COMPACT_OR_EMPTY).list(1);
  /** Shared loader instance */
  public static final IGeometryLoader<MaterialBlockModel> LOADER = MaterialBlockModel::deserialize;

  public enum ModelType { TOOL, PART, ANVIL }

  /** Block model to retexture. Expected each element is a single material or not a material, no multiple or mixed. */
  private final SimpleBlockModel model;
  /** List of parts, each is a set of materials to retexture */
  private final List<Set<String>> parts;
  /** Type of model to create. */
  private final ModelType type;

  /** Loads a material block model from JSON */
  public static MaterialBlockModel deserialize(JsonObject json, JsonDeserializationContext context) {
    SimpleBlockModel model = SimpleBlockModel.deserialize(json, context);

    // if retextured is set, using the anvil style model
    if (json.has("retextured")) {
      return new MaterialBlockModel(model, List.of(MATERIAL.getIfPresent(json, "retextured")), ModelType.ANVIL);
    }
    // if material is set, using the parts model
    if (json.has("material")) {
      return new MaterialBlockModel(model, List.of(MATERIAL.getIfPresent(json, "material")), ModelType.PART);
    }
    // otherwise, using the tool model
    return new MaterialBlockModel(model, PARTS.getIfPresent(json, "parts"), ModelType.TOOL);
  }

  @Override
  public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
    model.resolveParents(modelGetter, context);
  }

  @Override
  public BakedModel bake(IGeometryBakingContext owner, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation location) {
    BakedModel baked = model.bake(owner, baker, spriteGetter, transform, overrides, location);
    List<Set<String>> parts = this.parts.stream().map(part -> RetexturedModel.getAllRetextured(owner, model, part)).toList();

    // part model - fetches material from NBT field
    if (type == ModelType.PART) {
      return new BakedPart(baked, owner, model, transform, parts.get(0));
    }
    // anvil model - fetches material from NBT field, but can also choose block texture
    if (type == ModelType.ANVIL) {
      return new BakedAnvil(baked, owner, model, transform, parts.get(0));
    }
    // for tools with just one material, use simpler tool model
    if (parts.size() == 1) {
      return new SimpleBakedTool(baked, owner, model, transform, parts.get(0));
    }
    // standard tool with multiple materials
    boolean particleRetextured = parts.stream().anyMatch(set -> set.contains("particle"));
    return new BakedTool(baked, owner, model, transform, parts, particleRetextured);
  }

  /** Common logic between all variants of baking */
  private static abstract class AbstractBaked<T,P> extends DynamicBakedWrapper<BakedModel> {
    protected final IGeometryBakingContext owner;
    protected final SimpleBlockModel model;
    protected final ModelState transform;
    protected final boolean particleRetextured;
    protected final ModelProperty<P> property;

    private AbstractBaked(BakedModel original, IGeometryBakingContext owner, SimpleBlockModel model, ModelState transform, boolean particleRetextured, ModelProperty<P> property) {
      super(original);
      this.owner = owner;
      this.model = model;
      this.transform = transform;
      this.particleRetextured = particleRetextured;
      this.property = property;
    }

    /** Fetches textures from the materials for baking */
    protected abstract void fetchMaterials(T materials, Function<Material, TextureAtlasSprite> spriteGetter, Map<String, Material> replacements, Map<String, TintedSprite> tints);

    /** Fetches a single material. For use in {@link #fetchMaterials(Object, Function, Map, Map)} */
    protected void fetchMaterial(MaterialVariantId material, Set<String> retextured, Function<Material, TextureAtlasSprite> spriteGetter, Map<String, Material> replacements, Map<String, TintedSprite> tints) {
      // fetch render info
      Optional<MaterialRenderInfo> optional = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(material);
      if (optional.isPresent()) {
        MaterialRenderInfo info = optional.get();

        // may have multiple names going to the same texture
        Map<Material, TintedSprite> seen = new HashMap<>();
        for (String name : retextured) {
          Material texture = owner.getMaterial(name);
          TintedSprite tinted = seen.get(texture);
          if (tinted == null) {
            tinted = info.getSprite(texture, spriteGetter);
            seen.put(texture, tinted);
          }
          TextureAtlasSprite sprite = tinted.sprite();
          // bit annoying that we have to unwrap the sprite, but its needed to be fetched to check if its present
          replacements.put(name, new Material(sprite.atlasLocation(), sprite.contents().name()));
          // if we have a transform, save that with the name too
          if (tinted.color() != -1 || tinted.emissivity() > 0) {
            tints.put('#' + name, tinted);
          }
        }
      }
    }

    /** Retextures with the given materials list */
    protected BakedModel bakeWith(T materials) {
      // map of name to replacement texture
      Map<String,Material> replacements = new HashMap<>();
      // map of name to transformation to apply, will likely be smaller than replacement textures
      Map<String,TintedSprite> tints = new HashMap<>();

      // delegate material fetching
      Function<Material,TextureAtlasSprite> spriteGetter = Material::sprite;
      fetchMaterials(materials, spriteGetter, replacements, tints);

      // quick exit in case we found nothing, should never happen
      if (replacements.isEmpty()) {
        return originalModel;
      }

      // create context to swap the textures
      IGeometryBakingContext retextureContext = new ExtraTextureContext(owner, replacements);

      // if no parts need colors, we can just use standard baking
      if (tints.isEmpty()) {
        return model.bakeDynamic(retextureContext, transform);
      }

      // need to tint or apply light to some textures
      TextureAtlasSprite particle = spriteGetter.apply(owner.getMaterial("particle"));
      SimpleBakedModel.Builder builder = SimpleBlockModel.bakedBuilder(owner, originalModel.getOverrides()).particle(particle);
      List<BlockElement> elements = model.getElements();
      int size = elements.size();
      IQuadTransformer quadTransformer = SimpleBlockModel.applyTransform(transform, owner.getRootTransform());
      Transformation transformation = transform.getRotation();
      boolean uvlock = transform.isUvLocked();
      for (int i = 0; i < size; i++) {
        BlockElement part = elements.get(i);
        // determine if any of the faces needs a tint
        // for simplicity, assume the whole part is tinted if so. Build your model to separate distinct material faces if needed
        TintedSprite tint = null;
        for (BlockElementFace face : part.faces.values()) {
          TintedSprite faceTint = tints.get(face.texture);
          if (faceTint != null) {
            tint = faceTint;
            break;
          }
        }
        // apply color if we have it
        if (tint != null) {
          IQuadTransformer partTransformer = tint.color() == -1 ? quadTransformer : quadTransformer.andThen(ColoredBlockModel.applyColorQuadTransformer(tint.color()));
          ColoredBlockModel.bakePart(builder, retextureContext, part, tint.emissivity(), spriteGetter, transformation, partTransformer, uvlock, BAKE_LOCATION);
        } else {
          SimpleBlockModel.bakePart(builder, retextureContext, part, spriteGetter, transform, quadTransformer, BAKE_LOCATION);
        }
      }
      return builder.build(SimpleBlockModel.getRenderTypeGroup(owner));
    }

    /** Gets the cached model for the given materials. */
    protected abstract BakedModel getCachedModel(P materials);

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
      if (particleRetextured) {
        P materials = data.get(property);
        if (materials != null) {
          return getCachedModel(materials).getParticleIcon(data);
        }
      }
      return originalModel.getParticleIcon(data);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
      P materials = extraData.get(property);
      if (materials != null) {
        return getCachedModel(materials).getQuads(state, side, rand, extraData, renderType);
      }
      return originalModel.getQuads(state, side, rand, extraData, renderType);
    }
  }

  /** Custom overrides logic to sub in materials from NBT. */
  private static class MaterialsOverrides extends NestedOverrides {
    private final AbstractBaked<?, MaterialIdNBT> baked;
    public MaterialsOverrides(AbstractBaked<?,MaterialIdNBT> baked, ItemOverrides nested) {
      super(nested);
      this.baked = baked;
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
      return baked.getCachedModel(MaterialIdNBT.from(stack));
    }
  }

  /** Baked model for tools with multiple materials. */
  private static class BakedTool extends AbstractBaked<MaterialIdNBT,MaterialIdNBT> {
    /** Cache of texture name to baked model */
    private final Cache<MaterialIdNBT, BakedModel> cache = CacheBuilder
      .newBuilder()
      // ensure we can display every single tool that shows in JEI, plus a couple extra
      .maximumSize(MaterialRenderInfoLoader.INSTANCE.getAllRenderInfos().size() * 3L / 2)
      .build();

    private final List<Set<String>> parts;
    @Getter
    private final ItemOverrides overrides;
    private BakedTool(BakedModel original, IGeometryBakingContext owner, SimpleBlockModel model, ModelState transform, List<Set<String>> parts, boolean particleRetextured) {
      super(original, owner, model, transform, particleRetextured, ModelProperties.MATERIALS);
      this.parts = parts;
      this.overrides = new MaterialsOverrides(this, original.getOverrides());
    }

    @Override
    protected void fetchMaterials(MaterialIdNBT materials, Function<Material, TextureAtlasSprite> spriteGetter, Map<String, Material> replacements, Map<String, TintedSprite> tints) {
      for (int i = 0; i < parts.size(); i++) {
        fetchMaterial(materials.getMaterial(i), parts.get(i), spriteGetter, replacements, tints);
      }
    }

    @Override
    protected BakedModel getCachedModel(MaterialIdNBT materials) {
      if (!materials.getMaterials().isEmpty()) {
        try {
          return cache.get(materials, () -> bakeWith(materials));
        } catch (ExecutionException e) {
          TConstruct.LOG.error("Failed to get tool model from cache", e);
        }
      }
      return originalModel;
    }
  }

  /** Common logic between tool parts and tools with just 1 material. */
  private static abstract class BakedSingleMaterial<P> extends AbstractBaked<MaterialVariantId,P> {
    /** Cache of texture name to baked model */
    private final Map<MaterialVariantId, BakedModel> cache = new ConcurrentHashMap<>();

    protected final Set<String> retexture;
    private final Function<MaterialVariantId, BakedModel> baker = this::bakeWith;

    private BakedSingleMaterial(BakedModel original, IGeometryBakingContext owner, SimpleBlockModel model, ModelState transform, Set<String> retexture, ModelProperty<P> property) {
      super(original, owner, model, transform, retexture.contains("particle"), property);
      this.retexture = retexture;
    }

    /** Gets the model for the given material */
    public BakedModel getCachedModel(MaterialVariantId material) {
      if (IMaterial.UNKNOWN_ID.equals(material)) {
        return originalModel;
      }
      return cache.computeIfAbsent(material, baker);
    }

    @Override
    protected void fetchMaterials(MaterialVariantId materials, Function<Material, TextureAtlasSprite> spriteGetter, Map<String, Material> replacements, Map<String, TintedSprite> tints) {
      fetchMaterial(materials, retexture, spriteGetter, replacements, tints);
    }
  }

  /** Baked model for tools with just 1 material. */
  private static class SimpleBakedTool extends BakedSingleMaterial<MaterialIdNBT> {
    @Getter
    private final ItemOverrides overrides;
    private SimpleBakedTool(BakedModel original, IGeometryBakingContext owner, SimpleBlockModel model, ModelState transform, Set<String> retexture) {
      super(original, owner, model, transform, retexture, ModelProperties.MATERIALS);
      this.overrides = new MaterialsOverrides(this, original.getOverrides());
    }

    @Override
    protected BakedModel getCachedModel(MaterialIdNBT materials) {
      return getCachedModel(materials.getMaterial(0));
    }
  }

  /** Baked model for tool parts. */
  private static class BakedPart extends BakedSingleMaterial<MaterialVariantId> {
    @Getter
    private final ItemOverrides overrides;
    private BakedPart(BakedModel original, IGeometryBakingContext owner, SimpleBlockModel model, ModelState transform, Set<String> retexture) {
      super(original, owner, model, transform, retexture, ModelProperties.MATERIAL);
      this.overrides = new MaterialOverrides(original.getOverrides());
    }

    /** Custom overrides logic to sub in materials from NBT. */
    private class MaterialOverrides extends NestedOverrides {
      public MaterialOverrides(ItemOverrides nested) {
        super(nested);
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
        return getCachedModel(IMaterialItem.getMaterialFromStack(stack));
      }
    }
  }

  /** Model that supports both block and material textures. */
  private static class BakedAnvil extends BakedSingleMaterial<MaterialVariantId> {
    private final Map<ResourceLocation, BakedModel> blockCache = new ConcurrentHashMap<>();
    private final Function<ResourceLocation, BakedModel> blockBaker = this::bakeWithBlock;
    @Getter
    private final MaterialBlockOverrides overrides;
    private BakedAnvil(BakedModel original, IGeometryBakingContext owner, SimpleBlockModel model, ModelState transform, Set<String> retexture) {
      super(original, owner, model, transform, retexture, ModelProperties.MATERIAL);
      this.overrides = new MaterialBlockOverrides();
    }

    /** Rebakes the model with a specific texture. See also {@link RetexturedModel} */
    private BakedModel bakeWithBlock(ResourceLocation texture) {
      return this.model.bakeDynamic(new RetexturedContext(this.owner, this.retexture, texture), this.transform);
    }

    /** Gets the cached model for the given block. */
    private BakedModel getCachedModel(Block block) {
      return this.blockCache.computeIfAbsent(ModelHelper.getParticleTexture(block), blockBaker);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data) {
      // block takes priority if present
      if (particleRetextured) {
        Block block = data.get(RetexturedHelper.BLOCK_PROPERTY);
        if (block != null) {
          return getCachedModel(block).getParticleIcon(data);
        }
      }
      return super.getParticleIcon(data);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
      Block block = extraData.get(RetexturedHelper.BLOCK_PROPERTY);
      if (block != null) {
        return getCachedModel(block).getQuads(state, side, rand, extraData, renderType);
      }
      return super.getQuads(state, side, rand, extraData, renderType);
    }

    /** Custom overrides logic to sub in materials from NBT. */
    private class MaterialBlockOverrides extends ItemOverrides {
      @Nullable
      @Override
      public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
        if (stack.isEmpty() || !stack.hasTag()) {
          return originalModel;
        }
        Block block = RetexturedHelper.getTexture(stack);
        if (block != Blocks.AIR) {
          return getCachedModel(block);
        }
        return getCachedModel(IMaterialItem.getMaterialFromStack(stack));
      }
    }
  }
}
