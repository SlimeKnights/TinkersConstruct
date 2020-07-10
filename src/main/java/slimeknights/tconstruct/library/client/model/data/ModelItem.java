package slimeknights.tconstruct.library.client.model.data;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.JSONUtils;
import slimeknights.tconstruct.library.client.model.ModelUtils;

import java.util.List;

public class ModelItem {
  /** Model item for rendering no item */
  private static final ModelItem EMPTY = new ModelItem(new Vector3f(0,0,0), 0, 0, 0);

  /** Item center location in pixels */
  @Getter
  private final Vector3f center;
  /** Item size in pixels. If 0, item is skipped */
  @Getter
  private final float size;
  /** X axis rotation, applied first */
  @Getter
  private final float x;
  /** Y axis rotation, applied second */
  @Getter
  private final float y;

  /** Item center location in percentages, lazy loaded */
  private Vector3f centerScaled;
  /** Item size in percentages, lazy loaded */
  private Float sizeScaled;

  public ModelItem(Vector3f center, float size, float x, float y) {
    this.center = center;
    this.size = size;
    this.x = x;
    this.y = y;
  }

  /**
   * Gets the center for rendering this item, scaled for renderer
   * @return Scaled center
   */
  public Vector3f getCenterScaled() {
    if (centerScaled == null) {
      centerScaled = center.copy();
      centerScaled.mul(1f / 16f);
    }
    return centerScaled;
  }

  /**
   * Gets the size to render this item, scaled for the renderer
   * @return Size scaled
   */
  public float getSizeScaled() {
    if (sizeScaled == null) {
      sizeScaled = size / 16f;
    }
    return sizeScaled;
  }

  /**
   * Returns true if this model item is empty, meaning no items should be rendered
   * @return  True if empty
   */
  public boolean isEmpty() {
    return size == 0;
  }

  /**
   * Gets a model item from a JSON object
   * @param json  JSON object instance
   * @return  Model item object
   */
  public static ModelItem fromJson(JsonObject json) {
    // if the size is 0, skip rendering this item
    float size = JSONUtils.getFloat(json, "size");
    if (size == 0) {
      return ModelItem.EMPTY;
    }
    Vector3f center = ModelUtils.arrayToVector(json, "center");
    float x = ModelUtils.getRotation(json, "x");
    float y = ModelUtils.getRotation(json, "y");
    return new ModelItem(center, size, x, y);
  }

  /**
   * Gets a list of model items from JSON
   * @param parent  Parent JSON object
   * @param key     Name of the array of model item objects
   * @return  List of model items
   */
  public static List<ModelItem> listFromJson(JsonObject parent, String key) {
    return ModelUtils.parseList(JSONUtils.getJsonArray(parent, key), ModelItem::fromJson, key);
  }
}
