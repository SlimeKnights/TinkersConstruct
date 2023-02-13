package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.InventoryModifier;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

public class ItemFrameModifier extends InventoryModifier {
  /** Pattern and inventory key */
  private static final Pattern ITEM_FRAME = new Pattern(TConstruct.MOD_ID, "item_frame");
  public ItemFrameModifier() {
    super(ITEM_FRAME, 1);
  }

  @Override
  public int getSlotLimit(IToolStackView tool, ModifierEntry modifier, int slot) {
    return 1;
  }

  @Nullable
  @Override
  public Pattern getPattern(IToolStackView tool, ModifierEntry modifier, int slot, boolean hasStack) {
    return hasStack ? null : ITEM_FRAME;
  }

  /** Parses all stacks in NBT into the passed list */
  public void getAllStacks(IToolStackView tool, int level, List<ItemStack> stackList) {
    IModDataView modData = tool.getPersistentData();
    if (modData.contains(ITEM_FRAME, Tag.TAG_LIST)) {
      ListTag list = tool.getPersistentData().get(ITEM_FRAME, GET_COMPOUND_LIST);
      int max = getSlots(tool, level);

      // make sure the stacks are in order, NBT could store them in any order
      ItemStack[] parsed = new ItemStack[max];
      for (int i = 0; i < list.size(); i++) {
        CompoundTag compound = list.getCompound(i);
        int slot = compound.getInt(TAG_SLOT);
        if (slot < max) {
          parsed[slot] = ItemStack.of(compound);
        }
      }
      // add stacks into the list
      for (ItemStack stack : parsed) {
        if (stack != null && !stack.isEmpty()) {
          stackList.add(stack);
        }
      }
    }
  }
}
