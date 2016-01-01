package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import slimeknights.mantle.block.BlockInventory;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.smeltery.tileentity.TileCastingBasin;
import slimeknights.tconstruct.smeltery.tileentity.TileCastingTable;

public class BlockCasting extends BlockInventory {

  public static final PropertyEnum<CastingType> TYPE = PropertyEnum.create("type", CastingType.class);

  public BlockCasting() {
    super(Material.rock);
    setHardness(3F);
    setResistance(20F);
    setCreativeTab(TinkerRegistry.tabSmeltery);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
    for(CastingType type : CastingType.values()) {
      list.add(new ItemStack(this, 1, type.getMeta()));
    }
  }

  @Override
  protected BlockState createBlockState() {
    return new BlockState(this, TYPE);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return state.getValue(TYPE).getMeta();
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    if(meta < 0 || meta >= CastingType.values().length) {
      meta = 0;
    }
    return getDefaultState().withProperty(TYPE, CastingType.values()[meta]);
  }

  @Override
  public int damageDropped(IBlockState state) {
    return getMetaFromState(state);
  }

  @Override
  public TileEntity createNewTileEntity(World worldIn, int meta) {
    switch(getStateFromMeta(meta).getValue(TYPE)) {
      case TABLE:
        return new TileCastingTable();
      case BASIN:
        return new TileCastingBasin();
    }
    return null;
  }

  @Override
  protected boolean openGui(EntityPlayer player, World world, BlockPos pos) {
    // no gui
    return false;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @SideOnly(Side.CLIENT)
  public EnumWorldBlockLayer getBlockLayer()
  {
    return EnumWorldBlockLayer.CUTOUT;
  }

  public enum CastingType implements IStringSerializable, EnumBlock.IEnumMeta {
    TABLE,
    BASIN;

    public final int meta;

    CastingType() {
      meta = ordinal();
    }

    @Override
    public String getName() {
      return this.toString();
    }

    @Override
    public int getMeta() {
      return meta;
    }
  }
}
