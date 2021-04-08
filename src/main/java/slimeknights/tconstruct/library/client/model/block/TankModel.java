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
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;
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

import org.jetbrains.annotations.Nonnull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
  @Nullable
  protected final SimpleBlockModel gui;
  protected final IncrementalFluidCuboid fluid;

  @Override
  public Collection<SpriteIdentifier> getTextures(IModelConfiguration owner, Function<Identifier,UnbakedModel> modelGetter, Set<Pair<String,String>> missingTextureErrors) {
    Collection<SpriteIdentifier> textures = new HashSet<>(model.getTextures(owner, modelGetter, missingTextureErrors));
    if (gui != null) {
      textures.addAll(gui.getTextures(owner, modelGetter, missingTextureErrors));
    }
    return textures;
  }

  @Override
  public net.minecraft.client.render.model.BakedModel bake(IModelConfiguration owner, ModelLoader bakery, Function<SpriteIdentifier,Sprite> spriteGetter, ModelBakeSettings transform, ModelOverrideList overrides, Identifier location) {
    net.minecraft.client.render.model.BakedModel baked = model.bakeModel(owner, transform, overrides, spriteGetter, location);
    // bake the GUI model if present
    net.minecraft.client.render.model.BakedModel bakedGui = baked;
    if (gui != null) {
      bakedGui = gui.bakeModel(owner, transform, overrides, spriteGetter, location);
    }
    return new BakedModel<>(owner, transform, baked, bakedGui, this);
  }

  /** Override to add the fluid part to the item model */
  private static class FluidPartOverride extends ModelOverrideList {
    /** Shared override instance, since the logic is not model dependent */
    public static final FluidPartOverride INSTANCE = new FluidPartOverride();

    @Override
    public net.minecraft.client.render.model.BakedModel apply(net.minecraft.client.render.model.BakedModel model, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
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
      return ((BakedModel<?>)model).getCachedModel(tank.getFluid(), tank.getCapacity());
    }
  }

  /**
   * Wrapper that swaps the model for the GUI
   */
  private static class BakedGuiUniqueModel extends BakedModelWrapper<net.minecraft.client.render.model.BakedModel> {
    private final net.minecraft.client.render.model.BakedModel gui;
    public BakedGuiUniqueModel(net.minecraft.client.render.model.BakedModel base, net.minecraft.client.render.model.BakedModel gui) {
      super(base);
      this.gui = gui;
    }

    /* Swap out GUI model if needed */

    @Override
    public boolean doesHandlePerspectives() {
      return true;
    }

    @Override
    public net.minecraft.client.render.model.BakedModel handlePerspective(Mode cameraTransformType, MatrixStack mat) {
      if (cameraTransformType == Mode.GUI) {
        return gui.handlePerspective(cameraTransformType, mat);
      }
      return originalModel.handlePerspective(cameraTransformType, mat);
    }
  }

  /**
   * Baked variant to load in the custom overrides
   * @param <T>  Parent model type, used to make this easier to extend
   */
  public static class BakedModel<T extends TankModel> extends BakedGuiUniqueModel {
    private final IModelConfiguration owner;
    private final ModelBakeSettings originalTransforms;
    @SuppressWarnings("WeakerAccess")
    protected final T original;
    private final Cache<FluidStack, net.minecraft.client.render.model.BakedModel> cache = CacheBuilder
      .newBuilder()
      .maximumSize(64)
      .build();

    @SuppressWarnings("WeakerAccess")
    protected BakedModel(IModelConfiguration owner, ModelBakeSettings transforms, net.minecraft.client.render.model.BakedModel baked, net.minecraft.client.render.model.BakedModel gui, T original) {
      super(baked, gui);
      this.owner = owner;
      this.originalTransforms = transforms;
      this.original = original;
    }

    @Override
    public ModelOverrideList getOverrides() {
      return FluidPartOverride.INSTANCE;
    }

    /**
     * Gets the model with the fluid part added
     * @param stack  Fluid stack to add
     * @return  Model with the fluid part
     */
    private net.minecraft.client.render.model.BakedModel getModel(FluidStack stack) {
      // add fluid texture
      Map<String,SpriteIdentifier> textures = new HashMap<>();
      FluidAttributes attributes = stack.getFluid().getAttributes();
      textures.put("fluid", ModelLoaderRegistry.blockMaterial(attributes.getStillTexture(stack)));
      textures.put("flowing_fluid", ModelLoaderRegistry.blockMaterial(attributes.getFlowingTexture(stack)));
      IModelConfiguration textured = new ExtraTextureConfiguration(owner, textures);

      // add fluid part
      // TODO: fullbright for fluids with light level
      List<ModelElement> elements = Lists.newArrayList(original.model.getElements());
      ModelElement fluid = original.fluid.getPart(stack.getAmount(), attributes.isGaseous(stack));
      elements.add(fluid);
      // bake the model
      net.minecraft.client.render.model.BakedModel baked = SimpleBlockModel.bakeDynamic(textured, elements, originalTransforms);

      // if we have GUI, bake a GUI variant
      if (original.gui != null) {
        elements = Lists.newArrayList(original.gui.getElements());
        elements.add(fluid);
        baked = new BakedGuiUniqueModel(baked, SimpleBlockModel.bakeDynamic(textured, elements, originalTransforms));
      }

      // return what we ended up with
      return baked;
    }

    /**
     * Gets a cached model with the fluid part added
     * @param fluid  Scaled contained fluid
     * @return  Cached model
     */
    private net.minecraft.client.render.model.BakedModel getCachedModel(FluidStack fluid) {
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
    private net.minecraft.client.render.model.BakedModel getCachedModel(FluidStack fluid, int capacity) {
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
    public void apply(ResourceManager resourceManager) {}

    @Override
    public TankModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
      SimpleBlockModel model = SimpleBlockModel.deserialize(deserializationContext, modelContents);
      SimpleBlockModel gui = null;
      if (modelContents.has("gui")) {
        gui = SimpleBlockModel.deserialize(deserializationContext, JsonHelper.getObject(modelContents, "gui"));
      }
      IncrementalFluidCuboid fluid = IncrementalFluidCuboid.fromJson(JsonHelper.getObject(modelContents, "fluid"));
      return new TankModel(model, gui, fluid);
    }
  }
}
