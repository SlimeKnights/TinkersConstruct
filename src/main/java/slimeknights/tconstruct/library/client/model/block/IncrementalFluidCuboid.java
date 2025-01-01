package slimeknights.tconstruct.library.client.model.block;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import org.joml.Vector3f;
import slimeknights.mantle.client.render.FluidCuboid;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

/** Extension of {@link FluidCuboid} which supports being scaled by the fluid amount, used in tank models. */
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
  public BlockElement getPart(int amount, boolean gas) {
    // set cube height based on stack amount
    Vector3f from = getFrom();
    Vector3f to = getTo();
    // gas renders upside down
    float minY = from.y();
    float maxY = to.y();
    if (gas) {
      from = new Vector3f(from);
      from.y = maxY + (amount * (minY - maxY) / increments);
    } else {
      to = new Vector3f(to);
      to.y = minY + (amount * (maxY - minY) / increments);
    }

    // create faces based on face data
    Map<Direction,BlockElementFace> faces = new EnumMap<>(Direction.class);
    for (Entry<Direction, FluidFace> entry : this.getFaces().entrySet()) {
      // only add the face if requested
      Direction dir = entry.getKey();
      FluidFace face = entry.getValue();
      // calculate in flowing and rotations
      boolean isFlowing = face.isFlowing();
      faces.put(dir, new BlockElementFace(
        null, 0, isFlowing ? "flowing_fluid" : "fluid",
        getFaceUvs(from ,to, dir, face.rotation(), isFlowing ? 0.5f : 1f)));
    }

    // create the part with the fluid
    return new BlockElement(from, to, faces, null, false);
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
    switch (side) {
      case DOWN -> {
        u1 = from.x(); v1 = 16f - to.z();
        u2 = to.x();   v2 = 16f - from.z();
      }
      case UP -> {
        u1 = from.x(); v1 = from.z();
        u2 = to.x();   v2 = to.z();
      }
      default -> {
        u1 = 16f - to.x();   v1 = 16f - to.y();
        u2 = 16f - from.x(); v2 = 16f - from.y();
      }
      case SOUTH -> {
        u1 = from.x(); v1 = 16f - to.y();
        u2 = to.x();   v2 = 16f - from.y();
      }
      case WEST -> {
        u1 = from.z(); v1 = 16f - to.y();
        u2 = to.z();   v2 = 16f - from.y();
      }
      case EAST -> {
        u1 = 16f - to.z();   v1 = 16f - to.y();
        u2 = 16f - from.z(); v2 = 16f - from.y();
      }
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
    FluidCuboid base = FluidCuboid.LOADABLE.deserialize(json);
    int increments = GsonHelper.getAsInt(json, "increments");
    return new IncrementalFluidCuboid(base.getFrom(), base.getTo(), base.getFaces(), increments);
  }
}
