package slimeknights.tconstruct.tools.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.List;

import javax.annotation.Nonnull;

import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.mantle.util.LocUtils;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.smeltery.ICast;
import slimeknights.tconstruct.library.tools.IPattern;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.shared.tileentity.TileTable;
import slimeknights.tconstruct.tools.common.block.BlockToolTable;

public class ItemBlockTable extends ItemBlockMeta {

  public ItemBlockTable(Block block) {
    super(block);
  }

  @Override
  public void addInformation(@Nonnull ItemStack stack, @Nonnull EntityPlayer playerIn, @Nonnull List<String> tooltip, boolean advanced) {
    super.addInformation(stack, playerIn, tooltip, advanced);
    if(!stack.hasTagCompound()) {
      return;
    }

    ItemStack legs = getLegStack(stack);
    if(legs != null) {
      tooltip.add(legs.getDisplayName());
    }

    if(stack.getTagCompound().hasKey("inventory")) {
      this.addInventoryInformation(stack, playerIn, tooltip, advanced);
    }
  }

  /**
   * Gets the itemstack that determines the leg's texture from the table
   * @param table  Input table
   * @return  The itemstack determining the leg's texture, or null if none exists
   */
  public static ItemStack getLegStack(ItemStack table) {
    NBTTagCompound tag = TagUtil.getTagSafe(table).getCompoundTag(TileTable.FEET_TAG);
    ItemStack stack = ItemStack.loadItemStackFromNBT(tag);

    // don't use a stack with a null item
    if(stack == null || stack.getItem() == null) {
      return null;
    }

    return stack;
  }

  protected void addInventoryInformation(@Nonnull ItemStack stack, @Nonnull EntityPlayer playerIn, @Nonnull List<String> tooltip, boolean advanced) {
    // get the inventory, so we can inspect its items
    NBTTagCompound inventory = stack.getTagCompound().getCompoundTag("inventory");

    if (!inventory.hasKey("Items")) {
      return;
    }

    NBTTagList items = inventory.getTagList("Items", 10);

    if(items.hasNoTags()) {
      return; // Items tag list with no items?!
    }

    // check if it's a PatternChest (damage value corresponds to TableTypes enum)
    if (BlockToolTable.TableTypes.fromMeta(stack.getItemDamage()) == BlockToolTable.TableTypes.PatternChest) {
      // determine if it holds casts or patterns
      String desc = null;

      for (int i = 0; i < items.tagCount(); ++i) {
        // iterate the item stacks, until we find a valid item
        ItemStack inventoryStack = ItemStack.loadItemStackFromNBT(items.getCompoundTagAt(i));

        if (inventoryStack == null) {
          continue; // unable to load any item for this NBT tag - assume invalid/removed item, so check next
        }

        Item item = inventoryStack.getItem();

        if (item instanceof ICast || item instanceof IPattern) {
          desc = ((item instanceof ICast) ? "tooltip.patternchest.holds_casts" : "tooltip.patternchest.holds_patterns");
          break; // found the first valid Item, break loop
        }
      }

      if (desc != null) {
        tooltip.addAll(LocUtils.getTooltips(Util.translateFormatted(desc, items.tagCount())));
      }
    }
    else {
      // generic chest, only show count
      tooltip.addAll(LocUtils.getTooltips(Util.translateFormatted("tooltip.chest.has_items", items.tagCount())));
    }
  }
}
