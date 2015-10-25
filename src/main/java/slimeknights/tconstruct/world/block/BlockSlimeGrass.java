package slimeknights.tconstruct.world.block;

import net.minecraft.block.BlockGrass;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.world.client.SlimeColorizer;

public class BlockSlimeGrass extends BlockGrass {
  public static PropertyEnum TYPE = PropertyEnum.create("type", DirtType.class);

  public BlockSlimeGrass() {
    this.setCreativeTab(TinkerRegistry.tabWorld);
  }

  @Override
  public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
    // todo: grow slime thingies :D
    super.grow(worldIn, rand, pos, state);
  }


  @SideOnly(Side.CLIENT)
  @Override
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
    for(DirtType type : DirtType.values()) {
      list.add(new ItemStack(this, 1, type.meta));
    }
  }

  @Override
  protected BlockState createBlockState() {
    return new BlockState(this, TYPE, BlockGrass.SNOWY);
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return this.getDefaultState().withProperty(TYPE, DirtType.fromMeta(meta));
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return ((DirtType) state.getValue(TYPE)).meta;
  }

  @Override
  public int damageDropped(IBlockState state) {
    return getMetaFromState(state);
  }

  @Override
  public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
    float loop = 250;
    float x = Math.abs((loop - (Math.abs(pos.getX())%(2*loop)))/loop);
    float z = Math.abs((loop - (Math.abs(pos.getZ())%(2*loop)))/loop);

    if(x < z) {
      float tmp = x;
      x = z;
      z = tmp;
    }

    return SlimeColorizer.getColor(x,z);
  }

  public enum DirtType implements IStringSerializable, EnumBlock.IEnumMeta {
    VANILLA,
    GREEN,
    BLUE;

    DirtType() {
      this.meta = this.ordinal();
    }

    public final int meta;

    @Override
    public int getMeta() {
      return meta;
    }

    public static DirtType fromMeta(int meta) {
      if(meta < 0 || meta > values().length) {
        meta = 0;
      }

      return values()[meta];
    }

    @Override
    public String getName() {
      return this.toString();
    }
  }
}
