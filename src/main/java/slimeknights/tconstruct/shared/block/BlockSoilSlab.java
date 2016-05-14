package slimeknights.tconstruct.shared.block;

import java.util.Locale;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.mantle.block.EnumBlockSlab;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.shared.TinkerCommons;

public class BlockSoilSlab extends EnumBlockSlab<BlockSoilSlab.SoilType> {

  public final static PropertyEnum<SoilType> TYPE = PropertyEnum.create("type", SoilType.class);

  public BlockSoilSlab() {
    super(Material.SAND, TYPE, SoilType.class);
    this.slipperiness = 0.8F;
    this.setHardness(3.0f);

    this.setSoundType(SoundType.SAND);

    setHarvestLevel("Shovel", -1);
    setCreativeTab(TinkerRegistry.tabGeneral);
  }

  @Override
  public IBlockState getFullBlock(IBlockState state) {
    if(TinkerCommons.blockSoil == null) {
      return null;
    }
    return TinkerCommons.blockSoil.getDefaultState().withProperty(BlockSoil.TYPE, state.getValue(TYPE).asSoil());
  }

  public enum SoilType implements IStringSerializable, EnumBlock.IEnumMeta {
    MUDBRICK;

    public final int meta;

    SoilType() {
      meta = ordinal();
    }

    @Override
    public String getName() {
      return this.toString().toLowerCase(Locale.US);
    }
    
    public BlockSoil.SoilTypes asSoil() {
      switch(this) {
      case MUDBRICK:
        return BlockSoil.SoilTypes.MUDBRICK;
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
