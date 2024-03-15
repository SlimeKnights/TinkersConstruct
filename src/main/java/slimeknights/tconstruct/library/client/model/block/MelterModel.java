package slimeknights.tconstruct.library.client.model.block;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
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
  public static final IGeometryLoader<TankModel> LOADER = MelterModel::deserialize;

  private final List<ModelItem> items;
  @SuppressWarnings("WeakerAccess")
  protected MelterModel(SimpleBlockModel model, @Nullable SimpleBlockModel gui, IncrementalFluidCuboid fluid, List<ModelItem> items) {
    super(model, gui, fluid, false);
    this.items = items;
  }

  @Override
  public BakedModel bake(IGeometryBakingContext owner, ModelBakery bakery, Function<Material,TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation location) {
    BakedModel baked = model.bake(owner, bakery, spriteGetter, transform, overrides, location);
    // bake the GUI model if present
    BakedModel bakedGui = baked;
    if (gui != null) {
      bakedGui = gui.bake(owner, bakery, spriteGetter, transform, overrides, location);
    }
    return new Baked(owner, transform, baked, bakedGui, this);
  }

  /** Baked variant to allow access to items */
  public static final class Baked extends TankModel.Baked<MelterModel> {
    private Baked(IGeometryBakingContext owner, ModelState transforms, BakedModel baked, BakedModel gui, MelterModel original) {
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
  public static TankModel deserialize(JsonObject json, JsonDeserializationContext context) {
    SimpleBlockModel model = SimpleBlockModel.deserialize(json, context);
    SimpleBlockModel gui = null;
    if (json.has("gui")) {
      gui = SimpleBlockModel.deserialize(GsonHelper.getAsJsonObject(json, "gui"), context);
    }
    IncrementalFluidCuboid fluid = IncrementalFluidCuboid.fromJson(GsonHelper.getAsJsonObject(json, "fluid"));
    List<ModelItem> items = ModelItem.listFromJson(json, "items");
    return new MelterModel(model, gui, fluid, items);
  }
}
