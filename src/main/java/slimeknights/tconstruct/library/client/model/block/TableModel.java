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
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import slimeknights.mantle.client.model.RetexturedModel;
import slimeknights.mantle.client.model.inventory.ModelItem;
import slimeknights.mantle.client.model.util.SimpleBlockModel;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@AllArgsConstructor
public class TableModel implements IUnbakedGeometry<TableModel> {
  /** Shared loader instance */
  public static final IGeometryLoader<TableModel> LOADER = TableModel::deserialize;

  private final SimpleBlockModel model;
  private final Set<String> retextured;
  private final List<ModelItem> items;

  @Override
  public Collection<Material> getMaterials(IGeometryBakingContext owner, Function<ResourceLocation,UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
    return this.model.getMaterials(owner, modelGetter, missingTextureErrors);
  }

  @Override
  public BakedModel bake(IGeometryBakingContext owner, ModelBakery bakery, Function<Material,TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation location) {
    BakedModel baked = this.model.bake(owner, bakery, spriteGetter, transform, overrides, location);
    return new Baked(baked, owner, this.model, transform, RetexturedModel.getAllRetextured(owner, model, retextured), items);
  }

  /** Baked model instance */
  public static class Baked extends RetexturedModel.Baked {
    @Getter
    private final List<ModelItem> items;
    protected Baked(BakedModel baked, IGeometryBakingContext owner, SimpleBlockModel model, ModelState transform, Set<String> retextured, List<ModelItem> items) {
      super(baked, owner, model, transform, retextured);
      this.items = items;
    }
  }

  /** Model deserializer */
  public static TableModel deserialize(JsonObject json, JsonDeserializationContext context) {
    SimpleBlockModel model = SimpleBlockModel.deserialize(json, context);
    Set<String> retextured = RetexturedModel.getRetexturedNames(json);
    List<ModelItem> items = ModelItem.listFromJson(json, "items");
    return new TableModel(model, retextured, items);
  }
}
