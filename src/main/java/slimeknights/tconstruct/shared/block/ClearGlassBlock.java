package slimeknights.tconstruct.shared.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import slimeknights.mantle.block.ConnectedTextureBlock;

import javax.annotation.Nullable;

public class ClearGlassBlock extends ConnectedTextureBlock {

  public ClearGlassBlock() {
    super(Block.Properties.create(Material.GLASS).hardnessAndResistance(0.3F).sound(SoundType.GLASS));
  }

  @Override
  public BlockRenderLayer getRenderLayer() {
    return BlockRenderLayer.CUTOUT;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public float func_220080_a(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return 1.0F;
  }

  @Override
  public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
    return true;
  }

  @Override
  public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return false;
  }

  @Override
  public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return false;
  }

  @Override
  public boolean canEntitySpawn(BlockState state, IBlockReader worldIn, BlockPos pos, EntityType<?> type) {
    return false;
  }

  @Nullable
  @Override
  //TODO: Replace when forge Re-Evaluates
  public net.minecraftforge.common.ToolType getHarvestTool(BlockState state) {
    return ToolType.PICKAXE;
  }

  @Override
  //TODO: Replace when forge Re-Evaluates
  public int getHarvestLevel(BlockState state) {
    return -1;
  }
}
