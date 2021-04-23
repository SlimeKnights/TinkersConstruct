package slimeknights.tconstruct.library.client.model;

import com.google.common.base.Charsets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Hydos' Based Model Api Because Fabric Is Bad
 */
public class HBMABFIB {

  public static JsonUnbakedModel getModelSafe(Identifier resourceId) {
    try {
      return JsonUnbakedModel.deserialize(getModelJson(resourceId));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  static BufferedReader getModelJson(Identifier location) throws IOException {
    Identifier file = new Identifier(location.getNamespace(), "models/" + location.getPath() + ".json");
    Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(file);
    return new BufferedReader(new InputStreamReader(resource.getInputStream(), Charsets.UTF_8));
  }
}
