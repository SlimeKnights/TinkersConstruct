package slimeknights.tconstruct.shared.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.library.TinkerRegistry;

public class BlockSlime extends net.minecraft.block.BlockSlime {

  public static final PropertyEnum<SlimeType> TYPE = PropertyEnum.create("type", SlimeType.class);

  public BlockSlime() {
    this.setCreativeTab(TinkerRegistry.tabWorld);
    this.disableStats();
    this.setSoundType(SoundType.SLIME);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void getSubBlocks(@Nonnull Item itemIn, CreativeTabs tab, List<ItemStack> list) {
    for(SlimeType type : SlimeType.values()) {
      list.add(new ItemStack(this, 1, type.meta));
    }
  }

  @Nonnull
  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, TYPE);
  }

  @Nonnull
  @Override
  public IBlockState getStateFromMeta(int meta) {
    return this.getDefaultState().withProperty(TYPE, SlimeType.fromMeta(meta));
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return state.getValue(TYPE).meta;
  }

  @Override
  public int damageDropped(IBlockState state) {
    return getMetaFromState(state);
  }

  public enum SlimeType implements IStringSerializable, EnumBlock.IEnumMeta {
    GREEN(0x01cd4e, 0x69bc5e),
    BLUE(0x01cbcd, 0x74c5c8),
    PURPLE(0xaf4cf6, 0xcc68ff),
    BLOOD(0xb50101, 0xb80000),
    MAGMA(0xff970d, 0xffab49);

    private SlimeType(int color, int ballColor) {
      this.meta = this.ordinal();
      this.color = color;
      this.ballColor = ballColor;
    }

    public final int meta;
    private final int color, ballColor;

    public static SlimeType fromMeta(int meta) {
      if(meta < 0 || meta >= values().length) {
        meta = 0;
      }

      return values()[meta];
    }

    @Override
    public int getMeta() {
      return meta;
    }

    @Override
    public String getName() {
      return this.toString().toLowerCase(Locale.US);
    }

    /**
     * Returns the block color for this slime type
     */
    public int getColor() {
      return color;
    }


    /**
     * Returns the slimeball color for this slime type, usually it is less saturated
     */
    public int getBallColor() {
      return ballColor;
    }
  }
}
