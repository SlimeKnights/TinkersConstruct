package slimeknights.tconstruct.shared.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.DyeColor;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nullable;
import java.util.Locale;

public class ClearStainedGlassBlock extends ClearGlassBlock {

  private final GlassColor glassColor;

  public ClearStainedGlassBlock(Properties properties, GlassColor glassColor) {
    super(properties);
    this.glassColor = glassColor;
  }

  @Nullable
  @Override
  public float[] getBeaconColorMultiplier(BlockState state, IWorldReader world, BlockPos pos, BlockPos beaconPos) {
    if (this.glassColor != null) {
      return this.glassColor.rgb;
    }

    return null;
  }

  public GlassColor getGlassColor() {
    return this.glassColor;
  }

  // do not change enum names, they're used for block registries
  public enum GlassColor implements IStringSerializable {
    WHITE(0xffffff, DyeColor.WHITE),
    ORANGE(0xd87f33, DyeColor.ORANGE),
    MAGENTA(0xb24cd8, DyeColor.MAGENTA),
    LIGHT_BLUE(0x6699d8, DyeColor.LIGHT_BLUE),
    YELLOW(0xe5e533, DyeColor.YELLOW),
    LIME(0x7fcc19, DyeColor.LIME),
    PINK(0xf27fa5, DyeColor.PINK),
    GRAY(0x4c4c4c, DyeColor.GRAY),
    LIGHT_GRAY(0x999999, DyeColor.LIGHT_GRAY),
    CYAN(0x4c7f99, DyeColor.CYAN),
    PURPLE(0x7f3fb2, DyeColor.PURPLE),
    BLUE(0x334cb2, DyeColor.BLUE),
    BROWN(0x664c33, DyeColor.BROWN),
    GREEN(0x667f33, DyeColor.GREEN),
    RED(0x993333, DyeColor.RED),
    BLACK(0x191919, DyeColor.BLACK);

    private final int color;
    private final DyeColor dye;
    private final float[] rgb;
    private final String name;

    GlassColor(int color, DyeColor dye) {
      this.color = color;
      this.dye = dye;
      this.rgb = calcRGB(color);
      this.name = this.name().toLowerCase(Locale.US);
    }

    private static float[] calcRGB(int color) {
      float[] out = new float[3];
      out[0] = ((color >> 16) & 0xFF) / 255f;
      out[1] = ((color >> 8) & 0xFF) / 255f;
      out[2] = (color & 0xFF) / 255f;
      return out;
    }

    // tintIndex for the variant, as we only use one texture
    public int getColor() {
      return this.color;
    }

    public MaterialColor getMaterialColor() {
      return dye.getMapColor();
    }

    /** Gets the vanilla dye color associated with this color */
    public DyeColor getDye() {
      return dye;
    }

    @Override
    public String toString() {
      return name;
    }

    @Override
    public String getName() {
      return name;
    }
  }
}
