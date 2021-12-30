package slimeknights.tconstruct.tables.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.tables.block.entity.chest.TinkersChestBlockEntity;

public class TinkersChestBlock extends ChestBlock {
  public TinkersChestBlock(Properties builder, BlockEntitySupplier<? extends BlockEntity> be, boolean dropsItems) {
    super(builder, be, dropsItems);
  }

  @Override
  public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
    ItemStack stack = new ItemStack(this);
    BlockEntityHelper.get(TinkersChestBlockEntity.class, world, pos).ifPresent(te -> {
      if (te.hasColor()) {
        ((DyeableLeatherItem) stack.getItem()).setColor(stack, te.getColor());
      }
    });
    return stack;
  }
}
