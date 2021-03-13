package slimeknights.tconstruct.tables.client.inventory.library.slots;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.JSONUtils;
import slimeknights.mantle.util.JsonHelper;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Getter
public class SlotPosition {
  public static final SlotPosition EMPTY = new SlotPosition(0, 0);

  private final int x;
  private final int y;

  /**
   * Creates a list of SlotPositions from the given json
   *
   * @param parent the parent json
   * @param key the key to read from
   * @return a list of SlotPositions
   */
  @SuppressWarnings("Convert2MethodRef")
  public static List<SlotPosition> listFromJson(JsonObject parent, String key) {
    JsonElement json = parent.get(key);

    // object: one cube
    if (json.isJsonObject()) {
      return Collections.singletonList(fromJson(json.getAsJsonObject()));
    }

    // array: multiple cubes
    if (json.isJsonArray()) {
      return JsonHelper.parseList(json.getAsJsonArray(), key, (j) -> fromJson(j));
    }

    throw new JsonSyntaxException("Invalid slot '" + key + "', must be an array or an object");
  }

  /**
   * Creates a SlotPosition from the given JSON data
   *
   * @param json the json data
   * @param name the name that contains the actual positions
   * @return a slot position
   */
  public static SlotPosition fromJson(JsonObject json, String name) {
    JsonArray array = JSONUtils.getJsonArray(json, name);

    if (array.size() != 2) {
      throw new JsonParseException("Expected 2 " + name + " values, found: " + array.size());
    }

    int[] vec = new int[2];
    for (int i = 0; i < vec.length; ++i) {
      vec[i] = JSONUtils.getInt(array.get(i), name + "[" + i + "]");
    }

    return new SlotPosition(vec[0], vec[1]);
  }

  /**
   * creates a slot position from the given json object
   *
   * @param json the json data
   * @return a slot position
   */
  public static SlotPosition fromJson(JsonObject json) {
    return fromJson(json, "position");
  }

  /**
   * Checks to see if the given slot position should be hidden or not
   * used mostly for the main tinkerer slot
   *
   * @return if the slot is hidden or not
   */
  public boolean isHidden() {
    return this.x == -1 && this.y == -1;
  }
}
