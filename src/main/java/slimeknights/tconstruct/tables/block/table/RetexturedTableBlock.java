package slimeknights.tconstruct.tables.block.table;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import slimeknights.mantle.block.RetexturedBlock;
import slimeknights.tconstruct.tables.block.TinkerTableBlock;

import javax.annotation.Nullable;

public abstract class RetexturedTableBlock extends TinkerTableBlock {
  public RetexturedTableBlock(Settings builder) {
    super(builder);
  }

  @Override
  public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    super.onPlaced(world, pos, state, placer, stack);
    RetexturedBlock.updateTextureBlock(world, pos, stack);
  }

  @Override
  public ItemStack getPickBlock(BlockState state, HitResult target, BlockView world, BlockPos pos, PlayerEntity player) {
    return RetexturedBlock.getPickBlock(world, pos, state);
  }
}
