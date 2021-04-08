package slimeknights.tconstruct.library.client.model.block;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
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
    super(model, gui, fluid);
    this.items = items;
  }

  @Override
  public net.minecraft.client.render.model.BakedModel bake(IModelConfiguration owner, ModelLoader bakery, Function<SpriteIdentifier,Sprite> spriteGetter, ModelBakeSettings transform, ModelOverrideList overrides, Identifier location) {
    net.minecraft.client.render.model.BakedModel baked = model.bakeModel(owner, transform, overrides, spriteGetter, location);
    // bake the GUI model if present
    net.minecraft.client.render.model.BakedModel bakedGui = baked;
    if (gui != null) {
      bakedGui = gui.bakeModel(owner, transform, overrides, spriteGetter, location);
    }
    return new BakedModel(owner, transform, baked, bakedGui, this);
  }

  /** Baked variant to allow access to items */
  public static final class BakedModel extends TankModel.BakedModel<MelterModel> {
    @SuppressWarnings("WeakerAccess")
    protected BakedModel(IModelConfiguration owner, ModelBakeSettings transforms, net.minecraft.client.render.model.BakedModel baked, net.minecraft.client.render.model.BakedModel gui, MelterModel original) {
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
    public void apply(ResourceManager resourceManager) {}

    @Override
    public TankModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
      SimpleBlockModel model = SimpleBlockModel.deserialize(deserializationContext, modelContents);
      SimpleBlockModel gui = null;
      if (modelContents.has("gui")) {
        gui = SimpleBlockModel.deserialize(deserializationContext, JsonHelper.getObject(modelContents, "gui"));
      }
      IncrementalFluidCuboid fluid = IncrementalFluidCuboid.fromJson(JsonHelper.getObject(modelContents, "fluid"));
      List<ModelItem> items = ModelItem.listFromJson(modelContents, "items");
      return new MelterModel(model, gui, fluid, items);
    }
  }
}
