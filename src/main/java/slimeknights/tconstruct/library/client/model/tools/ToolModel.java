package slimeknights.tconstruct.library.client.model.tools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.AffineTransformation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.model.JsonModelResourceProvider;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ToolModel implements TinkerModelGeometry {

  /**
   * Shared loader instance
   */
  public static final Loader LOADER = new Loader();

  /**
   * List of tool parts in this model
   */
  private final List<ToolPart> toolParts;
  /**
   * If true, this is a large tool and uses double resolution textures in hand
   */
  private final boolean isLarge;
  /**
   * Transform matrix to apply to child parts
   */
  private final Vec2f offset;

  private ModelOverrideList overrides;

  public ToolModel(List<ToolPart> toolParts, boolean isLarge, Vec2f offset) {
    this.toolParts = toolParts;
    this.isLarge = isLarge;
    this.offset = offset;
  }

  /**
   * Same as {@link #bake(ModelLoader, Function, ModelBakeSettings, Identifier)} , but uses fewer arguments and does not require an instance
   *
   * @param owner           Model configuration
   * @param spriteGetter    Sprite getter function
   * @param largeTransforms Transform to apply to the large parts. If null, only generates small parts
   * @param parts           List of tool parts in this tool
   * @param materials       Materials to use for the parts
   * @param isBroken        If true, generates broken model
   * @param overrides       Override instance to use, will either be empty or {@link MaterialOverrideHandler}
   * @return Baked model
   */
  private static BakedModel bakeInternal(ModelLoader owner, Function<SpriteIdentifier, Sprite> spriteGetter, AffineTransformation largeTransforms, List<ToolPart> parts, List<MaterialId> materials, boolean isBroken, ModelOverrideList overrides) {
    Sprite particle = null;
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
      particle = MaterialModel.getPartQuads(smallConsumer, owner, spriteGetter, AffineTransformation.identity(), part.getName(isBroken, false), index, material);
      if (largeTransforms != null) {
        MaterialModel.getPartQuads(largeConsumer, owner, spriteGetter, largeTransforms, part.getName(isBroken, true), index, material);
      }
    }
    assert particle != null;

    // bake model - while the transform may not be identity, it never has rotation so its safe to say untransformed
//    ImmutableMap<Mode, AffineTransformation> transformMap = Maps.immutableEnumMap(PerspectiveMapWrapper.getTransforms(owner.getCombinedTransform()));

    // large models use a custom model here
    if (largeTransforms != null) {
      return new BakedLargeToolModel(largeBuilder.build(), smallBuilder.build(), particle, null, overrides, true);
    }
    // for small, we leave out the large quads, so the baked item model logic is sufficient
    throw new RuntimeException("Large transforms where not supported and fabric sucks with its model api");
//    return new BakedItemModel(smallBuilder.build(), particle, null, overrides, true, true);
  }

  @Nullable
  @Override
  public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
    AffineTransformation largeTransforms = isLarge ? new AffineTransformation(new Vector3f((offset.x - 8) / 32, (-offset.y - 8) / 32, 0), null, new Vector3f(2, 2, 1), null) : null;
    overrides = new MaterialOverrideHandler(loader, toolParts, largeTransforms); // TODO: nest original overrides?
    return bakeInternal(loader, textureGetter, largeTransforms, toolParts, Collections.emptyList(), false, overrides);
  }

  @Override
  public boolean isVanillaAdapter() {
    return false;
  }

  @Override
  public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {

  }

  @Override
  public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {

  }

  @Override
  public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
    return null;
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
    return overrides;
  }

  @Override
  public Collection<Identifier> getModelDependencies() {
    return null;
  }

  @Override
  public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
    Set<SpriteIdentifier> allTextures = Sets.newHashSet();
    for (ToolPart part : toolParts) {
      MaterialModel.getMaterialTextures(allTextures, part.getName(false, false), null);
      if (part.hasBroken()) {
        MaterialModel.getMaterialTextures(allTextures, part.getName(true, false), null);
      }
      if (isLarge) {
        MaterialModel.getMaterialTextures(allTextures, part.getName(false, true), null);
        if (part.hasBroken()) {
          MaterialModel.getMaterialTextures(allTextures, part.getName(true, true), null);
        }
      }
    }
    return allTextures;
  }

  /**
   * Data class for a single tool part
   */
  private static class ToolPart {

    private final String name;
    private final int index;
    @Nullable
    private final String broken;

    public ToolPart(String name, int index, @Nullable String broken) {
      this.name = name;
      this.index = index;
      this.broken = broken;
    }

    /**
     * If true, this part has a broken texture
     */
    public boolean hasBroken() {
      return broken != null;
    }

    /**
     * Gets the name for this part
     *
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
      String name = net.minecraft.util.JsonHelper.getString(json, "name");
      int index = net.minecraft.util.JsonHelper.getInt(json, "index");
      String broken = null;
      if (json.has("broken")) {
        broken = net.minecraft.util.JsonHelper.getString(json, "broken");
      }
      return new ToolPart(name, index, broken);
    }

    public int getIndex() {
      return this.index;
    }
  }

  /**
   * Dynamic override handler to swap in the material texture
   */
  private static final class MaterialOverrideHandler extends ModelOverrideList {

    // contains all the baked models since they'll never change, cleared automatically as the baked model is discarded
    private final Map<List<MaterialId>, BakedModel> cleanCache = new HashMap<>();
    private final Map<List<MaterialId>, BakedModel> brokenCache = new HashMap<>();

    // parameters needed for rebaking
    private final ModelLoader owner;
    private final List<ToolPart> toolParts;
    @Nullable
    private final AffineTransformation largeTransforms;

    private MaterialOverrideHandler(ModelLoader owner, List<ToolPart> toolParts, AffineTransformation largeTransforms) {
      this.owner = owner;
      this.toolParts = toolParts;
      this.largeTransforms = largeTransforms;
    }

    /**
     * Bakes a copy of this model using the given material
     *
     * @param materials New materials for the model
     * @return Baked model
     */
    private BakedModel bakeDynamic(List<MaterialId> materials, boolean isBroken) {
      // bake internal does not require an instance to bake, we can pass in whatever material we want
      // use empty override list as the sub model never calls overrides, and already has a material
      return bakeInternal(owner, SpriteIdentifier::getSprite, largeTransforms, toolParts, materials, isBroken, ModelOverrideList.EMPTY);
    }

    @Override
    public BakedModel apply(BakedModel originalModel, ItemStack stack, ClientWorld world, LivingEntity entity) {
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

  /**
   * Baked model for large tools, has separate quads in GUIs
   */
  private static class BakedLargeToolModel implements BakedModel {

    private final ImmutableList<BakedQuad> largeQuads;
    private final Sprite particleTexture;
    private final ImmutableMap<Mode, AffineTransformation> transforms;
    private final ModelOverrideList overrides;
    private final boolean isSideLit;
    private final BakedModel guiModel;

    private BakedLargeToolModel(ImmutableList<BakedQuad> largeQuads, ImmutableList<BakedQuad> smallQuads, Sprite particle, ImmutableMap<Mode, AffineTransformation> transforms, ModelOverrideList overrides, boolean isSideLit) {
      this.largeQuads = largeQuads;
      this.particleTexture = particle;
      this.transforms = transforms;
      this.overrides = overrides;
      this.isSideLit = isSideLit;
      this.guiModel = new BakedLargeToolGui(this, smallQuads);
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
      if (side == null) {
        return largeQuads;
      }
      return ImmutableList.of();
    }
    /* Misc properties */

    @Override
    public boolean useAmbientOcclusion() {
      return true;
    }

    @Override
    public boolean hasDepth() {
      return false;
    }

    @Override
    public boolean isBuiltin() {
      return false;
    }

    @Override
    public Sprite getSprite() {
      return particleTexture;
    }

    @Override
    public ModelTransformation getTransformation() {
      return ModelTransformation.NONE;
    }

    public Sprite getParticleTexture() {
      return this.particleTexture;
    }

    public ModelOverrideList getOverrides() {
      return this.overrides;
    }

    public boolean isSideLit() {
      return this.isSideLit;
    }
  }

  /**
   * Baked model for large tools in the GUI, small quads
   */
  private static class BakedLargeToolGui implements BakedModel {

    private final List<BakedQuad> guiQuads;
    private final BakedLargeToolModel originalModel;

    public BakedLargeToolGui(BakedLargeToolModel model, List<BakedQuad> guiQuads) {
      this.originalModel = model;
      this.guiQuads = guiQuads;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
      // these quads are only shown in handle perspective for GUI
      if (side == null) {
        return guiQuads;
      }
      return ImmutableList.of();
    }

    @Override
    public boolean useAmbientOcclusion() {
      return originalModel.useAmbientOcclusion();
    }

    @Override
    public boolean hasDepth() {
      return originalModel.hasDepth();
    }

    @Override
    public boolean isSideLit() {
      return originalModel.isSideLit();
    }

    @Override
    public boolean isBuiltin() {
      return originalModel.isBuiltin();
    }

    @Override
    public Sprite getSprite() {
      return originalModel.getSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
      return originalModel.getTransformation();
    }

    @Override
    public ModelOverrideList getOverrides() {
      return originalModel.getOverrides();
    }
  }

  /**
   * Model loader logic, use {@link #LOADER} to access instance
   */
  private static class Loader extends JsonModelResourceProvider{

    public Loader() {
      super(Util.getResource("tool").toString());
    }

    @Override
    public UnbakedModel loadJsonModelResource(Identifier resourceId, JsonObject modelContents, ModelProviderContext context) {
      List<ToolPart> parts = JsonHelper.parseList(modelContents, "parts", ToolPart::read);
      boolean isLarge = net.minecraft.util.JsonHelper.getBoolean(modelContents, "large", false);
      Vec2f offset = Vec2f.ZERO;
      if (modelContents.has("large_offset")) {
        offset = MaterialModel.arrayToObject(modelContents, "large_offset");
      }
      return new ToolModel(parts, isLarge, offset);
    }
  }
}
