package slimeknights.tconstruct.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.BlockSlime;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.BlockSlimeGrass.FoliageType;
import slimeknights.tconstruct.world.worldgen.SlimeTreeGenerator;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockSlimeSapling extends BlockSapling {

  public static PropertyEnum<FoliageType> FOLIAGE = BlockSlimeGrass.FOLIAGE;

  public BlockSlimeSapling() {
    setCreativeTab(TinkerRegistry.tabWorld);
    setDefaultState(this.blockState.getBaseState());
    this.setSoundType(SoundType.PLANT);
  }

  @Override
  public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
    for(FoliageType type : FoliageType.values()) {
      list.add(new ItemStack(this, 1, getMetaFromState(getDefaultState().withProperty(FOLIAGE, type))));
    }
  }

  @Nonnull
  @Override
  protected BlockStateContainer createBlockState() {
    // TYPE has to be included because of the BlockSapling constructor.. but it's never used.
    return new BlockStateContainer(this, FOLIAGE, STAGE, TYPE);
  }

  /**
   * Convert the given metadata into a BlockState for this Block
   */
  @Nonnull
  @Override
  public IBlockState getStateFromMeta(int meta) {
    if(meta < 0 || meta >= BlockSlimeGrass.FoliageType.values().length) {
      meta = 0;
    }
    BlockSlimeGrass.FoliageType grass = BlockSlimeGrass.FoliageType.values()[meta];
    return this.getDefaultState().withProperty(BlockSlimeGrass.FOLIAGE, grass);
  }

  /**
   * Convert the BlockState into the correct metadata value
   */
  @Override
  public int getMetaFromState(IBlockState state) {
    return state.getValue(BlockSlimeGrass.FOLIAGE).ordinal();
  }

  @Override
  public int damageDropped(IBlockState state) {
    return getMetaFromState(state);
  }

  @Override
  public boolean isReplaceable(IBlockAccess worldIn, @Nonnull BlockPos pos) {
    return false;
  }

  @Override
  public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
    Block ground = worldIn.getBlockState(pos.down()).getBlock();
    return ground == TinkerWorld.slimeGrass || ground == TinkerWorld.slimeDirt;
  }

  @Nonnull
  @Override
  public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
    return TinkerWorld.slimePlantType;
  }

  @Nonnull
  @Override
  public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
    IBlockState iblockstate = world.getBlockState(pos);
    int meta = iblockstate.getBlock().getMetaFromState(iblockstate);
    return new ItemStack(Item.getItemFromBlock(this), 1, meta);
  }

  @Override
  public void generateTree(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rand) {
    if(!net.minecraftforge.event.terraingen.TerrainGen.saplingGrowTree(worldIn, rand, pos)) {
      return;
    }

    BlockSlime.SlimeType slimeType = BlockSlime.SlimeType.GREEN;
    if(state.getValue(FOLIAGE) == FoliageType.ORANGE) {
      slimeType = BlockSlime.SlimeType.MAGMA;
    }

    IBlockState slimeGreen = TinkerCommons.blockSlimeCongealed.getDefaultState().withProperty(BlockSlime.TYPE, slimeType);
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
