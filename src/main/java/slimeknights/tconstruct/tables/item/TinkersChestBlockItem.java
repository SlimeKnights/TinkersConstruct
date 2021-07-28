package slimeknights.tconstruct.tables.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.mantle.util.TileEntityHelper;
import slimeknights.tconstruct.tables.tileentity.chest.TinkersChestTileEntity;

import javax.annotation.Nullable;

/** Dyeable chest block */
public class TinkersChestBlockItem extends BlockItem implements IDyeableArmorItem {
  public TinkersChestBlockItem(Block blockIn, Properties builder) {
    super(blockIn, builder);
  }

  @Override
  public int getColor(ItemStack stack) {
    CompoundNBT tag = stack.getChildTag("display");
    return tag != null && tag.contains("color", NBT.TAG_ANY_NUMERIC) ? tag.getInt("color") : TinkersChestTileEntity.DEFAULT_COLOR;
  }

  @Override
  protected boolean onBlockPlaced(BlockPos pos, World worldIn, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
    boolean result = super.onBlockPlaced(pos, worldIn, player, stack, state);
    if (hasColor(stack)) {
      int color = getColor(stack);
      TileEntityHelper.getTile(TinkersChestTileEntity.class, worldIn, pos).ifPresent(te -> te.setColor(color));
    }
    return result;
  }
}
