package slimeknights.tconstruct.library.data;

import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.IdAwareObject;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableCrossbowItem;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableLauncherItem;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

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
  protected void tool(IdAwareObject tool, @Nullable JsonObject blocking, String... brokenParts) throws IOException {
    ResourceLocation id = tool.getId();
    String name = id.getPath();
    if (blocking != null) {
      withDisplay("tool/" + name + "/blocking", id, blocking);
    }
    transformTool("tool/" + name + "/broken", readJson(id), "", false, "broken", brokenParts);
  }

  /** Logic for creating models for the given ammo type */
  @NonExtendable
  protected interface AmmoHandler {
    default void apply(AbstractToolItemModelProvider self, String name, JsonObject base, JsonObject properties, int pullingCount, String[] pullingParts) {
      for (int i = 1; i <= pullingCount; i++) {
        String pulling = "tool/" + name + "/pulling_" + i;
        self.transformTool(pulling, base, "", false, Integer.toString(i), pullingParts);
        self.withDisplay("tool/" + name + "/blocking_" + i, self.resource(pulling), properties);
      }
    }
  }

  /** Default staticly display ammo for bows or crossbows */
  public enum AmmoType implements AmmoHandler {
    CROSSBOW {
      @Override
      public void apply(AbstractToolItemModelProvider self, String name, JsonObject base, JsonObject properties, int pullingCount, String[] pullingParts) {
        // crossbows have two ammo states
        String arrowName = "tool/" + name + "/arrow";
        String fireworkName = "tool/" + name + "/firework";
        JsonObject ammoBase = suffixTextures(base.deepCopy(), "3", pullingParts);
        self.models.put(arrowName, self.addPart(ammoBase.deepCopy(), "ammo", name, "arrow"));
        self.models.put(fireworkName, self.addPart(ammoBase.deepCopy(), "ammo", name, "firework"));
        self.withDisplay("tool/" + name + "/arrow_blocking", self.resource(arrowName), properties);
        self.withDisplay("tool/" + name + "/firework_blocking", self.resource(fireworkName), properties);
        // apply default blocking and pulling
        super.apply(self, name, base, properties, pullingCount, pullingParts);
      }
    },
    BOW {
      @Override
      public void apply(AbstractToolItemModelProvider self, String name, JsonObject base, JsonObject properties, int pullingCount, String[] pullingParts) {
        // create charging tools before adding in the arrow
        String[] pullingWithArrow = Streams.concat(Stream.of("arrow"), Arrays.stream(pullingParts)).toArray(String[]::new);
        // bows have an arrow part that pulls back
        JsonObject withArrow = self.addPart(base.deepCopy(), "arrow", name, "arrow");
        for (int i = 1; i <= pullingCount; i++) {
          String pulling = "tool/" + name + "/pulling_arrow_" + i;
          self.transformTool(pulling, withArrow, "", false, Integer.toString(i), pullingWithArrow);
          self.withDisplay("tool/" + name + "/blocking_arrow_" + i, self.resource(pulling), properties);
        }
        // apply default blocking and pulling
        super.apply(self, name, base, properties, pullingCount, pullingParts);
      }
    },
    NONE;
  }

  /** Serializes the vector to an JSON array */
  private static JsonElement serializeVec2(Vec2 vec2) {
    JsonArray array = new JsonArray();
    array.add(vec2.x);
    array.add(vec2.y);
    return array;
  }

  /** Crossbow displaying ammo as an item stack instead of statically */
  public record CrossbowAmmo(Vec2 ammoOffset, boolean flipAmmo, boolean leftAmmo) implements AmmoHandler {
    @Override
    public void apply(AbstractToolItemModelProvider self, String name, JsonObject base, JsonObject properties, int pullingCount, String[] pullingParts) {
      // crossbows have two ammo states
      String fireworkName = "tool/" + name + "/firework";
      JsonObject ammoBase = suffixTextures(base.deepCopy(), "3", pullingParts);
      self.models.put(fireworkName, self.addPart(ammoBase.deepCopy(), "ammo", name, "firework"));
      self.withDisplay("tool/" + name + "/firework_blocking", self.resource(fireworkName), properties);

      // apply default blocking and pulling
      for (int i = 1; i < pullingCount; i++) {
        String pulling = "tool/" + name + "/pulling_" + i;
        self.transformTool(pulling, base, "", false, Integer.toString(i), pullingParts);
        self.withDisplay("tool/" + name + "/blocking_" + i, self.resource(pulling), properties);
      }
      String pulling = "tool/" + name + "/pulling_" + pullingCount;
      JsonObject arrow = self.transformTool(pulling, base, "", false, Integer.toString(pullingCount), pullingParts);
      self.withDisplay("tool/" + name + "/blocking_" + pullingCount, self.resource(pulling), properties);
      // add the arrow to pulling 3, ToolModel handles not showing it when it has no ammo
      {
        JsonObject ammo = new JsonObject();
        ammo.addProperty("key", ModifiableCrossbowItem.KEY_CROSSBOW_AMMO.toString());
        ammo.addProperty("flip", flipAmmo);
        ammo.addProperty("left", leftAmmo);
        ammo.add("offset", serializeVec2(ammoOffset));
        arrow.add("ammo", ammo);
      }
    }
  }

  /** Longbow displaying ammo on each pulling and blocking variant */
  public record LongbowAmmo(Vec2[] smallOffsets, Vec2[] largeOffsets, boolean flipAmmo, boolean leftAmmo) implements AmmoHandler {
    @Override
    public void apply(AbstractToolItemModelProvider self, String name, JsonObject base, JsonObject properties, int pullingCount, String[] pullingParts) {
      // bows have an arrow part that pulls back
      for (int i = 1; i <= pullingCount; i++) {
        String pulling = "tool/" + name + "/pulling_" + i;
        JsonObject arrow = self.transformTool(pulling, base, "", false, Integer.toString(i), pullingParts);
        {
          JsonObject ammo = new JsonObject();
          ammo.addProperty("key", ModifiableLauncherItem.KEY_DRAWBACK_AMMO.toString());
          ammo.addProperty("flip", flipAmmo);
          ammo.addProperty("left", leftAmmo);
          ammo.add("small_offset", serializeVec2(smallOffsets[i-1]));
          ammo.add("large_offset", serializeVec2(largeOffsets[i-1]));
          arrow.add("ammo", ammo);
        }
        self.withDisplay("tool/" + name + "/blocking_" + i, self.resource(pulling), properties);
      }
    }
  }

  /** Creates a model in the blocking folder with the given copied display */
  protected void bow(IdAwareObject bow, JsonObject properties, boolean crossbow, String... pullingParts) throws IOException {
    bow(bow, properties, crossbow ? AmmoType.CROSSBOW : AmmoType.BOW, pullingParts);
  }

  /** Creates a model in the blocking folder with the given copied display */
  protected void bow(IdAwareObject bow, JsonObject properties, AmmoHandler ammo, String... pullingParts) throws IOException {
    pulling(bow, properties, ammo, "bowstring", 3, pullingParts);
  }

  /** Creates a model in the blocking folder with the given copied display */
  protected void pulling(IdAwareObject bow, JsonObject blocking, AmmoHandler ammo, String brokenPart, int pullingCount, String... pullingParts) throws IOException {
    ResourceLocation id = bow.getId();
    String name = id.getPath();
    JsonObject base = readJson(id);
    base.remove("overrides"); // don't need them anywhere, notably ditching for the sake of ammo models
    transformTool("tool/" + name + "/broken", base, "", false, "broken", brokenPart);
    withDisplay("tool/" + name + "/blocking", id, blocking);

    // apply ammo specific code
    ammo.apply(this, name, base, blocking, pullingCount, pullingParts);
  }

  /** Creates models for blocking, broken and fully charged for the given tool */
  protected void charged(IdAwareObject bow, JsonObject properties, String... brokenParts) throws IOException {
    ResourceLocation id = bow.getId();
    String name = id.getPath();
    JsonObject base = readJson(id);
    base.remove("overrides");
    withDisplay("tool/" + name + "/blocking", id, properties);
    transformTool("tool/" + name + "/broken", base, "", false, "broken", brokenParts);

    addPart(base, "overlay", name, "overlay");

    String charged = "tool/" + name + "/charged";
    transformTool(charged, base, "", false, "charged", "overlay");
    withDisplay("tool/" + name + "/blocking_charged", resource(charged), properties);
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
  protected void armor(String name, EnumObject<ArmorItem.Type,? extends Item> armor, ArmorItem.Type[] types, String... textures) throws IOException {
    for (ArmorItem.Type slot : types) {
      transformTool("armor/" + name + '/' + slot.getName() + "_broken", readJson(Loadables.ITEM.getKey(armor.get(slot))), "", false, "broken", textures);
    }
  }

  /** Adds broken and blocking models for the armor set */
  protected void armor(String name, EnumObject<ArmorItem.Type,? extends Item> armor, String... textures) throws IOException {
    armor(name, armor, ArmorItem.Type.values(), textures);
  }

  /** Creates models for fishing rods cast and broken */
  @SuppressWarnings("SameParameterValue") // API
  protected void fishingRod(IdAwareObject tool, @Nullable JsonObject blocking, String[] castParts, String[] brokenParts) throws IOException {
    ResourceLocation id = tool.getId();
    String name = id.getPath();
    JsonObject base = readJson(id);
    String cast = "tool/" + name + "/cast";
    transformTool(cast,   base, "", false, "cast", castParts);
    transformTool("tool/" + name + "/broken", base, "", false, "broken", brokenParts);
    if (blocking != null) {
      withDisplay("tool/" + name + "/blocking", id, blocking);
      withDisplay("tool/" + name + "/blocking_cast", resource(cast), blocking);
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
    model.addProperty("parent", parent.withPrefix("item/").toString());
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
  protected JsonObject transformTool(String destination, JsonObject tool, String parent, boolean allRoots, String suffix, String... updateTextures) {
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
    return transformed;
  }
}
