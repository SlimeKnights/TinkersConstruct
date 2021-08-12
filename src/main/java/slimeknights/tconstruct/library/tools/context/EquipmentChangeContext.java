package slimeknights.tconstruct.library.tools.context;

import lombok.Getter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;

import static slimeknights.tconstruct.common.TinkerTags.Items.MODIFIABLE;

/** Context for equipment change modifier hooks */
public class EquipmentChangeContext {
  /** Entity who changed equipment */
  @Getter
  private final LivingEntity entity;
  /** Slot that changed */
  @Getter
  private final EquipmentSlotType changedSlot;
  /** Original stack in the slot */
  @Getter
  private final ItemStack original;
  /** Replacement stack in the slot */
  @Getter
  private final ItemStack replacement;
  /** Original tool in the slot, null if the slot does not contain a modifiable item */
  @Nullable @Getter
  private final IModifierToolStack originalTool;
  /** Array of tools currently on the entity */
  private final IModifierToolStack[] toolsInSlots = new IModifierToolStack[6];

  /** Gets a tool stack if the stack is modifiable, null otherwise */
  @Nullable
  private static IModifierToolStack getToolStackIfModifiable(ItemStack stack) {
    if (!stack.isEmpty() && MODIFIABLE.contains(stack.getItem())) {
      return ToolStack.from(stack);
    }
    return null;
  }

  public EquipmentChangeContext(LivingEntity entity, EquipmentSlotType changedSlot, ItemStack original, ItemStack replacement) {
    this.entity = entity;
    this.changedSlot = changedSlot;
    this.original = original;
    this.replacement = replacement;
    this.originalTool = getToolStackIfModifiable(original);
    for (EquipmentSlotType slotType : EquipmentSlotType.values()) {
      int index = slotType.getSlotIndex();
      if (slotType == changedSlot) {
        toolsInSlots[index] = getToolStackIfModifiable(replacement);
      } else {
        toolsInSlots[index] = getToolStackIfModifiable(entity.getItemStackFromSlot(slotType));
      }
    }
  }

  /**
   * Gets the tool stack in the given slot
   * @param slotType  Slot type
   * @return  Tool stack in the given slot, or null if the slot is not modifiable
   */
  @Nullable
  public IModifierToolStack getToolInSlot(EquipmentSlotType slotType) {
    return toolsInSlots[slotType.getSlotIndex()];
  }

  /**
   * Gets the tool stack for the stack replacing the original
   * @return  Tool stack replacing, or null if the slot is not modifable
   */
  @Nullable
  public IModifierToolStack getReplacementTool() {
    return getToolInSlot(changedSlot);
  }
}
