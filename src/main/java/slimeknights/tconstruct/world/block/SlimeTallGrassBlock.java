package slimeknights.tconstruct.world.block;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
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
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;

public class SlimeTallGrassBlock extends BushBlock implements IShearable {

  protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

  private final SlimeGrassBlock.FoliageType foliageType;
  private final SlimePlantType plantType;

  public SlimeTallGrassBlock(SlimeGrassBlock.FoliageType foliageType, SlimePlantType plantType) {
    super(Block.Properties.create(Material.PLANTS).sound(SoundType.PLANT).doesNotBlockMovement().hardnessAndResistance(0.0F).tickRandomly());
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
    return block == TinkerWorld.green_slime_dirt || block == TinkerWorld.blue_slime_dirt || block == TinkerWorld.purple_slime_dirt || block == TinkerWorld.magma_slime_dirt || block == TinkerWorld.blue_vanilla_slime_grass || block == TinkerWorld.purple_vanilla_slime_grass || block == TinkerWorld.orange_vanilla_slime_grass || block == TinkerWorld.blue_green_slime_grass || block == TinkerWorld.purple_green_slime_grass || block == TinkerWorld.orange_green_slime_grass || block == TinkerWorld.blue_blue_slime_grass || block == TinkerWorld.purple_blue_slime_grass || block == TinkerWorld.orange_blue_slime_grass || block == TinkerWorld.blue_purple_slime_grass || block == TinkerWorld.purple_purple_slime_grass || block == TinkerWorld.orange_purple_slime_grass || block == TinkerWorld.blue_magma_slime_grass || block == TinkerWorld.purple_magma_slime_grass || block == TinkerWorld.orange_magma_slime_grass;
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
