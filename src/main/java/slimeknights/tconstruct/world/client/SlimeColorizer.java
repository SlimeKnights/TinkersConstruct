package slimeknights.tconstruct.world.client;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;

public class SlimeColorizer {

  public static int colorBlue = 0x2aec81;
  public static int colorPurple = 0xa92dff;
  public static int colorOrange = 0xd09800;

  private static final ResourceLocation LOC_SLIME_BLUE_PNG = Util.getResource("textures/colormap/slime_grass_color.png");
  private static final ResourceLocation LOC_SLIME_PURPLE_PNG = Util.getResource("textures/colormap/purple_grass_color.png");
  private static final ResourceLocation LOC_SLIME_ORANGE_PNG = Util.getResource("textures/colormap/orange_grass_color.png");

  private static int[] colorBufferBlue = new int[65536];
  private static int[] colorBufferPurple = new int[65536];
  private static int[] colorBufferOrange = new int[65536];

  public static final float loop = 256; // after how many blocks the pattern loops
  public static final BlockPos LOOP_OFFSET = new BlockPos(SlimeColorizer.loop / 2, 0, SlimeColorizer.loop / 2);

  public static void setBlueGrassBiomeColorizer(int[] colorBufferBlueIn) {
    colorBufferBlue = colorBufferBlueIn;
  }

  public static void setPurpleGrassBiomeColorizer(int[] colorBufferPurpleIn) {
    colorBufferPurple = colorBufferPurpleIn;
  }

  public static void setOrangeGrassBiomeColorizer(int[] colorBufferOrangeIn) {
    colorBufferOrange = colorBufferOrangeIn;
  }

  public static int getColorBlue(int x, int z) {
    return getColor(x, z, colorBufferBlue);
  }

  public static int getColorPurple(int x, int z) {
    return getColor(x, z, colorBufferPurple);
  }

  public static int getColorOrange(int x, int z) {
    return getColor(x, z, colorBufferOrange);
  }

  private static int getColor(int posX, int posZ, int[] buffer) {
    float x = Math.abs((loop - (Math.abs(posX) % (2 * loop))) / loop);
    float z = Math.abs((loop - (Math.abs(posZ) % (2 * loop))) / loop);

    if (x < z) {
      float tmp = x;
      x = z;
      z = tmp;
    }

    return buffer[(int) (x * 255f) << 8 | (int) (z * 255f)];
  }

  /** Block.getRenderColor needs BGR instead of RGB for some reason */
  public static int getColorStaticBGR(SlimeGrassBlock.FoliageType type) {
    int color = getColorStatic(type);
    return (color >> 16) & 0xff |
            (color & 0xff) << 16 |
            (color & 0xff00);
  }

  /** Position independant Slime foliage color */
  public static int getColorStatic(SlimeGrassBlock.FoliageType type) {
    if (type == SlimeGrassBlock.FoliageType.PURPLE) {
      return SlimeColorizer.colorPurple;
    }
    else if (type == SlimeGrassBlock.FoliageType.ORANGE) {
      return SlimeColorizer.colorOrange;
    }

    return SlimeColorizer.colorBlue;
  }

  /** Position dependant Slime foliage color */
  public static int getColorForPos(BlockPos pos, SlimeGrassBlock.FoliageType type) {
    if (type == SlimeGrassBlock.FoliageType.PURPLE) {
      return SlimeColorizer.getColorPurple(pos.getX(), pos.getZ());
    }
    else if (type == SlimeGrassBlock.FoliageType.ORANGE) {
      return SlimeColorizer.getColorOrange(pos.getX(), pos.getZ());
    }
    return SlimeColorizer.getColorBlue(pos.getX(), pos.getZ());
  }
}
