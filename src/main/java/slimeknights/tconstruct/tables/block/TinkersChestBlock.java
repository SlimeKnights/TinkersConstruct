package slimeknights.tconstruct.tables.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.tables.tileentity.chest.TinkersChestTileEntity;

import java.util.function.Supplier;

public class TinkersChestBlock extends ChestBlock {
  public TinkersChestBlock(Properties builder, Supplier<? extends TileEntity> te, boolean dropsItems) {
    super(builder, te, dropsItems);
  }

  @Override
  public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
    ItemStack stack = new ItemStack(this);
    TileEntityHelper.getTile(TinkersChestTileEntity.class, world, pos).ifPresent(te -> {
      if (te.hasColor()) {
        ((IDyeableArmorItem) stack.getItem()).setColor(stack, te.getColor());
      }
    });
    return stack;
  }
}
