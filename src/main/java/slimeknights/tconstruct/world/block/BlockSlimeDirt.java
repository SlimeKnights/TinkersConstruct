package slimeknights.tconstruct.world.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.library.TinkerRegistry;

public class BlockSlimeDirt extends EnumBlock<BlockSlimeDirt.DirtType> {
  public static PropertyEnum TYPE = PropertyEnum.create("type", DirtType.class);

  public BlockSlimeDirt() {
    super(Material.ground, TYPE, DirtType.class);
    this.setCreativeTab(TinkerRegistry.tabWorld);
  }

  public enum DirtType implements IStringSerializable, EnumBlock.IEnumMeta {
    GREEN,
    BLUE,
    PURPLE;

    DirtType() {
      this.meta = this.ordinal();
    }

    public final int meta;

    @Override
    public int getMeta() {
      return meta;
    }

    @Override
    public String getName() {
      return this.toString();
    }
  }
}
