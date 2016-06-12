package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.mantle.block.EnumBlockSlab;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public class BlockSearedSlab extends EnumBlockSlab<BlockSearedSlab.SearedType> {

  public final static PropertyEnum<SearedType> TYPE = PropertyEnum.create("type", SearedType.class);

  public BlockSearedSlab() {
    super(Material.ROCK, TYPE, SearedType.class);
    this.setCreativeTab(TinkerRegistry.tabSmeltery);
    this.setHardness(3F);
    this.setResistance(20F);
    this.setSoundType(SoundType.METAL);
  }

  @Override
  public IBlockState getFullBlock(IBlockState state) {
    if(TinkerSmeltery.searedBlock == null) {
      return null;
    }
    return TinkerSmeltery.searedBlock.getDefaultState().withProperty(BlockSeared.TYPE, state.getValue(TYPE).asSearedBlock());
  }

  // using a separate Enum than BlockSeared since there are 9 types (and slabs only support 8)
  public enum SearedType implements IStringSerializable, EnumBlock.IEnumMeta {
    STONE,
    COBBLE,
    PAVER,
    BRICK,
    BRICK_CRACKED,
    BRICK_FANCY,
    BRICK_SQUARE,
    ROAD;
    // creeper is in BlockStoneSlab2

    public final int meta;

    SearedType() {
      meta = ordinal();
    }

    @Override
    public String getName() {
      return this.toString().toLowerCase(Locale.US);
    }

    public BlockSeared.SearedType asSearedBlock() {
      switch(this) {
        case STONE:
          return BlockSeared.SearedType.STONE;
        case COBBLE:
          return BlockSeared.SearedType.COBBLE;
        case PAVER:
          return BlockSeared.SearedType.PAVER;
        case BRICK:
          return BlockSeared.SearedType.BRICK;
        case BRICK_CRACKED:
          return BlockSeared.SearedType.BRICK_CRACKED;
        case BRICK_FANCY:
          return BlockSeared.SearedType.BRICK_FANCY;
        case BRICK_SQUARE:
          return BlockSeared.SearedType.BRICK_SQUARE;
        case ROAD:
          return BlockSeared.SearedType.ROAD;
        default:
          return null;
      }
    }

    @Override
    public int getMeta() {
      return meta;
    }
  }
}
