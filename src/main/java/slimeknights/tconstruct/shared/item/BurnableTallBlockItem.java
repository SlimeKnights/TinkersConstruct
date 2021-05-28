package slimeknights.tconstruct.shared.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TallBlockItem;

public class BurnableTallBlockItem extends TallBlockItem {
  private final int burnTime;
  public BurnableTallBlockItem(Block blockIn, Properties builder, int burnTime) {
    super(blockIn, builder);
    this.burnTime = burnTime;
  }

  @Override
  public int getBurnTime(ItemStack itemStack) {
    return burnTime;
  }
}
