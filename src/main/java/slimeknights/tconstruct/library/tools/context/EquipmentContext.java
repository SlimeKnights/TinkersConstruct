package slimeknights.tconstruct.library.tools.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.tools.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;

import static slimeknights.tconstruct.common.TinkerTags.Items.MODIFIABLE;

@RequiredArgsConstructor
public class EquipmentContext {
  /** Entity who changed equipment */
  @Getter
  private final LivingEntity entity;
  /** Determines if the tool in the given slot was fetched */
  protected final boolean[] fetchedTool = new boolean[6];
  /** Array of tools currently on the entity */
  protected final IModifierToolStack[] toolsInSlots = new IModifierToolStack[6];

  /** Gets a tool stack if the stack is modifiable, null otherwise */
  @Nullable
  protected static IModifierToolStack getToolStackIfModifiable(ItemStack stack) {
    if (!stack.isEmpty() && MODIFIABLE.contains(stack.getItem())) {
      return ToolStack.from(stack);
    }
    return null;
  }

  /**
   * Gets the tool stack in the given slot
   * @param slotType  Slot type
   * @return  Tool stack in the given slot, or null if the slot is not modifiable
   */
  @Nullable
  public IModifierToolStack getToolInSlot(EquipmentSlotType slotType) {
    int index = slotType.getSlotIndex();
    if (!fetchedTool[index]) {
      toolsInSlots[index] = getToolStackIfModifiable(entity.getItemStackFromSlot(slotType));
      fetchedTool[index] = true;
    }
    return toolsInSlots[index];
  }

  /** Checks if any of the armor items are modifiable */
  public boolean hasModifiableArmor() {
    for (EquipmentSlotType slotType : ModifiableArmorMaterial.ARMOR_SLOTS) {
      if (getToolInSlot(slotType) != null) {
        return true;
      }
    }
    return false;
  }
}
