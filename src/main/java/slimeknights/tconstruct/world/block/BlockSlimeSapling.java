package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.BlockSlimeGrass.FoliageType;
import slimeknights.tconstruct.world.worldgen.SlimeTreeGenerator;

public class BlockSlimeSapling extends BlockSapling {
  public static PropertyEnum<FoliageType> FOLIAGE = BlockSlimeGrass.FOLIAGE;

  public BlockSlimeSapling() {
    setCreativeTab(TinkerRegistry.tabWorld);
    setDefaultState(this.blockState.getBaseState());
    this.setStepSound(soundTypeGrass);
  }

  @Override
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
    for(FoliageType type : FoliageType.values()) {
      list.add(new ItemStack(this, 1, getMetaFromState(getDefaultState().withProperty(FOLIAGE, type))));
    }
  }


  @Override
  protected BlockState createBlockState() {
    // TYPE has to be included because of the BlockSapling constructor.. but it's never used.
    return new BlockState(this, FOLIAGE, STAGE, TYPE);
  }

  /**
   * Convert the given metadata into a BlockState for this Block
   */
  public IBlockState getStateFromMeta(int meta)
  {
    if(meta < 0 || meta >= BlockSlimeGrass.FoliageType.values().length) {
      meta = 0;
    }
    BlockSlimeGrass.FoliageType grass = BlockSlimeGrass.FoliageType.values()[meta];
    return this.getDefaultState().withProperty(BlockSlimeGrass.FOLIAGE, grass);
  }

  /**
   * Convert the BlockState into the correct metadata value
   */
  public int getMetaFromState(IBlockState state)
  {
    return ((BlockSlimeGrass.FoliageType)state.getValue(BlockSlimeGrass.FOLIAGE)).ordinal();
  }

  @Override
  public int damageDropped(IBlockState state) {
    return getMetaFromState(state);
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
    return 0xffffff;
  }

  // Used for the block in world
  @SideOnly(Side.CLIENT)
  @Override
  public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
    return 0xffffff;
  }

  @Override
  public boolean isReplaceable(World worldIn, BlockPos pos) {
    return false;
  }

  @Override
  protected boolean canPlaceBlockOn(Block ground) {
    return ground == TinkerWorld.slimeGrass || ground == TinkerWorld.slimeDirt;
  }

  @Override
  public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
    return TinkerWorld.slimePlantType;
  }

  @Override
  public int getDamageValue(World worldIn, BlockPos pos) {
    IBlockState iblockstate = worldIn.getBlockState(pos);
    return iblockstate.getBlock().getMetaFromState(iblockstate);
  }

  @Override
  public void generateTree(World worldIn, BlockPos pos, IBlockState state, Random rand) {
    if (!net.minecraftforge.event.terraingen.TerrainGen.saplingGrowTree(worldIn, rand, pos)) return;

    IBlockState slimeGreen = TinkerWorld.slimeBlockCongealed.getDefaultState().withProperty(BlockSlime.TYPE, BlockSlime.SlimeType.GREEN);
    IBlockState leaves = TinkerWorld.slimeLeaves.getDefaultState().withProperty(BlockSlimeGrass.FOLIAGE, state.getValue(FOLIAGE));
    SlimeTreeGenerator gen = new SlimeTreeGenerator(5, 4, slimeGreen, leaves, null);

    // replace sapling with air
    worldIn.setBlockToAir(pos);

    // try generating
    gen.generateTree(rand, worldIn, pos);

    // check if it generated
    if(worldIn.isAirBlock(pos)) {
      // nope, set sapling again
      worldIn.setBlockState(pos, state, 4);
    }
  }
}
