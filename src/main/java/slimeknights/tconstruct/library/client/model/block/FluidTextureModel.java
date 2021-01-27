package slimeknights.tconstruct.library.client.model.block;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import lombok.AllArgsConstructor;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import slimeknights.mantle.client.model.RetexturedModel;
import slimeknights.mantle.client.model.util.DynamicBakedWrapper;
import slimeknights.mantle.client.model.util.SimpleBlockModel;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.smeltery.tileentity.tank.IDisplayFluidListener;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

/**
 * Model that replaces fluid textures with the fluid from model data
 */
@AllArgsConstructor
public class FluidTextureModel implements IModelGeometry<FluidTextureModel> {
  public static final Loader LOADER = new Loader();

  private final SimpleBlockModel model;
  private final Set<String> fluids;

  @Override
  public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation,IUnbakedModel> modelGetter, Set<Pair<String,String>> missingTextureErrors) {
    return model.getTextures(owner, modelGetter, missingTextureErrors);
  }

  @Override
  public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial,TextureAtlasSprite> spriteGetter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation modelLocation) {
    IBakedModel baked = model.bakeModel(owner, transform, overrides, spriteGetter, modelLocation);
    return new Baked(baked, model, owner, transform, RetexturedModel.getAllRetextured(owner, model, this.fluids));
  }

  /** Baked wrapper class */
  private static class Baked extends DynamicBakedWrapper<IBakedModel> {
    private final Map<Fluid,IBakedModel> cache = new HashMap<>();
    private final SimpleBlockModel model;
    private final IModelConfiguration owner;
    private final IModelTransform transform;
    private final Set<String> fluids;
    protected Baked(IBakedModel originalModel, SimpleBlockModel model, IModelConfiguration owner, IModelTransform transform, Set<String> fluids) {
      super(originalModel);
      this.model = model;
      this.owner = owner;
      this.transform = transform;
      this.fluids = fluids;
    }

    /** Retextures a model for the given fluid */
    private IBakedModel getRetexturedModel(Fluid fluid) {
      return this.model.bakeDynamic(new RetexturedModel.RetexturedConfiguration(this.owner, this.fluids, fluid.getAttributes().getStillTexture()), this.transform);
    }

    /** Gets a retextured model for the given fluid, using the cached model if possible */
    private IBakedModel getCachedModel(Fluid fluid) {
      return this.cache.computeIfAbsent(fluid, this::getRetexturedModel);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, Random random, IModelData data) {
      Fluid fluid = data.getData(IDisplayFluidListener.PROPERTY);
      if (fluid != null && fluid != Fluids.EMPTY) {
        return getCachedModel(fluid).getQuads(state, direction, random, data);
      }
      return originalModel.getQuads(state, direction, random, data);
    }
  }

  /** Model loader class */
  private static class Loader implements IModelLoader<FluidTextureModel> {
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {}

    @Override
    public FluidTextureModel read(JsonDeserializationContext context, JsonObject json) {
      SimpleBlockModel model = SimpleBlockModel.deserialize(context, json);
      Set<String> fluids = ImmutableSet.copyOf(JsonHelper.parseList(json, "fluids", JSONUtils::getString));
      return new FluidTextureModel(model, fluids);
    }
  }
}
