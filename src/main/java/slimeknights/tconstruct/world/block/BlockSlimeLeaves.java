package slimeknights.tconstruct.world.block;

import com.google.common.collect.Lists;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.Random;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.client.SlimeColorizer;
import slimeknights.tconstruct.world.block.BlockSlimeGrass.FoliageType;

public class BlockSlimeLeaves extends BlockLeaves {

  public BlockSlimeLeaves() {
    this.setCreativeTab(TinkerRegistry.tabWorld);
    this.setHardness(0.3f);

    this.setDefaultState(this.blockState.getBaseState().withProperty(CHECK_DECAY, false).withProperty(DECAYABLE, true));
  }

  @Override
  public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
    super.updateTick(worldIn, pos, state, rand);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
    for(FoliageType type : FoliageType.values()) {
      list.add(new ItemStack(this, 1, getMetaFromState(this.getDefaultState().withProperty(BlockSlimeGrass.FOLIAGE, type))));
    }
  }

  @Override
  public boolean isOpaqueCube() {
    return Blocks.leaves.isOpaqueCube();
  }

  @SideOnly(Side.CLIENT)
  @Override
  public EnumWorldBlockLayer getBlockLayer() {
    return Blocks.leaves.getBlockLayer();
  }

  @Override
  protected int getSaplingDropChance(IBlockState state) {
    return 35;
  }

  // sapling item
  @Override
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    return Item.getItemFromBlock(TinkerWorld.slimeSapling);
  }

  // sapling meta
  @Override
  public int damageDropped(IBlockState state) {
    return ((FoliageType)state.getValue(BlockSlimeGrass.FOLIAGE)).ordinal() & 3; // only first 2 bits
  }

  // item dropped on silktouching
  protected ItemStack createStackedBlock(IBlockState state)
  {
    return new ItemStack(Item.getItemFromBlock(this), 1, ((FoliageType)state.getValue(BlockSlimeGrass.FOLIAGE)).ordinal() & 3);
  }

  @Override
  protected BlockState createBlockState() {
    return new BlockState(this, BlockSlimeGrass.FOLIAGE, CHECK_DECAY, DECAYABLE);
  }

  /**
   * Convert the given metadata into a BlockState for this Block
   */
  public IBlockState getStateFromMeta(int meta)
  {
    int type = meta % 4;
    if(type < 0 || type >= FoliageType.values().length) {
      type = 0;
    }
    FoliageType grass = FoliageType.values()[type];
    return this.getDefaultState()
               .withProperty(BlockSlimeGrass.FOLIAGE, grass)
               .withProperty(DECAYABLE, (meta & 4) == 0)
               .withProperty(CHECK_DECAY, (meta & 8) > 0);
  }

  /**
   * Convert the BlockState into the correct metadata value
   */
  public int getMetaFromState(IBlockState state)
  {

    int meta = ((FoliageType)state.getValue(BlockSlimeGrass.FOLIAGE)).ordinal() & 3; // only first 2 bits

    if (!(Boolean) state.getValue(DECAYABLE))
    {
      meta |= 4;
    }

    if ((Boolean) state.getValue(CHECK_DECAY))
    {
      meta |= 8;
    }

    return meta;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getBlockColor ()
  {
    return 0xffffff;
  }

  // Used for the item
  @SideOnly(Side.CLIENT)
  @Override
  public int getRenderColor(IBlockState state) {
    FoliageType foliageType = (FoliageType) state.getValue(BlockSlimeGrass.FOLIAGE);
    return SlimeColorizer.getColorStatic(foliageType);
  }

  // Used for the block in world
  @SideOnly(Side.CLIENT)
  @Override
  public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
    IBlockState state = worldIn.getBlockState(pos);
    if(state.getBlock() != this) return getBlockColor();

    FoliageType foliageType = (FoliageType) state.getValue(BlockSlimeGrass.FOLIAGE);
    return SlimeColorizer.getColorForPos(pos, foliageType);
  }

  @Override
  public BlockPlanks.EnumType getWoodType(int meta) {
    throw new NotImplementedException(); // unused by our code.
  }

  @Override
  public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
    IBlockState state = world.getBlockState(pos);
    return Lists.newArrayList(createStackedBlock(state));
  }

  @Override
  public boolean isLeaves(IBlockAccess world, BlockPos pos) {
    return true;
  }
}
