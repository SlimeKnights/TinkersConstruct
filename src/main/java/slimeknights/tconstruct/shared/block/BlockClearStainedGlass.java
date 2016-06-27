package slimeknights.tconstruct.shared.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Locale;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.mantle.block.EnumBlockConnectedTexture;
import slimeknights.tconstruct.library.TinkerRegistry;

public class BlockClearStainedGlass extends EnumBlockConnectedTexture<BlockClearStainedGlass.EnumGlassColor> {

  public static final PropertyEnum<EnumGlassColor> COLOR = PropertyEnum.<EnumGlassColor>create("color", EnumGlassColor.class);

  public BlockClearStainedGlass() {
    super(Material.GLASS, COLOR, EnumGlassColor.class);

    this.setHardness(0.3f);
    setHarvestLevel("pickaxe", -1);
    this.setSoundType(SoundType.GLASS);

    this.setCreativeTab(TinkerRegistry.tabGeneral);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.TRANSLUCENT;
  }

  @Override
  public boolean isFullCube(IBlockState state) {
    return false;
  }

  @Override
  public boolean isOpaqueCube(IBlockState state) {
    return false;
  }

  /**
   * Get the MapColor for this Block and the given BlockState
   */
  @Override
  public MapColor getMapColor(IBlockState state) {
    return state.getValue(COLOR).getMapColor();
  }

  @Override
  @SuppressWarnings("deprecation")
  public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
    return canConnect(blockState, blockAccess.getBlockState(pos.offset(side))) ? false : super.shouldSideBeRendered(blockState, blockAccess, pos, side);
  }

  // The default does not implement EnumBlock.IEnumMeta, and Enums cannot be extended
  public enum EnumGlassColor implements IStringSerializable, EnumBlock.IEnumMeta {
    WHITE(0xffffff, MapColor.SNOW),
    ORANGE(0xd87f33, MapColor.ADOBE),
    MAGENTA(0xb24cd8, MapColor.MAGENTA),
    LIGHT_BLUE(0x6699d8, MapColor.LIGHT_BLUE),
    YELLOW(0xe5e533, MapColor.YELLOW),
    LIME(0x7fcc19, MapColor.LIME),
    PINK(0xf27fa5, MapColor.PINK),
    GRAY(0x4c4c4c, MapColor.GRAY),
    SILVER(0x999999, MapColor.SILVER),
    CYAN(0x4c7f99, MapColor.CYAN),
    PURPLE(0x7f3fb2, MapColor.PURPLE),
    BLUE(0x334cb2, MapColor.BLUE),
    BROWN(0x664c33, MapColor.BROWN),
    GREEN(0x667f33, MapColor.GREEN),
    RED(0x993333, MapColor.RED),
    BLACK(0x191919, MapColor.BLACK);

    private final int color;
    private final MapColor mapColor;
    private final int meta;

    private EnumGlassColor(int color, MapColor mapColor) {
      this.meta = ordinal();
      this.color = color;
      this.mapColor = mapColor;
    }

    @Override
    public int getMeta() {
      return meta;
    }

    @Override
    public String getName() {
      return this.toString().toLowerCase(Locale.US);
    }

    // tintIndex for the variant, as we only use one texture
    public int getColor() {
      return color;
    }

    public MapColor getMapColor() {
      return mapColor;
    }

  }
}
