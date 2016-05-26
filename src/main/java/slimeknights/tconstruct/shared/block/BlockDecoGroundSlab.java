package slimeknights.tconstruct.shared.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;

import slimeknights.mantle.block.EnumBlockSlab;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.shared.TinkerCommons;

public class BlockDecoGroundSlab extends EnumBlockSlab<BlockDecoGround.DecoGroundType> {

  public final static PropertyEnum<BlockDecoGround.DecoGroundType> TYPE = PropertyEnum.create("type", BlockDecoGround.DecoGroundType.class);

  public BlockDecoGroundSlab() {
    super(Material.GROUND, TYPE, BlockDecoGround.DecoGroundType.class);
    this.setHardness(2.0f);

    this.setSoundType(SoundType.GROUND);

    setHarvestLevel("Shovel", -1);
    setCreativeTab(TinkerRegistry.tabGeneral);
  }

  @Override
  public IBlockState getFullBlock(IBlockState state) {
    return TinkerCommons.blockDecoGround.getDefaultState().withProperty(BlockDecoGround.TYPE, state.getValue(TYPE));
  }
}
