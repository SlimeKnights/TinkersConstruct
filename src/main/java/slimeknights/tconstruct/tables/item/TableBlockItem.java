package slimeknights.tconstruct.tables.item;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.Tag;
import net.minecraft.core.NonNullList;
import slimeknights.mantle.item.RetexturedBlockItem;

import java.util.function.BooleanSupplier;

import net.minecraft.world.item.Item.Properties;

/** Retextured block that conditionally enables show all variants */
public class TableBlockItem extends RetexturedBlockItem {
  private final BooleanSupplier showAllCondition;
  public TableBlockItem(Block block, Tag<Item> textureTag, Properties builder, BooleanSupplier showAllCondition) {
    super(block, textureTag, builder);
    this.showAllCondition = showAllCondition;
  }

  @Override
  public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
    if (this.allowdedIn(group)) {
      addTagVariants(this.getBlock(), this.textureTag, items, showAllCondition.getAsBoolean());
    }
  }
}
