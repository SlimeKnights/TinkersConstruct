package slimeknights.tconstruct.gadgets.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.library.TinkerRegistry;

public class BlockDriedClay extends EnumBlock<BlockDriedClay.DriedBrickType> {

  public final static PropertyEnum<DriedBrickType> TYPE = PropertyEnum.create("type", DriedBrickType.class);

  public BlockDriedClay() {
    super(Material.ROCK, TYPE, DriedBrickType.class);
    this.setCreativeTab(TinkerRegistry.tabGadgets);
    this.setHardness(3F);
    this.setResistance(20F);
    this.setSoundType(SoundType.STONE);
  }

  public enum DriedBrickType implements IStringSerializable, EnumBlock.IEnumMeta {
    CLAY,
    BRICK;

    public final int meta;

    DriedBrickType() {
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
