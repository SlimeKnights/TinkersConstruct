package slimeknights.tconstruct.library.client.model.block;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
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
  public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation,UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
    return this.model.getTextures(owner, modelGetter, missingTextureErrors);
  }

  @Override
  public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material,TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation location) {
    BakedModel baked = this.model.bakeModel(owner, transform, overrides, spriteGetter, location);
    return new Baked(baked, owner, this.model, transform, RetexturedModel.getAllRetextured(owner, model, retextured), items);
  }

  /** Baked model instance */
  public static class Baked extends RetexturedModel.Baked {
    @Getter
    private final List<ModelItem> items;
    protected Baked(BakedModel baked, IModelConfiguration owner, SimpleBlockModel model, ModelState transform, Set<String> retextured, List<ModelItem> items) {
      super(baked, owner, model, transform, retextured);
      this.items = items;
    }
  }

  /** Model loader class */
  public static class Loader implements IModelLoader<TableModel> {
    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {}

    @Override
    public TableModel read(JsonDeserializationContext context, JsonObject json) {
      SimpleBlockModel model = SimpleBlockModel.deserialize(context, json);
      Set<String> retextured = RetexturedModel.Loader.getRetextured(json);
      List<ModelItem> items = ModelItem.listFromJson(json, "items");
      return new TableModel(model, retextured, items);
    }
  }
}
