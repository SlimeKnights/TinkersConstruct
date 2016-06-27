package slimeknights.tconstruct.world.block;

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

  public enum SlimeType implements IStringSerializable {
    GREEN,
    BLUE,
    PURPLE,
    BLOOD,
    MAGMA;

    SlimeType() {
      this.meta = this.ordinal();
    }

    public final int meta;

    public static SlimeType fromMeta(int meta) {
      if(meta < 0 || meta >= values().length) {
        meta = 0;
      }

      return values()[meta];
    }

    @Override
    public String getName() {
      return this.toString().toLowerCase(Locale.US);
    }
  }
}
