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

public class BlockSearedSlab2 extends EnumBlockSlab<BlockSearedSlab2.SearedType> {

  public final static PropertyEnum<SearedType> TYPE = PropertyEnum.create("type", SearedType.class);

  public BlockSearedSlab2() {
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

  public enum SearedType implements IStringSerializable, EnumBlock.IEnumMeta {
    CREEPER,
    BRICK_TRIANGLE,
    BRICK_SMALL,
    TILE;

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
        case CREEPER:
          return BlockSeared.SearedType.CREEPER;
        case BRICK_SMALL:
          return BlockSeared.SearedType.BRICK_SMALL;
        case BRICK_TRIANGLE:
          return BlockSeared.SearedType.BRICK_TRIANGLE;
        case TILE:
          return BlockSeared.SearedType.TILE;
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
