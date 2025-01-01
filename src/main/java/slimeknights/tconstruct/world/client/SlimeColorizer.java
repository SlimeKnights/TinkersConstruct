package slimeknights.tconstruct.world.client;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import slimeknights.tconstruct.world.block.FoliageType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Data class to handle all slime colors
 */
public class SlimeColorizer {
  /** Map of slime foliage type to color */
  private static final Map<FoliageType,int[]> COLOR_MAP = Util.make(new EnumMap<>(FoliageType.class), map -> {
    for (FoliageType type : FoliageType.values()) {
      map.put(type, new int[65536]);
    }
  });

  public static final float LOOP = 256; // after how many blocks the pattern loops
  public static final BlockPos LOOP_OFFSET = BlockPos.containing(SlimeColorizer.LOOP / 2, 0, SlimeColorizer.LOOP / 2);

  /**
   * Updates the colors for the given type
   * @param type    Type to update
   * @param colors  New colors
   */
  public static void setGrassColor(FoliageType type, int[] colors) {
    COLOR_MAP.put(type, colors);
  }

  /**
   * Gets the color for the given position
   * @param type  Foliage type
   * @param x     X position
   * @param z     Z position
   * @return      Color
   */
  public static int getColor(FoliageType type, int x, int z) {
    return getColor(COLOR_MAP.get(type), x, z);
  }

  /**
   * Position dependant Slime foliage color
   */
  public static int getColorForPos(BlockPos pos, FoliageType type) {
    return getColor(type, pos.getX(), pos.getZ());
  }

  /**
   * Gets the color for the given position from the buffer
   * @param buffer  Color buffer
   * @param posX    X position
   * @param posZ    Z position
   * @return        Color
   */
  private static int getColor(int[] buffer, int posX, int posZ) {
    float x = Math.abs((LOOP - (Math.abs(posX) % (2 * LOOP))) / LOOP);
    float z = Math.abs((LOOP - (Math.abs(posZ) % (2 * LOOP))) / LOOP);

    if (x < z) {
      float tmp = x;
      x = z;
      z = tmp;
    }

    return buffer[(int) (x * 255f) << 8 | (int) (z * 255f)];
  }
}
