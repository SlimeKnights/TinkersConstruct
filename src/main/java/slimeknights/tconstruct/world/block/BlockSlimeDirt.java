package slimeknights.tconstruct.world.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IPlantable;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.world.TinkerWorld;

public class BlockSlimeDirt extends EnumBlock<BlockSlimeDirt.DirtType> {
  public static PropertyEnum TYPE = PropertyEnum.create("type", DirtType.class);

  public BlockSlimeDirt() {
    super(Material.ground, TYPE, DirtType.class);
    this.setCreativeTab(TinkerRegistry.tabWorld);
  }

  @Override
  public boolean canSustainPlant(IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
    return plantable.getPlantType(world, pos) == TinkerWorld.slimePlantType;
  }

  public enum DirtType implements IStringSerializable, EnumBlock.IEnumMeta {
    GREEN,
    BLUE,
    PURPLE,
    MAGMA;

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
