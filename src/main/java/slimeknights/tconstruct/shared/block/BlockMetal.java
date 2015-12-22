package slimeknights.tconstruct.shared.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.library.TinkerRegistry;

public class BlockMetal extends EnumBlock<BlockMetal.MetalTypes> {

  public static final PropertyEnum<MetalTypes> TYPE = PropertyEnum.create("type", MetalTypes.class);

  public BlockMetal() {
    super(Material.iron, TYPE, MetalTypes.class);

    setHardness(5f);
    setHarvestLevel("pickaxe", -1); // we're generous. no harvest level required
    setCreativeTab(TinkerRegistry.tabGeneral);
  }

  public enum MetalTypes implements IStringSerializable, EnumBlock.IEnumMeta {
    COBALT,
    ARDITE,
    MANYULLYN,
    KNIGHTSLIME;

    public  final int meta;

    MetalTypes() {
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
