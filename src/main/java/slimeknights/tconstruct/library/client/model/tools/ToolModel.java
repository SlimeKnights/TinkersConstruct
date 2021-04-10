package slimeknights.tconstruct.library.client.model.tools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

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
    return allTextures;
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
  private static IBakedModel bakeInternal(IModelConfiguration owner, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, @Nullable TransformationMatrix largeTransforms, List<ToolPart> parts, List<MaterialId> materials, boolean isBroken, ItemOverrideList overrides) {
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
    overrides = new MaterialOverrideHandler(owner, toolParts, largeTransforms); // TODO: nest original overrides?
    return bakeInternal(owner, spriteGetter, largeTransforms, toolParts, Collections.emptyList(), false, overrides);
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
    private final Map<List<MaterialId>, IBakedModel> cleanCache = new HashMap<>();
    private final Map<List<MaterialId>, IBakedModel> brokenCache = new HashMap<>();

    // parameters needed for rebaking
    private final IModelConfiguration owner;
    private final List<ToolPart> toolParts;
    @Nullable
    private final TransformationMatrix largeTransforms;
    private MaterialOverrideHandler(IModelConfiguration owner, List<ToolPart> toolParts, @Nullable TransformationMatrix largeTransforms) {
      this.owner = owner;
      this.toolParts = toolParts;
      this.largeTransforms = largeTransforms;
    }

    /**
     * Bakes a copy of this model using the given material
     * @param materials  New materials for the model
     * @return  Baked model
     */
    private IBakedModel bakeDynamic(List<MaterialId> materials, boolean isBroken) {
      // bake internal does not require an instance to bake, we can pass in whatever material we want
      // use empty override list as the sub model never calls overrides, and already has a material
      return bakeInternal(owner, ModelLoader.defaultTextureGetter(), largeTransforms, toolParts, materials, isBroken, ItemOverrideList.EMPTY);
    }

    @Override
    public IBakedModel getOverrideModel(IBakedModel originalModel, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
      List<MaterialId> materialIds = MaterialIdNBT.from(stack).getMaterials();
      boolean broken = ToolDamageUtil.isBroken(stack);
      if (materialIds.isEmpty() && !broken) {
        return originalModel;
      }
      // cache all baked material models, they will not need to be recreated as materials will not change
      if (broken) {
        return brokenCache.computeIfAbsent(materialIds, mats -> bakeDynamic(mats, true));
      }
      return cleanCache.computeIfAbsent(materialIds, mats -> bakeDynamic(mats, false));
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
      return new ToolModel(parts, isLarge, offset);
    }
  }
}
