package slimeknights.tconstruct.shared.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.TinkerIntegration;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.shared.TinkerFluids;

import java.util.Locale;

public class BlockMetal extends EnumBlock<BlockMetal.MetalTypes> {

  public static final PropertyEnum<MetalTypes> TYPE = PropertyEnum.create("type", MetalTypes.class);

  public BlockMetal() {
    super(Material.IRON, TYPE, MetalTypes.class);

    setHardness(5f);
    setHarvestLevel("pickaxe", -1); // we're generous. no harvest level required
    setCreativeTab(TinkerRegistry.tabGeneral);
  }

  @Override
  public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
    for(MetalTypes type : MetalTypes.values()) {
      if(type == MetalTypes.ALUBRASS && !TinkerIntegration.isIntegrated(TinkerFluids.alubrass)) {
        continue;
      }
      list.add(new ItemStack(this, 1, type.getMeta()));
    }
  }

  @Override
  public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon) {
    return true;
  }

  public enum MetalTypes implements IStringSerializable, EnumBlock.IEnumMeta {
    COBALT,
    ARDITE,
    MANYULLYN,
    KNIGHTSLIME,
    PIGIRON,
    ALUBRASS,
    SILKY_JEWEL;

    public final int meta;

    MetalTypes() {
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
