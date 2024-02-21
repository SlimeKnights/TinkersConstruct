package slimeknights.tconstruct.library.modifiers.hook.interaction;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Collection;
import java.util.List;

/** Hook that runs while the tool is in the inventory */
public interface InventoryTickModifierHook {
  /**
   * Called when the stack updates in the player inventory
   * @param tool           Current tool instance
   * @param modifier       Modifier running the hook
   * @param world          World containing tool
   * @param holder         Entity holding tool
   * @param itemSlot       Slot containing this tool. Note this may be from the hotbar, main inventory, or armor inventory
   * @param isSelected     If true, this item is currently in the player's main hand
   * @param isCorrectSlot  If true, this item is in the proper slot. For tools, that is main hand or off hand. For armor, this means its in the correct armor slot
   * @param stack          Item stack instance to check other slots for the tool. Do not modify
   */
  void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack);

  /**
   * Handles ticking a modifiable item that works when held. Armor uses different logic
   * @param stack       Modifiable stack
   * @param worldIn     World instance
   * @param entityIn    Entity holding the tool
   * @param itemSlot    Slot with the tool
   * @param isSelected  If true, the tool is selected
   */
  static void heldInventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    // don't care about non-living, they skip most tool context
    if (entityIn instanceof LivingEntity) {
      ToolStack tool = ToolStack.from(stack);
      if (!worldIn.isClientSide) {
        tool.ensureHasData();
      }
      List<ModifierEntry> modifiers = tool.getModifierList();
      if (!modifiers.isEmpty()) {
        LivingEntity living = (LivingEntity) entityIn;
        // we pass in the stack for most custom context, but for the sake of armor it is easier to tell them that this is the correct slot for effects
        boolean isHeld = isSelected || living.getOffhandItem() == stack;
        for (ModifierEntry entry : modifiers) {
          entry.getHook(TinkerHooks.INVENTORY_TICK).onInventoryTick(tool, entry, worldIn, living, itemSlot, isSelected, isHeld, stack);
        }
      }
    }
  }

  /** Merger that runs all hooks one after another */
  record AllMerger(Collection<InventoryTickModifierHook> modules) implements InventoryTickModifierHook {
    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
      for (InventoryTickModifierHook module : modules) {
        module.onInventoryTick(tool, modifier, world, holder, itemSlot, isSelected, isCorrectSlot, stack);
      }
    }
  }
}
