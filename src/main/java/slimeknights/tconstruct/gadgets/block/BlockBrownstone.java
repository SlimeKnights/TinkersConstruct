package slimeknights.tconstruct.gadgets.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.entity.Entity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Locale;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.library.TinkerRegistry;

public class BlockBrownstone extends EnumBlock<BlockBrownstone.BrownstoneType> {

  public final static PropertyEnum<BrownstoneType> TYPE = PropertyEnum.create("type", BrownstoneType.class);

  public BlockBrownstone() {
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

  public enum BrownstoneType implements IStringSerializable, EnumBlock.IEnumMeta {
    SMOOTH,
    ROUGH,
    PAVER,
    BRICK,
    BRICK_CRACKED,
    BRICK_FANCY,
    BRICK_SQUARE,
    ROAD,
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

    @Override
    public int getMeta() {
      return meta;
    }
  }
}
