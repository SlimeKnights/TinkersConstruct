package slimeknights.tconstruct.shared.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.library.TinkerRegistry;

public class BlockFirewood extends EnumBlock<BlockFirewood.FirewoodType> {

  public final static PropertyEnum<FirewoodType> TYPE = PropertyEnum.create("type", FirewoodType.class);

  public BlockFirewood() {
    super(Material.ground, TYPE, FirewoodType.class);

    this.setHardness(2f);
    this.setResistance(7f);
    this.setCreativeTab(TinkerRegistry.tabGeneral);
    this.setLightLevel(0.5f);
    setStepSound(soundTypeWood);

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
      return this.toString();
    }

    @Override
    public int getMeta() {
      return meta;
    }
  }
}
