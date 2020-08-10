package slimeknights.tconstruct.library.client.model.tesr;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import slimeknights.tconstruct.library.client.model.ModelUtils;
import slimeknights.tconstruct.library.client.model.data.IncrementalFluidCuboid;
import slimeknights.tconstruct.library.client.model.data.ModelItem;

import java.util.List;
import java.util.function.Function;

/**
 * This model contains a list of items to display in the TESR, plus a single scalable fluid that can either be statically rendered or rendered in the TESR
 */
public class MelterModel extends TankModel {
  private final List<ModelItem> items;
  public MelterModel(BlockModel model, IncrementalFluidCuboid fluid, List<ModelItem> items) {
    super(model, fluid);
    this.items = items;
  }

  @Override
  public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial,TextureAtlasSprite> spriteGetter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation location) {
    IBakedModel baked = model.bakeModel(bakery, model, spriteGetter, transform, location, true);
    return new BakedModel(bakery, transform, baked, this);
  }

  /** Baked variant to allow access to items */
  public static final class BakedModel extends TankModel.BakedModel<MelterModel> {
    protected BakedModel(ModelBakery bakery, IModelTransform transforms, IBakedModel baked, MelterModel original) {
      super(bakery, transforms, baked, original);
    }

    /**
     * Gets a list of items used in inventory display
     * @return  Item list
     */
    public List<ModelItem> getItems() {
      return this.original.items;
    }
  }

  /** Loader for this model */
  public static class Loader implements IModelLoader<TankModel> {
    /**
     * Shared loader instance
     */
    public static final Loader INSTANCE = new Loader();

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {}

    @Override
    public TankModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
      BlockModel model = ModelUtils.deserialize(deserializationContext, modelContents);
      IncrementalFluidCuboid fluid = IncrementalFluidCuboid.fromJson(JSONUtils.getJsonObject(modelContents, "fluid"));
      List<ModelItem> items = ModelItem.listFromJson(modelContents, "items");
      return new MelterModel(model, fluid, items);
    }
  }
}
