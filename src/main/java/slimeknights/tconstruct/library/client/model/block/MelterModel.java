package slimeknights.tconstruct.library.client.model.block;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
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
import slimeknights.mantle.client.model.inventory.ModelItem;
import slimeknights.mantle.client.model.util.SimpleBlockModel;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

/**
 * This model contains a list of items to display in the TESR, plus a single scalable fluid that can either be statically rendered or rendered in the TESR
 */
public class MelterModel extends TankModel {
  /** Shared loader instance */
  public static final Loader LOADER = new Loader();

  private final List<ModelItem> items;
  @SuppressWarnings("WeakerAccess")
  protected MelterModel(SimpleBlockModel model, @Nullable SimpleBlockModel gui, IncrementalFluidCuboid fluid, List<ModelItem> items) {
    super(model, gui, fluid, false);
    this.items = items;
  }

  @Override
  public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial,TextureAtlasSprite> spriteGetter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation location) {
    IBakedModel baked = model.bakeModel(owner, transform, overrides, spriteGetter, location);
    // bake the GUI model if present
    IBakedModel bakedGui = baked;
    if (gui != null) {
      bakedGui = gui.bakeModel(owner, transform, overrides, spriteGetter, location);
    }
    return new BakedModel(owner, transform, baked, bakedGui, this);
  }

  /** Baked variant to allow access to items */
  public static final class BakedModel extends TankModel.BakedModel<MelterModel> {
    @SuppressWarnings("WeakerAccess")
    protected BakedModel(IModelConfiguration owner, IModelTransform transforms, IBakedModel baked, IBakedModel gui, MelterModel original) {
      super(owner, transforms, baked, gui, original);
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
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {}

    @Override
    public TankModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
      SimpleBlockModel model = SimpleBlockModel.deserialize(deserializationContext, modelContents);
      SimpleBlockModel gui = null;
      if (modelContents.has("gui")) {
        gui = SimpleBlockModel.deserialize(deserializationContext, JSONUtils.getJsonObject(modelContents, "gui"));
      }
      IncrementalFluidCuboid fluid = IncrementalFluidCuboid.fromJson(JSONUtils.getJsonObject(modelContents, "fluid"));
      List<ModelItem> items = ModelItem.listFromJson(modelContents, "items");
      return new MelterModel(model, gui, fluid, items);
    }
  }
}
