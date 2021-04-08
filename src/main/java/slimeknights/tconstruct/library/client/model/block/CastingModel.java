package slimeknights.tconstruct.library.client.model.block;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
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
  public net.minecraft.client.render.model.BakedModel bake(IModelConfiguration owner, ModelLoader bakery, Function<SpriteIdentifier,Sprite> spriteGetter, ModelBakeSettings transform, ModelOverrideList overrides, Identifier location) {
    net.minecraft.client.render.model.BakedModel baked = model.bakeModel(owner, transform, overrides, spriteGetter, location);
    return new BakedModel(baked, items, fluid);
  }

  /** Baked model, mostly a data wrapper around a normal model */
  public static class BakedModel extends InventoryModel.BakedModel {
    @Getter
    private final FluidCuboid fluid;
    private BakedModel(net.minecraft.client.render.model.BakedModel originalModel, List<ModelItem> items, FluidCuboid fluid) {
      super(originalModel, items);
      this.fluid = fluid;
    }
  }

  /** Loader for this model */
  public static class Loader implements IModelLoader<InventoryModel> {
    @Override
    public void apply(ResourceManager resourceManager) {}

    @Override
    public InventoryModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
      SimpleBlockModel model = SimpleBlockModel.deserialize(deserializationContext, modelContents);
      List<ModelItem> items = ModelItem.listFromJson(modelContents, "items");
      FluidCuboid fluid = FluidCuboid.fromJson(JsonHelper.getObject(modelContents, "fluid"));
      return new CastingModel(model, items, fluid);
    }
  }
}
