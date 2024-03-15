package slimeknights.tconstruct.library.client.model.block;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import lombok.Getter;
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
import slimeknights.mantle.client.model.fluid.FluidCuboid;
import slimeknights.mantle.client.model.inventory.InventoryModel;
import slimeknights.mantle.client.model.inventory.ModelItem;
import slimeknights.mantle.client.model.util.ColoredBlockModel;
import slimeknights.mantle.client.model.util.SimpleBlockModel;

import java.util.List;
import java.util.function.Function;

/**
 * This model contains a single fluid region that is scaled in the TESR, and a list of two items displayed in the TESR
 */
public class CastingModel extends InventoryModel {
  /** Shared loader instance */
  public static final IGeometryLoader<InventoryModel> LOADER = CastingModel::deserialize;

  private final FluidCuboid fluid;

  @SuppressWarnings("WeakerAccess")
  protected CastingModel(SimpleBlockModel model, List<ModelItem> items, FluidCuboid fluid) {
    super(model, items);
    this.fluid = fluid;
  }

  @Override
  public BakedModel bake(IGeometryBakingContext owner, ModelBakery bakery, Function<Material,TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation location) {
    BakedModel baked = model.bake(owner, bakery, spriteGetter, transform, overrides, location);
    return new Baked(baked, items, fluid);
  }

  /** Baked model, mostly a data wrapper around a normal model */
  public static class Baked extends InventoryModel.Baked {
    @Getter
    private final FluidCuboid fluid;
    private Baked(BakedModel originalModel, List<ModelItem> items, FluidCuboid fluid) {
      super(originalModel, items);
      this.fluid = fluid;
    }
  }

  /** Deserializes this model from JSON */
  public static InventoryModel deserialize(JsonObject json, JsonDeserializationContext context) {
    SimpleBlockModel model = ColoredBlockModel.deserialize(json, context);
    List<ModelItem> items = ModelItem.listFromJson(json, "items");
    FluidCuboid fluid = FluidCuboid.fromJson(GsonHelper.getAsJsonObject(json, "fluid"));
    return new CastingModel(model, items, fluid);
  }
}
