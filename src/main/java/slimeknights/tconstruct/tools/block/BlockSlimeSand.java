package slimeknights.tconstruct.tools.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

import java.util.List;

public class BlockSlimeSand extends Block {

  public static final PropertyEnum TYPE = PropertyEnum.create("type", SoilTypes.class);

  public BlockSlimeSand() {
    super(Material.clay);
    this.slipperiness = 0.8F;
    this.setHardness(3.0f);

    this.setStepSound(soundTypeSand);

    setHarvestLevel("Shovel", -1);

    this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, SoilTypes.GREEN));
  }

  @Override
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
    list.add(new ItemStack(itemIn, 1, 0));
    list.add(new ItemStack(itemIn, 1, 1));
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    if(meta == 1)
      return this.getDefaultState().withProperty(TYPE, SoilTypes.BLUE);

    return this.getDefaultState();
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    if(state.getValue(TYPE) == SoilTypes.BLUE)
      return 1;
    return 0;
  }

  @Override
  public int damageDropped(IBlockState state) {
    return getMetaFromState(state);
  }

  @Override
  protected BlockState createBlockState() {
    return new BlockState(this, TYPE);
  }

  public enum SoilTypes implements IStringSerializable {
    GREEN,
    BLUE;

    @Override
    public String getName() {
      return this.toString();
    }
  }
}
