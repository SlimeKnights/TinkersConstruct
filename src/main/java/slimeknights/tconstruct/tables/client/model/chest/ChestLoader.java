package slimeknights.tconstruct.tables.client.model.chest;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.client.model.IModelLoader;

import java.util.List;

public class ChestLoader implements IModelLoader<ChestModel> {

  public static final ChestLoader INSTANCE = new ChestLoader();

  private ChestLoader() {
  }

  @Override
  public void onResourceManagerReload(IResourceManager resourceManager) {

  }

  @Override
  public ChestModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
    List<BlockPart> list = this.getModelElements(deserializationContext, modelContents);
    return new ChestModel(list);
  }

  private List<BlockPart> getModelElements(JsonDeserializationContext deserializationContext, JsonObject object) {
    List<BlockPart> list = Lists.newArrayList();
    if (object.has("elements")) {
      for (JsonElement jsonelement : JSONUtils.getJsonArray(object, "elements")) {
        list.add(deserializationContext.deserialize(jsonelement, BlockPart.class));
      }
    }

    return list;
  }
}
