package slimeknights.tconstruct.library.client.model.tesr;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import slimeknights.tconstruct.library.client.model.ModelUtils;
import slimeknights.tconstruct.library.client.model.data.FluidCuboid;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * This model contains a list of fluid cuboids for the sake of rendering multiple fluid regions in world. It is used by the faucet at this time
 */
@AllArgsConstructor
public class FluidsModel implements IModelGeometry<FluidsModel> {
  private final BlockModel model;
  private final List<FluidCuboid> fluids;

  @Override
  public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation,IUnbakedModel> modelGetter, Set<Pair<String,String>> missingTextureErrors) {
    return model.getTextures(modelGetter, missingTextureErrors);
  }

  @Override
  public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial,TextureAtlasSprite> spriteGetter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation location) {
    IBakedModel baked = model.bakeModel(bakery, model, spriteGetter, transform, location, true);
    return new BakedModel(baked, fluids);
  }

  /** Baked model, mostly a data wrapper around a normal model */
  public static class BakedModel extends BakedModelWrapper<IBakedModel> {
    @Getter
    private final List<FluidCuboid> fluids;
    private BakedModel(IBakedModel originalModel, List<FluidCuboid> fluids) {
      super(originalModel);
      this.fluids = fluids;
    }
  }

  /** Loader for this model */
  public static class Loader implements IModelLoader<FluidsModel> {
    /**
     * Shared loader instance
     */
    public static final Loader INSTANCE = new Loader();

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {}

    @Override
    public FluidsModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
      BlockModel model = ModelUtils.deserialize(deserializationContext, modelContents);
      List<FluidCuboid> fluid = FluidCuboid.listFromJson(modelContents, "fluids");
      return new FluidsModel(model, fluid);
    }
  }
}
