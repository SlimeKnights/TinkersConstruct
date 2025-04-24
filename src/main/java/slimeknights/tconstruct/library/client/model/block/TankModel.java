package slimeknights.tconstruct.library.client.model.block;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import slimeknights.mantle.client.model.util.ColoredBlockModel;
import slimeknights.mantle.client.model.util.ExtraTextureContext;
import slimeknights.mantle.client.model.util.SimpleBlockModel;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.model.ModelProperties;
import slimeknights.tconstruct.library.client.model.UniqueGuiModel;
import slimeknights.tconstruct.smeltery.item.TankItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * This model contains a single scalable fluid that can either be statically rendered or rendered in the TESR. It also supports rendering fluids in the item model
 */
@AllArgsConstructor
public class TankModel implements IUnbakedGeometry<TankModel> {
  protected static final ResourceLocation BAKE_LOCATION = TConstruct.getResource("dynamic_model_baking");

  /** Shared loader instance */
  public static final IGeometryLoader<TankModel> LOADER = TankModel::deserialize;

  protected final SimpleBlockModel model;
  @Nullable
  protected final SimpleBlockModel gui;
  protected final IncrementalFluidCuboid fluid;
  protected final boolean forceModelFluid;

  @Override
  public void resolveParents(Function<ResourceLocation,UnbakedModel> modelGetter, IGeometryBakingContext context) {
    model.resolveParents(modelGetter, context);
    if (gui != null) {
      gui.resolveParents(modelGetter, context);
    }
  }

  @Override
  public BakedModel bake(IGeometryBakingContext owner, ModelBaker baker, Function<Material,TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation location) {
    BakedModel baked = model.bake(owner, baker, spriteGetter, transform, overrides, location);
    // bake the GUI model if present
    BakedModel bakedGui = baked;
    if (gui != null) {
      bakedGui = gui.bake(owner, baker, spriteGetter, transform, overrides, location);
    }
    return new Baked(owner, transform, baked, bakedGui, this);
  }

  /**
   * Baked variant to load in the custom overrides
   */
  private static class Baked extends UniqueGuiModel.Baked {
    private final IGeometryBakingContext owner;
    private final ModelState originalTransforms;
    @SuppressWarnings("WeakerAccess")
    protected final TankModel original;
    private final FluidPartOverride overrides = new FluidPartOverride();
    private final Cache<CacheKey,BakedModel> cache = CacheBuilder
      .newBuilder()
      .maximumSize(64)
      .build();

    /** Cache key since fluids don't do equality over amount */
    private record CacheKey(FluidStack fluid, int increments) {}

    @SuppressWarnings("WeakerAccess")
    protected Baked(IGeometryBakingContext owner, ModelState transforms, BakedModel baked, BakedModel gui, TankModel original) {
      super(baked, gui);
      this.owner = owner;
      this.originalTransforms = transforms;
      this.original = original;
    }

    @Override
    public ItemOverrides getOverrides() {
      return overrides;
    }

    /**
     * Bakes the model with the given fluid element
     * @param owner        Owner for baking, should include the fluid texture
     * @param baseModel    Base model for original elements
     * @param fluid        Fluid element for baking
     * @param color        Color for the fluid part
     * @param luminosity   Luminosity for the fluid part
     * @return  Baked model
     */
    private BakedModel bakeWithFluid(IGeometryBakingContext owner, SimpleBlockModel baseModel, BlockElement fluid, int color, int luminosity) {
      // setup for baking, using dynamic location and sprite getter
      Function<Material,TextureAtlasSprite> spriteGetter = Material::sprite;
      TextureAtlasSprite particle = spriteGetter.apply(owner.getMaterial("particle"));
      SimpleBakedModel.Builder builder = SimpleBlockModel.bakedBuilder(owner, ItemOverrides.EMPTY).particle(particle);
      IQuadTransformer quadTransformer = SimpleBlockModel.applyTransform(originalTransforms, owner.getRootTransform());
      // first, add all regular elements
      for (BlockElement element : baseModel.getElements()) {
        SimpleBlockModel.bakePart(builder, owner, element, spriteGetter, originalTransforms, quadTransformer, BAKE_LOCATION);
      }
      // next, add in the fluid
      IQuadTransformer fluidTransformer = color == -1 ? quadTransformer : quadTransformer.andThen(ColoredBlockModel.applyColorQuadTransformer(color));
      ColoredBlockModel.bakePart(builder, owner, fluid, luminosity, spriteGetter, originalTransforms.getRotation(), fluidTransformer, originalTransforms.isUvLocked(), BAKE_LOCATION);
      return builder.build(SimpleBlockModel.getRenderTypeGroup(owner));
    }

    /**
     * Gets the model with the fluid part added
     * @param key  Cache key containing fluid and increments
     * @return  Model with the fluid part
     */
    private BakedModel getModel(CacheKey key) {
      // fetch fluid data
      FluidStack stack = key.fluid();
      IClientFluidTypeExtensions attributes = IClientFluidTypeExtensions.of(stack.getFluid());
      FluidType type = stack.getFluid().getFluidType();
      int color = attributes.getTintColor(stack);
      int luminosity = type.getLightLevel(stack);
      Map<String,Material> textures = ImmutableMap.of(
        "fluid", new Material(InventoryMenu.BLOCK_ATLAS, attributes.getStillTexture(stack)),
        "flowing_fluid", new Material(InventoryMenu.BLOCK_ATLAS, attributes.getFlowingTexture(stack)));
      IGeometryBakingContext textured = new ExtraTextureContext(owner, textures);

      // add fluid part
      BlockElement fluid = original.fluid.getPart(key.increments, type.isLighterThanAir());
      // bake the model
      BakedModel baked = bakeWithFluid(textured, original.model, fluid, color, luminosity);

      // if we have GUI, bake a GUI variant
      if (original.gui != null) {
        baked = new UniqueGuiModel.Baked(baked, bakeWithFluid(textured, original.gui, fluid, color, 0));
      }

      // return what we ended up with
      return baked;
    }

    /**
     * Gets a cached model with the fluid part added
     * @param fluid  Scaled contained fluid
     * @return  Cached model
     */
    private BakedModel getCachedModel(CacheKey fluid) {
      try {
        return cache.get(fluid, () -> getModel(fluid));
      }
      catch(ExecutionException e) {
        TConstruct.LOG.error(e);
        return this;
      }
    }

    /**
     * Gets a cached model with the fluid part added
     * @param fluid     Fluid contained
     * @param capacity  Tank capacity
     * @return  Cached model
     */
    private BakedModel getCachedModel(FluidStack fluid, int capacity) {
      int increments = original.fluid.getIncrements();
      return getCachedModel(new CacheKey(fluid.copy(), Mth.clamp(fluid.getAmount() * increments / capacity, 1, increments)));
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData data, @Nullable RenderType renderType) {
      if ((original.forceModelFluid || Config.CLIENT.tankFluidModel.get()) && data.has(ModelProperties.FLUID_STACK)) {
        FluidStack fluid = data.get(ModelProperties.FLUID_STACK);
        if (fluid != null && !fluid.isEmpty()) {
          int capacity = Objects.requireNonNullElse(data.get(ModelProperties.TANK_CAPACITY), fluid.getAmount());
          return getCachedModel(fluid, capacity).getQuads(state, side, rand, ModelData.EMPTY, renderType);
        }
      }
      return originalModel.getQuads(state, side, rand, data, renderType);
    }

    /** Override to add the fluid part to the item model */
    private class FluidPartOverride extends ItemOverrides {
      @Override
      public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
        // ensure we have a fluid
        if (stack.isEmpty() || !stack.hasTag()) {
          return model;
        }
        // determine fluid
        FluidTank tank = TankItem.getTank(stack, 1);
        if (tank.isEmpty()) {
          return model;
        }
        // always baked model as this override is only used in our model
        return getCachedModel(tank.getFluid(), tank.getCapacity());
      }
    }
  }


  /** Loader for this model */
  public static TankModel deserialize(JsonObject json, JsonDeserializationContext context) {
    SimpleBlockModel model = SimpleBlockModel.deserialize(json, context);
    SimpleBlockModel gui = null;
    if (json.has("gui")) {
      gui = SimpleBlockModel.deserialize(GsonHelper.getAsJsonObject(json, "gui"), context);
    }
    IncrementalFluidCuboid fluid = IncrementalFluidCuboid.fromJson(GsonHelper.getAsJsonObject(json, "fluid"));
    boolean forceModelFluid = GsonHelper.getAsBoolean(json, "render_fluid_in_model", false);
    return new TankModel(model, gui, fluid, forceModelFluid);
  }
}
