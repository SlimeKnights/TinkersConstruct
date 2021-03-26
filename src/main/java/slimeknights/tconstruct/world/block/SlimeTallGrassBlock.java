package slimeknights.tconstruct.world.block;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.PlantType;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeGrassBlock.FoliageType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

// todo: evaluate block
public class SlimeTallGrassBlock extends BushBlock implements IForgeShearable {

  private static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

  private final SlimeGrassBlock.FoliageType foliageType;
  private final SlimePlantType plantType;

  public SlimeTallGrassBlock(Properties properties, SlimeGrassBlock.FoliageType foliageType, SlimePlantType plantType) {
    super(properties);
    this.foliageType = foliageType;
    this.plantType = plantType;
  }

  @Deprecated
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
    return TinkerWorld.SLIME_PLANT_TYPE;
  }

  @Override
  public List<ItemStack> onSheared(@Nullable PlayerEntity player, ItemStack item, World world, BlockPos pos, int fortune) {
    ItemStack stack = new ItemStack(this, 1);
    return Lists.newArrayList(stack);
  }

  @Override
  protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
    Block block = state.getBlock();
    return TinkerWorld.slimeDirt.contains(block) || TinkerWorld.vanillaSlimeGrass.contains(block) || TinkerWorld.earthSlimeGrass.contains(block) || TinkerWorld.skySlimeGrass.contains(block) || TinkerWorld.enderSlimeGrass.contains(block) || TinkerWorld.ichorSlimeGrass.contains(block);
  }

  public SlimeGrassBlock.FoliageType getFoliageType() {
    return this.foliageType;
  }

  public SlimePlantType getPlantType() {
    return this.plantType;
  }

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    if (this.foliageType != FoliageType.ICHOR) {
      super.fillItemGroup(group, items);
    }
  }

  public enum SlimePlantType implements IStringSerializable {
    TALL_GRASS,
    FERN;

    @Override
    public String getString() {
      return this.toString().toLowerCase(Locale.US);
    }
  }
}
