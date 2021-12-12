package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.impl.InventoryModifier;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;
import java.util.List;

public class ItemFrameModifier extends InventoryModifier {
  /** Pattern and inventory key */
  private static final Pattern ITEM_FRAME = new Pattern(TConstruct.MOD_ID, "item_frame");
  public ItemFrameModifier() {
    super(0x7FB7D7, ITEM_FRAME, 1);
  }

  @Override
  public int getSlotLimit(IModifierToolStack tool, int slot) {
    return 1;
  }

  @Nullable
  @Override
  public Pattern getPattern(IModifierToolStack tool, int level, int slot, boolean hasStack) {
    return hasStack ? null : ITEM_FRAME;
  }

  /** Parses all stacks in NBT into the passed list */
  public void getAllStacks(IModifierToolStack tool, int level, List<ItemStack> stackList) {
    IModDataReadOnly modData = tool.getPersistentData();
    if (modData.contains(ITEM_FRAME, NBT.TAG_LIST)) {
      ListNBT list = tool.getPersistentData().get(ITEM_FRAME, GET_COMPOUND_LIST);
      int max = getSlots(tool, level);

      // make sure the stacks are in order, NBT could store them in any order
      ItemStack[] parsed = new ItemStack[max];
      for (int i = 0; i < list.size(); i++) {
        CompoundNBT compound = list.getCompound(i);
        int slot = compound.getInt(TAG_SLOT);
        if (slot < max) {
          parsed[slot] = ItemStack.read(compound);
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
