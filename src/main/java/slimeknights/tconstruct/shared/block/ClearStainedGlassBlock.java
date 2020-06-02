package slimeknights.tconstruct.shared.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.block.ConnectedTextureBlock;

import javax.annotation.Nullable;
import java.util.Locale;

public class ClearStainedGlassBlock extends ConnectedTextureBlock {

  private final GlassColor glassColor;

  public ClearStainedGlassBlock(Properties properties, GlassColor glassColor) {
    super(properties);
    this.glassColor = glassColor;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return 1.0F;
  }

  @Override
  public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
    return true;
  }

  @Override
  public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return false;
  }

  @Override
  public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return false;
  }

  @Override
  public boolean canEntitySpawn(BlockState state, IBlockReader worldIn, BlockPos pos, EntityType<?> type) {
    return false;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
    return this.canConnect(state, adjacentBlockState);
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
    WHITE(0xffffff, MaterialColor.SNOW),
    ORANGE(0xd87f33, MaterialColor.ADOBE),
    MAGENTA(0xb24cd8, MaterialColor.MAGENTA),
    LIGHT_BLUE(0x6699d8, MaterialColor.LIGHT_BLUE),
    YELLOW(0xe5e533, MaterialColor.YELLOW),
    LIME(0x7fcc19, MaterialColor.LIME),
    PINK(0xf27fa5, MaterialColor.PINK),
    GRAY(0x4c4c4c, MaterialColor.GRAY),
    LIGHT_GRAY(0x999999, MaterialColor.LIGHT_GRAY),
    CYAN(0x4c7f99, MaterialColor.CYAN),
    PURPLE(0x7f3fb2, MaterialColor.PURPLE),
    BLUE(0x334cb2, MaterialColor.BLUE),
    BROWN(0x664c33, MaterialColor.BROWN),
    GREEN(0x667f33, MaterialColor.GREEN),
    RED(0x993333, MaterialColor.RED),
    BLACK(0x191919, MaterialColor.BLACK);

    private final int color;
    private final MaterialColor materialColor;
    private final float[] rgb;
    private final String name;

    GlassColor(int color, MaterialColor mapColor) {
      this.color = color;
      this.materialColor = mapColor;
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
      return this.materialColor;
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
