package slimeknights.tconstruct.world.block;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.PlantType;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeGrassBlock.FoliageType;

import org.jetbrains.annotations.Nonnull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Locale;

// todo: evaluate block
public class SlimeTallGrassBlock extends PlantBlock implements IForgeShearable {

  private static final VoxelShape SHAPE = Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

  private final FoliageType foliageType;
  private final SlimePlantType plantType;

  public SlimeTallGrassBlock(Settings properties, FoliageType foliageType, SlimePlantType plantType) {
    super(properties);
    this.foliageType = foliageType;
    this.plantType = plantType;
  }

  @Deprecated
  @Override
  public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
    return SHAPE;
  }

  /**
   * Get the OffsetType for this Block. Determines if the model is rendered slightly offset.
   */
  @NotNull
  @Override
  @Environment(EnvType.CLIENT)
  public Block.OffsetType getOffsetType() {
    return OffsetType.XYZ;
  }

//  /* Forge/MC callbacks */
//  @NotNull
//  @Override
//  public PlantType getPlantType(BlockView world, BlockPos pos) {
//    return TinkerWorld.SLIME_PLANT_TYPE;
//  }

  @Override
  public List<ItemStack> onSheared(@Nullable PlayerEntity player, ItemStack item, World world, BlockPos pos, int fortune) {
    ItemStack stack = new ItemStack(this, 1);
    return Lists.newArrayList(stack);
  }

  @Override
  protected boolean canPlantOnTop(BlockState state, BlockView worldIn, BlockPos pos) {
    Block block = state.getBlock();
    return TinkerWorld.slimeDirt.contains(block) || TinkerWorld.vanillaSlimeGrass.contains(block) || TinkerWorld.earthSlimeGrass.contains(block) || TinkerWorld.skySlimeGrass.contains(block) || TinkerWorld.enderSlimeGrass.contains(block) || TinkerWorld.ichorSlimeGrass.contains(block);
  }

  public FoliageType getFoliageType() {
    return this.foliageType;
  }

  public SlimePlantType getPlantType() {
    return this.plantType;
  }

  @Override
  public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> items) {
    if (this.foliageType != FoliageType.ICHOR) {
      super.addStacksForDisplay(group, items);
    }
  }

  public enum SlimePlantType implements StringIdentifiable {
    TALL_GRASS,
    FERN;

    @Override
    public String asString() {
      return this.toString().toLowerCase(Locale.US);
    }
  }
}
