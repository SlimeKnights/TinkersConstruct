package slimeknights.tconstruct.library.client.model.block;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.vector.Vector3f;
import slimeknights.mantle.client.model.fluid.FluidCuboid;
import slimeknights.mantle.client.model.util.ModelHelper;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

@Getter
public class IncrementalFluidCuboid extends FluidCuboid {
  private final int increments;
  @SuppressWarnings("WeakerAccess")
  public IncrementalFluidCuboid(Vector3f from, Vector3f to, Map<Direction,FluidFace> faces, int increments) {
    super(from, to, faces);
    this.increments = increments;
  }

  /**
   * Gets the fluid part for this incremental cuboid
   * @param amount  Fluid amount
   * @param gas     If true, renders upside down
   * @return  Fluid part
   */
  @SuppressWarnings("WeakerAccess")
  public BlockPart getPart(int amount, boolean gas) {
    // set cube height based on stack amount
    Vector3f from = getFrom();
    Vector3f to = getTo();
    // gas renders upside down
    float minY = from.getY();
    float maxY = to.getY();
    if (gas) {
      from = from.copy();
      from.setY(maxY + (amount * (minY - maxY) / increments));
    } else {
      to = to.copy();
      to.setY(minY + (amount * (maxY - minY) / increments));
    }

    // create faces based on face data
    Map<Direction,BlockPartFace> faces = new EnumMap<>(Direction.class);
    for (Entry<Direction, FluidFace> entry : this.getFaces().entrySet()) {
      // only add the face if requested
      Direction dir = entry.getKey();
      FluidFace face = entry.getValue();
      // calculate in flowing and rotations
      boolean isFlowing = face.isFlowing();
      faces.put(dir, new BlockPartFace(
        null, 0, isFlowing ? "flowing_fluid" : "fluid",
        getFaceUvs(from ,to, dir, face.getRotation(), isFlowing ? 0.5f : 1f)));
    }

    // create the part with the fluid
    return new BlockPart(from, to, faces, null, false);
  }

  /**
   * Creates a block part UV based on the given block dimensions
   * @param from      Block start position
   * @param to        Block end position
   * @param side      Block side
   * @param rotation  Side rotation
   * @param scale     UV scale, set to 0.5 for flowing fluids
   * @return  BlockFaceUV instance
   */
  private static BlockFaceUV getFaceUvs(Vector3f from, Vector3f to, Direction side, int rotation, float scale) {
    // first, translate from and to into texture coords
    float u1, u2, v1, v2;
    switch(side) {
      case DOWN:
        u1 = from.getX(); v1 = 16f - to.getZ();
        u2 = to.getX(); v2 = 16f - from.getZ();
        break;
      case UP:
        u1 = from.getX(); v1 = from.getZ();
        u2 = to.getX(); v2 = to.getZ();
        break;
      case NORTH:
      default:
        u1 = 16f - to.getX(); v1 =  16f - to.getY();
        u2 = 16f - from.getX(); v2 = 16f - from.getY();
        break;
      case SOUTH:
        u1 = from.getX(); v1 =  16f - to.getY();
        u2 = to.getX(); v2 = 16f - from.getY();
        break;
      case WEST:
        u1 = from.getZ(); v1 =  16f - to.getY();
        u2 = to.getZ(); v2 = 16f - from.getY();
        break;
      case EAST:
        u1 = 16f - to.getZ(); v1 = 16f - to.getY();
        u2 = 16f - from.getZ(); v2 = 16f - from.getY();
        break;
    }
    // cycle coords so they line up with the relevant block part
    // 0:   u1, v1, u2, v2
    // 90:  v1, u2, v2, u1
    // 180: u2, v2, u1, v1
    // 270: v2, u1, v1, u2
    // swapping 1 and 2 requies 16-x for swap
    float[] uv;
    if (rotation >= 180) {
      float temp = v1;
      v1 = 16f - v2;
      v2 = 16f - temp;
    }
    // flip U at 90 or 180
    if (rotation == 90 || rotation == 180) {
      float temp = u1;
      u1 = 16f - u2;
      u2 = 16f - temp;
    }
    // rotations need swapped UV
    if ((rotation % 180) == 90) {
      uv = new float[] {v1 * scale, u1 * scale, v2 * scale, u2 * scale};
    } else {
      uv = new float[] {u1 * scale, v1 * scale, u2 * scale, v2 * scale};
    }
    return new BlockFaceUV(uv, rotation);
  }

  /**
   * Creates a new scalable fluid cuboid from JSON
   * @param json  Fluid JSON object
   * @return  Scalable fluid cuboid
   */
  public static IncrementalFluidCuboid fromJson(JsonObject json) {
    Vector3f from = ModelHelper.arrayToVector(json, "from");
    Vector3f to = ModelHelper.arrayToVector(json, "to");
    Map<Direction,FluidFace> faces = getFaces(json);
    int increments = JSONUtils.getInt(json, "increments");
    return new IncrementalFluidCuboid(from, to, faces, increments);
  }
}
