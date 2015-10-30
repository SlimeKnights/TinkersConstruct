package slimeknights.tconstruct.world.block;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.client.SlimeColorizer;
import slimeknights.tconstruct.world.block.BlockSlimeGrass.FoliageType;

public class BlockTallSlimeGrass extends BlockBush implements IShearable {

  public static PropertyEnum TYPE = PropertyEnum.create("type", SlimePlantType.class);
  public static PropertyEnum FOLIAGE = BlockSlimeGrass.FOLIAGE;

  public BlockTallSlimeGrass() {
    setCreativeTab(TinkerRegistry.tabWorld);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
    for(SlimePlantType type : SlimePlantType.values()) {
      for(FoliageType foliage : FoliageType.values()) {
        list.add(new ItemStack(this, 1, getMetaFromState(getDefaultState().withProperty(TYPE, type).withProperty(FOLIAGE, foliage))));
      }
    }
  }

  /* State stuff */
  @Override
  protected BlockState createBlockState() {
    return new BlockState(this, TYPE, FOLIAGE);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    int meta = ((SlimePlantType)state.getValue(TYPE)).getMeta();
    meta |= ((FoliageType)state.getValue(FOLIAGE)).ordinal() << 2;

    return meta;
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    int type = meta & 3;
    if(type >= SlimePlantType.values().length) {
      type = 0;
    }

    int foliage = meta >> 2;
    if(foliage >= FoliageType.values().length) {
      foliage = 0;
    }
    IBlockState state = getDefaultState();
    state = state.withProperty(TYPE, SlimePlantType.values()[type]);
    state = state.withProperty(FOLIAGE, FoliageType.values()[foliage]);

    return state;
  }


  /* Logic stuff */

  @Override
  public boolean isReplaceable(World worldIn, BlockPos pos) {
    return true;
  }

  @Override
  public int getDamageValue(World worldIn, BlockPos pos) {
    IBlockState iblockstate = worldIn.getBlockState(pos);
    return iblockstate.getBlock().getMetaFromState(iblockstate);
  }

  @Override
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    return null;
  }

  @Override
  protected boolean canPlaceBlockOn(Block ground) {
    return ground == TinkerWorld.slimeGrass || ground == TinkerWorld.slimeDirt;
  }

  /* Rendering Stuff */
  /**
   * Get the OffsetType for this Block. Determines if the model is rendered slightly offset.
   */
  @SideOnly(Side.CLIENT)
  public Block.EnumOffsetType getOffsetType()
  {
    return Block.EnumOffsetType.XYZ;
  }

  @Override
  public int getBlockColor() {
    return SlimeColorizer.colorBlue;
  }

  // Used for the item
  @SideOnly(Side.CLIENT)
  @Override
  public int getRenderColor(IBlockState state) {
    FoliageType foliageType = (FoliageType) state.getValue(FOLIAGE);
    return SlimeColorizer.getColorStatic(foliageType);
  }

  // Used for the block in world
  @SideOnly(Side.CLIENT)
  @Override
  public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
    IBlockState state = worldIn.getBlockState(pos);
    if(state.getBlock() != this) return getBlockColor();

    FoliageType foliageType = (FoliageType) state.getValue(FOLIAGE);
    return SlimeColorizer.getColorForPos(pos, foliageType);
  }

  /* Forge/MC callbacks */
  @Override
  public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
    return TinkerWorld.slimePlantType;
  }

  @Override
  public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
    return true;
  }

  @Override
  public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
  {
    IBlockState state = world.getBlockState(pos);
    ItemStack stack = new ItemStack(this, 1, getMetaFromState(state));
    return Lists.newArrayList(stack);
  }

  public enum SlimePlantType implements IStringSerializable, EnumBlock.IEnumMeta {
    TALL_GRASS,
    FERN;


    SlimePlantType() {
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
