package slimeknights.tconstruct.world.block;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.PlantType;
import slimeknights.tconstruct.blocks.WorldBlocks;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;

// todo: evaluate block
public class SlimeTallGrassBlock extends BushBlock implements IShearable {

  protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

  private final SlimeGrassBlock.FoliageType foliageType;
  private final SlimePlantType plantType;

  public SlimeTallGrassBlock(Properties properties, SlimeGrassBlock.FoliageType foliageType, SlimePlantType plantType) {
    super(properties);
    this.foliageType = foliageType;
    this.plantType = plantType;
  }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return SHAPE;
  }

  /**
   * Get the OffsetType for this Block. Determines if the model is rendered slightly offset.
   */
  @Nonnull
  @Override
  @OnlyIn(Dist.CLIENT)
  public Block.OffsetType getOffsetType() {
    return Block.OffsetType.XYZ;
  }

  /* Forge/MC callbacks */
  @Nonnull
  @Override
  public PlantType getPlantType(IBlockReader world, BlockPos pos) {
    return TinkerWorld.slimePlantType;
  }

  @Override
  public boolean isShearable(@Nonnull ItemStack item, IWorldReader world, BlockPos pos) {
    return true;
  }

  @Override
  public List<ItemStack> onSheared(@Nonnull ItemStack item, IWorld world, BlockPos pos, int fortune) {
    ItemStack stack = new ItemStack(this, 1);
    return Lists.newArrayList(stack);
  }

  @Override
  protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
    Block block = state.getBlock();
    return WorldBlocks.slime_dirt.contains(block) || WorldBlocks.vanilla_slime_grass.contains(block) || WorldBlocks.green_slime_grass.contains(block) || WorldBlocks.blue_slime_grass.contains(block) || WorldBlocks.purple_slime_grass.contains(block) || WorldBlocks.magma_slime_grass.contains(block);
  }

  public SlimeGrassBlock.FoliageType getFoliageType() {
    return this.foliageType;
  }

  public SlimePlantType getPlantType() {
    return this.plantType;
  }

  public enum SlimePlantType implements IStringSerializable {
    TALL_GRASS,
    FERN;

    @Override
    public String getName() {
      return this.toString().toLowerCase(Locale.US);
    }
  }
}
