package slimeknights.tconstruct.library.client.model.block;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
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
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import slimeknights.mantle.client.model.util.ExtraTextureConfiguration;
import slimeknights.mantle.client.model.util.SimpleBlockModel;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.model.ModelProperties;
import slimeknights.tconstruct.smeltery.item.TankItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * This model contains a single scalable fluid that can either be statically rendered or rendered in the TESR. It also supports rendering fluids in the item model
 */
@Log4j2
@AllArgsConstructor
public class TankModel implements IModelGeometry<TankModel> {
  /** Shared loader instance */
  public static final Loader LOADER = new Loader();

  protected final SimpleBlockModel model;
  protected final IncrementalFluidCuboid fluid;

  @Override
  public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation,IUnbakedModel> modelGetter, Set<Pair<String,String>> missingTextureErrors) {
    return model.getTextures(owner, modelGetter, missingTextureErrors);
  }

  @Override
  public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial,TextureAtlasSprite> spriteGetter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation location) {
    IBakedModel baked = model.bakeModel(owner, transform, overrides, spriteGetter, location);
    return new BakedModel<>(owner, transform, baked, this);
  }

  /** Override to add the fluid part to the item model */
  private static class FluidPartOverride extends ItemOverrideList {
    /** Shared override instance, since the logic is not model dependent */
    public static final FluidPartOverride INSTANCE = new FluidPartOverride();

    @Override
    public IBakedModel func_239290_a_(IBakedModel model, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
      // ensure we have a fluid
      if (stack.isEmpty() || !stack.hasTag()) {
        return model;
      }
      // determine fluid
      FluidTank tank = TankItem.getFluidTank(stack);
      if (tank.isEmpty()) {
        return model;
      }
      // always baked model as this override is only used in our model
      return ((BakedModel)model).getCachedModel(tank.getFluid(), tank.getCapacity());
    }
  }

  /**
   * Baked variant to load in the custom overrides
   * @param <T>  Parent model type, used to make this easier to extend
   */
  public static class BakedModel<T extends TankModel> extends BakedModelWrapper<IBakedModel> {
    private final IModelConfiguration owner;
    private final IModelTransform originalTransforms;
    @SuppressWarnings("WeakerAccess")
    protected final T original;
    private final Cache<FluidStack, IBakedModel> cache = CacheBuilder
      .newBuilder()
      .maximumSize(64)
      .build();

    @SuppressWarnings("WeakerAccess")
    protected BakedModel(IModelConfiguration owner, IModelTransform transforms, IBakedModel baked, T original) {
      super(baked);
      this.owner = owner;
      this.originalTransforms = transforms;
      this.original = original;
    }

    @Override
    public ItemOverrideList getOverrides() {
      return FluidPartOverride.INSTANCE;
    }

    /**
     * Gets the model with the fluid part added
     * @param stack  Fluid stack to add
     * @return  Model with the fluid part
     */
    private IBakedModel getModel(FluidStack stack) {
      // add fluid texture
      Map<String,RenderMaterial> textures = new HashMap<>();
      FluidAttributes attributes = stack.getFluid().getAttributes();
      textures.put("fluid", ModelLoaderRegistry.blockMaterial(attributes.getStillTexture(stack)));
      textures.put("flowing_fluid", ModelLoaderRegistry.blockMaterial(attributes.getFlowingTexture(stack)));
      IModelConfiguration textured = new ExtraTextureConfiguration(owner, textures);

      // add fluid part
      // TODO: fullbright for fluids with light level
      List<BlockPart> elements = Lists.newArrayList(original.model.getElements());
      elements.add(original.fluid.getPart(stack.getAmount(), attributes.isGaseous(stack)));

      // bake the model
      return SimpleBlockModel.bakeDynamic(textured, elements, originalTransforms);
    }

    /**
     * Gets a cached model with the fluid part added
     * @param fluid  Scaled contained fluid
     * @return  Cached model
     */
    private IBakedModel getCachedModel(FluidStack fluid) {
      try {
        return cache.get(fluid, () -> getModel(fluid));
      }
      catch(ExecutionException e) {
        log.error(e);
        return this;
      }
    }

    /**
     * Gets a cached model with the fluid part added
     * @param fluid     Fluid contained
     * @param capacity  Tank capacity
     * @return  Cached model
     */
    private IBakedModel getCachedModel(FluidStack fluid, int capacity) {
      int increments = original.fluid.getIncrements();
      return getCachedModel(new FluidStack(fluid.getFluid(), Math.min(fluid.getAmount() * increments / capacity, increments)));
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
      if (Config.CLIENT.tankFluidModel.get() && data.hasProperty(ModelProperties.FLUID_TANK)) {
        IFluidTank tank = data.getData(ModelProperties.FLUID_TANK);
        if (tank != null && !tank.getFluid().isEmpty()) {
          return getCachedModel(tank.getFluid(), tank.getCapacity()).getQuads(state, side, rand, EmptyModelData.INSTANCE);
        }
      }
      return originalModel.getQuads(state, side, rand, data);
    }

    /**
     * Gets the fluid location
     * @return  Fluid location data
     */
    public IncrementalFluidCuboid getFluid() {
      return original.fluid;
    }
  }

  /** Loader for this model */
  public static class Loader implements IModelLoader<TankModel> {
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {}

    @Override
    public TankModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
      SimpleBlockModel model = SimpleBlockModel.deserialize(deserializationContext, modelContents);
      IncrementalFluidCuboid fluid = IncrementalFluidCuboid.fromJson(JSONUtils.getJsonObject(modelContents, "fluid"));
      return new TankModel(model, fluid);
    }
  }
}
