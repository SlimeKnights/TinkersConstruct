package slimeknights.tconstruct.tables.item;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.tables.block.entity.chest.TinkersChestBlockEntity;

import javax.annotation.Nullable;

/** Dyeable chest block */
public class TinkersChestBlockItem extends BlockItem implements DyeableLeatherItem {
  public TinkersChestBlockItem(Block blockIn, Properties builder) {
    super(blockIn, builder);
  }

  @Override
  public int getColor(ItemStack stack) {
    CompoundTag tag = stack.getTagElement("display");
    return tag != null && tag.contains("color", Tag.TAG_ANY_NUMERIC) ? tag.getInt("color") : TinkersChestBlockEntity.DEFAULT_COLOR;
  }

  @Override
  protected boolean updateCustomBlockEntityTag(BlockPos pos, Level worldIn, @Nullable Player player, ItemStack stack, BlockState state) {
    boolean result = super.updateCustomBlockEntityTag(pos, worldIn, player, stack, state);
    if (hasCustomColor(stack)) {
      int color = getColor(stack);
      BlockEntityHelper.get(TinkersChestBlockEntity.class, worldIn, pos).ifPresent(te -> te.setColor(color));
    }
    return result;
  }
}
