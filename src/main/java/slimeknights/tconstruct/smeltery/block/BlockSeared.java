package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.library.TinkerRegistry;

public class BlockSeared extends BlockEnumSmeltery<BlockSeared.SearedType> {

  public final static PropertyEnum<SearedType> TYPE = PropertyEnum.create("type", SearedType.class);

  public BlockSeared() {
    super(Material.rock, TYPE, SearedType.class);
    this.setCreativeTab(TinkerRegistry.tabSmeltery);
    setHardness(3F);
    setResistance(20F);
    setStepSound(soundTypeMetal);
  }

  public enum SearedType implements IStringSerializable, EnumBlock.IEnumMeta {
    STONE,
    COBBLE,
    PAVER,
    BRICK,
    BRICK_CRACKED,
    BRICK_FANCY,
    BRICK_SQUARE,
    ROAD,
    CREEPER;

    public final int meta;

    SearedType() {
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
