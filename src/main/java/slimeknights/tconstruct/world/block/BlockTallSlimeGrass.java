package slimeknights.tconstruct.world.block;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.BlockSlimeGrass.FoliageType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class BlockTallSlimeGrass extends BlockBush implements IShearable {

  public static PropertyEnum<SlimePlantType> TYPE = PropertyEnum.create("type", SlimePlantType.class);
  public static PropertyEnum<FoliageType> FOLIAGE = BlockSlimeGrass.FOLIAGE;

  public BlockTallSlimeGrass() {
    setCreativeTab(TinkerRegistry.tabWorld);
    this.setSoundType(SoundType.PLANT);
  }

  @Override
  public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
    for(SlimePlantType type : SlimePlantType.values()) {
      for(FoliageType foliage : FoliageType.values()) {
        list.add(new ItemStack(this, 1, getMetaFromState(getDefaultState().withProperty(TYPE, type).withProperty(FOLIAGE, foliage))));
      }
    }
  }

  /* State stuff */
  @Nonnull
  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, TYPE, FOLIAGE);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    int meta = (state.getValue(TYPE)).getMeta();
    meta |= (state.getValue(FOLIAGE)).ordinal() << 2;

    return meta;
  }

  @Nonnull
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
  public boolean isReplaceable(IBlockAccess worldIn, @Nonnull BlockPos pos) {
    return true;
  }

  @Nonnull
  @Override
  public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
    int meta = this.getMetaFromState(state);
    return new ItemStack(Item.getItemFromBlock(this), 1, meta);
  }

  @Override
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    return null;
  }

  @Override
  public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
    IBlockState soil = worldIn.getBlockState(pos.down());
    Block ground = soil.getBlock();
    return ground == TinkerWorld.slimeGrass || ground == TinkerWorld.slimeDirt;
  }

  /* Rendering Stuff */

  /**
   * Get the OffsetType for this Block. Determines if the model is rendered slightly offset.
   */
  @Nonnull
  @Override
  @SideOnly(Side.CLIENT)
  public Block.EnumOffsetType getOffsetType() {
    return Block.EnumOffsetType.XYZ;
  }

  /* Forge/MC callbacks */
  @Nonnull
  @Override
  public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
    return TinkerWorld.slimePlantType;
  }

  @Override
  public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
    return true;
  }

  @Override
  public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
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
      return this.toString().toLowerCase(Locale.US);
    }
  }
}
