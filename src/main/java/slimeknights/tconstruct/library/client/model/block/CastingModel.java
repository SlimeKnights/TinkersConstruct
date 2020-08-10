package slimeknights.tconstruct.library.client.model.block;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import lombok.Getter;
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
import slimeknights.mantle.client.model.fluid.FluidCuboid;
import slimeknights.mantle.client.model.inventory.InventoryModel;
import slimeknights.mantle.client.model.inventory.ModelItem;
import slimeknights.mantle.client.model.util.SimpleBlockModel;

import java.util.List;
import java.util.function.Function;

/**
 * This model contains a single fluid region that is scaled in the TESR, and a list of two items displayed in the TESR
 */
public class CastingModel extends InventoryModel {
  /** Shared loader instance */
  public static final Loader LOADER = new Loader();

  private final FluidCuboid fluid;

  @SuppressWarnings("WeakerAccess")
  protected CastingModel(SimpleBlockModel model, List<ModelItem> items, FluidCuboid fluid) {
    super(model, items);
    this.fluid = fluid;
  }

  @Override
  public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial,TextureAtlasSprite> spriteGetter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation location) {
    IBakedModel baked = model.bakeModel(owner, transform, overrides, spriteGetter, location);
    return new BakedModel(baked, items, fluid);
  }

  /** Baked model, mostly a data wrapper around a normal model */
  public static class BakedModel extends InventoryModel.BakedModel {
    @Getter
    private final FluidCuboid fluid;
    private BakedModel(IBakedModel originalModel, List<ModelItem> items, FluidCuboid fluid) {
      super(originalModel, items);
      this.fluid = fluid;
    }
  }

  /** Loader for this model */
  public static class Loader implements IModelLoader<InventoryModel> {
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {}

    @Override
    public InventoryModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
      SimpleBlockModel model = SimpleBlockModel.deserialize(deserializationContext, modelContents);
      List<ModelItem> items = ModelItem.listFromJson(modelContents, "items");
      FluidCuboid fluid = FluidCuboid.fromJson(JSONUtils.getJsonObject(modelContents, "fluid"));
      return new CastingModel(model, items, fluid);
    }
  }
}
