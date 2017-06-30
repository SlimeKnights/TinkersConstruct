package slimeknights.tconstruct.gadgets.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Locale;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.mantle.block.EnumBlockSlab;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.TinkerRegistry;

public class BlockBrownstoneSlab2 extends EnumBlockSlab<BlockBrownstoneSlab2.BrownstoneType> {

  public final static PropertyEnum<BrownstoneType> TYPE = PropertyEnum.create("type", BrownstoneType.class);

  public BlockBrownstoneSlab2() {
    super(Material.ROCK, TYPE, BrownstoneType.class);
    this.setCreativeTab(TinkerRegistry.tabGadgets);
    this.setHardness(3F);
    this.setResistance(20F);
    this.setSoundType(SoundType.STONE);
  }

  @Override
  public void onEntityWalk(World worldIn, BlockPos pos, Entity entity) {
    if(entity.isInWater()) {
      entity.motionX *= 1.20;
      entity.motionZ *= 1.20;
    }
    else {
      entity.motionX *= 1.25;
      entity.motionZ *= 1.25;
    }
  }

  @Override
  public IBlockState getFullBlock(IBlockState state) {
    if(TinkerGadgets.brownstone == null) {
      return null;
    }
    return TinkerGadgets.brownstone.getDefaultState().withProperty(BlockBrownstone.TYPE, state.getValue(TYPE).asBrownstone());
  }

  public enum BrownstoneType implements IStringSerializable, EnumBlock.IEnumMeta {
    CREEPER,
    BRICK_TRIANGLE,
    BRICK_SMALL,
    TILE;

    public final int meta;

    BrownstoneType() {
      meta = ordinal();
    }

    @Override
    public String getName() {
      return this.toString().toLowerCase(Locale.US);
    }

    public BlockBrownstone.BrownstoneType asBrownstone() {
      switch(this) {
        case CREEPER:
          return BlockBrownstone.BrownstoneType.CREEPER;
        case BRICK_SMALL:
          return BlockBrownstone.BrownstoneType.BRICK_SMALL;
        case BRICK_TRIANGLE:
          return BlockBrownstone.BrownstoneType.BRICK_TRIANGLE;
        case TILE:
          return BlockBrownstone.BrownstoneType.TILE;
        default:
          throw new IllegalArgumentException("Unknown enum value? Impossibru!");
      }
    }

    @Override
    public int getMeta() {
      return meta;
    }
  }
}
