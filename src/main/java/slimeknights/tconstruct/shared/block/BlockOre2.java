package slimeknights.tconstruct.shared.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Locale;

import javax.annotation.Nonnull;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.utils.HarvestLevels;

public class BlockOre2 extends EnumBlock<BlockOre2.OreTypes> {

  public static final PropertyEnum<OreTypes> TYPE = PropertyEnum.create("type", OreTypes.class);

  public BlockOre2() {
    this(Material.ROCK);
  }

  public BlockOre2(Material material) {
    super(material, TYPE, OreTypes.class);

    setHardness(10f);
    setHarvestLevel("pickaxe", HarvestLevels.ENDERITUM);
    setCreativeTab(TinkerRegistry.tabWorld);
  }

  @Override
  public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
    for(OreTypes type : OreTypes.values()) {
      list.add(new ItemStack(this, 1, type.getMeta()));
    }
  }

  @Nonnull
  @Override
  @SideOnly(Side.CLIENT)
  public BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.CUTOUT_MIPPED;
  }

  public enum OreTypes implements IStringSerializable, EnumBlock.IEnumMeta {
    ENDERITUM,
    TITANIUM;

    public final int meta;

    OreTypes() {
      meta = ordinal();
    }

    @Override
    public String getName() {
      return this.toString().toLowerCase(Locale.US);
    }

    @Override
    public int getMeta() {
      return meta;
    }
  }
}
