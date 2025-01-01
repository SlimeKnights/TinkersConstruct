package slimeknights.tconstruct.library.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.IdAwareObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static slimeknights.mantle.util.IdExtender.INSTANCE;

/** Helper for generating tool item models */
public abstract class AbstractToolItemModelProvider extends GenericDataProvider {
  protected final Map<String,JsonObject> models = new HashMap<>();
  protected final ExistingFileHelper existingFileHelper;
  protected final String modId;
  public AbstractToolItemModelProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper, String modId) {
    super(packOutput, Target.RESOURCE_PACK, "models/item");
    this.existingFileHelper = existingFileHelper;
    this.modId = modId;
  }

  /** Add all relevant models */
  protected abstract void addModels() throws IOException;

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    try {
      addModels();
    } catch (IOException e) {
      return CompletableFuture.failedFuture(e);
    }
    // no key comparator - I want them sorted in the same order as the input models for easier readability
    return allOf(models.entrySet().stream().map((entry) -> saveJson(cache, new ResourceLocation(modId, entry.getKey()), entry.getValue(), null)));
  }



  /* Model types */

  /** Creates models for blocking and broken for the given tool */
  protected void tool(IdAwareObject tool, JsonObject properties, String... brokenParts) throws IOException {
    ResourceLocation id = tool.getId();
    String name = id.getPath();
    withDisplay("tool/" + name + "/blocking", id, properties);
    transformTool("tool/" + name + "/broken", readJson(id), "", false, "broken", brokenParts);
  }

  /** Creates a model in the blocking folder with the given copied display */
  protected void bow(IdAwareObject bow, JsonObject properties, boolean crossbow, String... pullingParts) throws IOException {
    ResourceLocation id = bow.getId();
    String name = id.getPath();
    JsonObject base = readJson(id);
    base.remove("overrides"); // don't need them anywhere, notably ditching for the sake of ammo models
    transformTool("tool/" + name + "/broken", base, "", false, "broken", "bowstring");
    withDisplay("tool/" + name + "/blocking", id, properties);
    if (crossbow) {
      // crossbows have two ammo states
      String arrowName = "tool/" + name + "/arrow";
      String fireworkName = "tool/" + name + "/firework";
      JsonObject ammoBase = suffixTextures(base.deepCopy(), "3", pullingParts);
      models.put(arrowName, addPart(ammoBase.deepCopy(), "ammo", name, "arrow"));
      models.put(fireworkName, addPart(ammoBase.deepCopy(), "ammo", name, "firework"));
      withDisplay("tool/" + name + "/arrow_blocking", resource(arrowName), properties);
      withDisplay("tool/" + name + "/firework_blocking", resource(fireworkName), properties);
    } else {
      // bows have an arrow part that pulls back
      addPart(base, "arrow", name, "arrow");
    }
    for (int i = 1; i <= 3; i++) {
      String pulling = "tool/" + name + "/pulling_" + i;
      transformTool(pulling, base, "", false, Integer.toString(i), pullingParts);
      withDisplay("tool/" + name + "/blocking_" + i, resource(pulling), properties);
    }
  }

  /** Creates a model in the blocking folder with the given copied display */
  protected void staff(IdAwareObject staff, JsonObject properties) throws IOException {
    ResourceLocation id = staff.getId();
    String path = id.getPath();
    String name = path.substring(0, path.length() - "_staff".length());
    JsonObject base = readJson(id);
    withDisplay("tool/staff/" + name + "/blocking", id, properties);
    transformTool("tool/staff/" + name + "/broken", base, "", true, "broken", "tool");
    for (int i = 1; i <= 5; i++) {
      String charging = "tool/staff/" + name + "/charging_" + i;
      transformTool(charging, base, "tconstruct:item/base/staff_charging", false, Integer.toString(i), "tool");
      withDisplay("tool/staff/" + name + "/blocking_" + i, resource(charging), properties);
    }
  }

  /** Adds broken and blocking models for the shield */
  protected void shield(String setName, IdAwareObject shield, JsonObject properties, String... parts) throws IOException {
    ResourceLocation id = shield.getId();
    withDisplay("armor/" + setName + "/shield_blocking", id, Objects.requireNonNull(properties));
    transformTool("armor/" + setName + "/shield_broken", readJson(id), "", false, "broken", parts);
  }

  /** Adds broken and blocking models for the armor set */
  @SuppressWarnings("deprecation")  // no its not
  protected void armor(String name, EnumObject<ArmorItem.Type,? extends Item> armor, String... textures) throws IOException {
    for (ArmorItem.Type slot : ArmorItem.Type.values()) {
      transformTool("armor/" + name + '/' + slot.getName() + "_broken", readJson(BuiltInRegistries.ITEM.getKey(armor.get(slot))), "", false, "broken", textures);
    }
  }

  /* Helpers */

  /** Reads a JSON file */
  protected JsonObject readJson(ResourceLocation path) throws IOException {
    try (BufferedReader reader = existingFileHelper.getResource(path, PackType.CLIENT_RESOURCES, ".json", "models/item").openAsReader()) {
      return GsonHelper.parse(reader);
    }
  }

  /** Creates a resource location under this mod */
  protected ResourceLocation resource(String name) {
    return new ResourceLocation(modId, name);
  }

  /** Creates a model with display from the given target */
  protected void withDisplay(String destination, ResourceLocation parent, JsonObject properties) {
    JsonObject model = new JsonObject();
    model.addProperty("parent", INSTANCE.prefix(parent, "item/").toString());
    model.add("display", properties.get("display"));
    models.put(destination, model);
  }

  /** Adds a new root to the array with the given suffix */
  protected static JsonArray copyAndSuffixRoot(JsonArray array, String suffix, boolean allRoots) {
    JsonArray newArray = new JsonArray();
    boolean first = true;
    for (JsonElement element : array) {
      if (allRoots || first) {
        newArray.add(element.getAsString() + suffix);
      }
      newArray.add(element);
      first = false;
    }
    return newArray;
  }

  /** Adds a part to the given tool */
  protected JsonObject addPart(JsonObject tool, String part, String toolName, String texture) {
    JsonObject textures = tool.getAsJsonObject("textures");
    // add the texture
    boolean large = GsonHelper.getAsBoolean(tool, "large", false);
    textures.addProperty(part, resource("item/tool/" + toolName + "/" + texture).toString());
    if (large) {
      textures.addProperty("large_" + part, resource("item/tool/" + toolName + "/large/" + texture).toString());
    }
    JsonObject partObject = new JsonObject();
    partObject.addProperty("name", part);
    tool.getAsJsonArray("parts").add(partObject);
    return tool;
  }

  /** Suffixes the passed textures with the given suffix */
  protected static JsonObject suffixTextures(JsonObject tool, String suffix, String... updateTextures) {
    // update parts that we were told to update
    boolean large = GsonHelper.getAsBoolean(tool, "large", false);
    JsonObject textures = tool.getAsJsonObject("textures");
    for (String part : updateTextures) {
      textures.addProperty(part, GsonHelper.getAsString(textures, part) + '_' + suffix);
      if (large) {
        textures.addProperty("large_" + part, GsonHelper.getAsString(textures, "large_" + part) + '_' + suffix);
      }
    }
    return tool;
  }

  /** Transforms the given tool by adding suffixes to listed textures and the modifier roots */
  protected void transformTool(String destination, JsonObject tool, String parent, boolean allRoots, String suffix, String... updateTextures) {
    JsonObject transformed = tool.deepCopy();
    // set parent if given
    if (!parent.isEmpty()) {
      transformed.addProperty("parent", parent);
    }
    // update parts that we were told to update
    suffixTextures(transformed, suffix, updateTextures);
    // add modifier roots
    if (GsonHelper.getAsBoolean(transformed, "large", false)) {
      JsonObject roots = transformed.getAsJsonObject("modifier_roots");
      roots.add("small", copyAndSuffixRoot(roots.getAsJsonArray("small"), suffix + '/', allRoots));
      roots.add("large", copyAndSuffixRoot(roots.getAsJsonArray("large"), suffix + '/', allRoots));
    } else {
      transformed.add("modifier_roots", copyAndSuffixRoot(transformed.getAsJsonArray("modifier_roots"), suffix + '/', allRoots));
    }
    // delete overrides, no need to nest them
    transformed.remove("overrides");
    models.put(destination, transformed);
  }
}
