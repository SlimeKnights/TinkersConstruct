package slimeknights.tconstruct.library.client.model.block;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import slimeknights.mantle.client.model.RetexturedModel;
import slimeknights.mantle.client.model.inventory.ModelItem;
import slimeknights.mantle.client.model.util.SimpleBlockModel;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@AllArgsConstructor
public class TableModel implements IModelGeometry<TableModel> {
  /** Shared loader instance */
  public static final Loader LOADER = new Loader();

  private final SimpleBlockModel model;
  private final Set<String> retextured;
  private final List<ModelItem> items;

  @Override
  public Collection<SpriteIdentifier> getTextures(IModelConfiguration owner, Function<Identifier,UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
    return this.model.getTextures(owner, modelGetter, missingTextureErrors);
  }

  @Override
  public net.minecraft.client.render.model.BakedModel bake(IModelConfiguration owner, ModelLoader bakery, Function<SpriteIdentifier,Sprite> spriteGetter, ModelBakeSettings transform, ModelOverrideList overrides, Identifier location) {
    net.minecraft.client.render.model.BakedModel baked = this.model.bakeModel(owner, transform, overrides, spriteGetter, location);
    return new BakedModel(baked, owner, this.model, transform, RetexturedModel.getAllRetextured(owner, model, retextured), items);
  }

  /** Baked model instance */
  public static class BakedModel extends RetexturedModel.BakedModel {
    @Getter
    private final List<Item> items;
    protected BakedModel(net.minecraft.client.render.model.BakedModel baked, IModelConfiguration owner, SimpleBlockModel model, ModelBakeSettings transform, Set<String> retextured, List<ModelItem> items) {
      super(baked, owner, model, transform, retextured);
      this.items = items;
    }
  }

  /** Model loader class */
  public static class Loader implements IModelLoader<TableModel> {
    @Override
    public void apply(ResourceManager resourceManager) {}

    @Override
    public TableModel read(JsonDeserializationContext context, JsonObject json) {
      SimpleBlockModel model = SimpleBlockModel.deserialize(context, json);
      Set<String> retextured = RetexturedModel.Loader.getRetextured(json);
      List<ModelItem> items = ModelItem.listFromJson(json, "items");
      return new TableModel(model, retextured, items);
    }
  }
}
