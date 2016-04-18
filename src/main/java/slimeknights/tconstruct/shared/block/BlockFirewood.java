package slimeknights.tconstruct.shared.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.library.TinkerRegistry;

public class BlockFirewood extends EnumBlock<BlockFirewood.FirewoodType> {

  public final static PropertyEnum<FirewoodType> TYPE = PropertyEnum.create("type", FirewoodType.class);

  public BlockFirewood() {
    super(Material.WOOD, TYPE, FirewoodType.class);

    this.setHardness(2f);
    this.setResistance(7f);
    this.setCreativeTab(TinkerRegistry.tabGeneral);
    this.setLightLevel(0.5f);
    this.setSoundType(SoundType.WOOD);

    this.setHarvestLevel("axe", -1);
  }

  public enum FirewoodType implements IStringSerializable, EnumBlock.IEnumMeta {
    LAVAWOOD,
    FIREWOOD;

    public final int meta;

    FirewoodType() {
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
