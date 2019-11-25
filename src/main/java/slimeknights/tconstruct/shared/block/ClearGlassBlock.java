package slimeknights.tconstruct.shared.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import slimeknights.mantle.block.ConnectedTextureBlock;

public class ClearGlassBlock extends ConnectedTextureBlock {

  public ClearGlassBlock(Properties properties) {
    super(properties);
  }

  @Override
  public BlockRenderLayer getRenderLayer() {
    return BlockRenderLayer.CUTOUT;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
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
}
