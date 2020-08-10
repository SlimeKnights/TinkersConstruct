package slimeknights.tconstruct.library.client.model.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.vector.Vector3f;
import slimeknights.tconstruct.library.client.model.ModelUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FluidCuboid {
  protected static final Map<Direction, FluidFace> DEFAULT_FACES;
  static {
    DEFAULT_FACES = new EnumMap<>(Direction.class);
    for (Direction direction : Direction.values()) {
      DEFAULT_FACES.put(direction, FluidFace.NORMAL);
    }
  }

  /** Fluid start, scaled for block models */
  @Getter
  private final Vector3f from;
  /** Fluid end, scaled for block models */
  @Getter
  private final Vector3f to;
  /** Block faces for the fluid */
  @Getter(value = AccessLevel.PROTECTED)
  private Map<Direction, FluidFace> faces;

  /** Cache for scaled from */
  @Nullable
  private Vector3f fromScaled;
  /** Cache for scaled to */
  @Nullable
  private Vector3f toScaled;

  public FluidCuboid(Vector3f from, Vector3f to, Map<Direction,FluidFace> faces) {
    this.from = from;
    this.to = to;
    this.faces = faces;
  }

  /**
   * Checks if the fluid has the given face
   * @param face  Face to check
   * @return  True if the face is present
   */
  @Nullable
  public FluidFace getFace(Direction face) {
    return faces.get(face);
  }

  /**
   * Gets fluid from, scaled for renderer
   * @return Scaled from
   */
  public Vector3f getFromScaled() {
    if (fromScaled == null) {
      fromScaled = from.copy();
      fromScaled.mul(1 / 16f);
    }
    return fromScaled;
  }

  /**
   * Gets fluid to, scaled for renderer
   * @return Scaled from
   */
  public Vector3f getToScaled() {
    if (toScaled == null) {
      toScaled = to.copy();
      toScaled.mul(1 / 16f);
    }
    return toScaled;
  }

  /**
   * Creates a new fluid cuboid from JSON
   * @param json  JSON object
   * @return  Fluid cuboid
   */
  public static FluidCuboid fromJson(JsonObject json) {
    Vector3f from = ModelUtils.arrayToVector(json, "from");
    Vector3f to = ModelUtils.arrayToVector(json, "to");
    // if faces is defined, fill with specified faces
    Map<Direction,FluidFace> faces = getFaces(json);
    return new FluidCuboid(from, to, faces);
  }

  /**
   * Gets a list of fluid cuboids from the given parent
   * @param parent  Parent JSON
   * @param key     List key
   * @return  List of cuboids
   */
  public static List<FluidCuboid> listFromJson(JsonObject parent, String key) {
    JsonElement json = parent.get(key);

    // object: one cube
    if (json.isJsonObject()) {
      return Collections.singletonList(fromJson(json.getAsJsonObject()));
    }

    // array: multiple cubes
    if (json.isJsonArray()) {
      return ModelUtils.parseList(json.getAsJsonArray(), FluidCuboid::fromJson, key);
    }

    throw new JsonSyntaxException("Invalid fluid '" + key + "', must be an array or an object");
  }

  /**
   * Gets a face set from the given json element
   * @param json  JSON parent
   * @return  Set of faces
   */
  protected static Map<Direction, FluidFace> getFaces(JsonObject json) {
    if (!json.has("faces")) {
      return DEFAULT_FACES;
    }

    Map<Direction, FluidFace> faces = new EnumMap<>(Direction.class);
    JsonObject object = JSONUtils.getJsonObject(json, "faces");
    for (Entry<String, JsonElement> entry : object.entrySet()) {
      // if the direction is a face, add it
      String name = entry.getKey();
      Direction dir = Direction.byName(name);
      if (dir != null) {
        JsonObject face = JSONUtils.getJsonObject(entry.getValue(), name);
        boolean flowing = JSONUtils.getBoolean(face, "flowing", false);
        int rotation = ModelUtils.getRotation(face, "rotation");
        faces.put(dir, new FluidFace(flowing, rotation));
      } else {
        throw new JsonSyntaxException("Unknown face '" + name + "'");
      }
    }
    return faces;
  }

  @AllArgsConstructor
  @Getter
  public static class FluidFace {
    public static final FluidFace NORMAL = new FluidFace(false, 0);
    private final boolean isFlowing;
    private final int rotation;
  }
}
