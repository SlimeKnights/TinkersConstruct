package slimeknights.tconstruct.tables.item;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.NonNullList;
import slimeknights.mantle.item.RetexturedBlockItem;

import java.util.function.BooleanSupplier;

/** Retextured block that conditionally enables show all variants */
public class TableBlockItem extends RetexturedBlockItem {
  private final BooleanSupplier showAllCondition;
  public TableBlockItem(Block block, ITag<Item> textureTag, Properties builder, BooleanSupplier showAllCondition) {
    super(block, textureTag, builder);
    this.showAllCondition = showAllCondition;
  }

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    if (this.isInGroup(group)) {
      addTagVariants(this.getBlock(), this.textureTag, items, showAllCondition.getAsBoolean());
    }
  }
}
