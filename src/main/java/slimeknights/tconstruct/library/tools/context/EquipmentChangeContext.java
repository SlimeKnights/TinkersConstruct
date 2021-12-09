package slimeknights.tconstruct.library.tools.context;

import lombok.Getter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;

/** Context for equipment change modifier hooks */
public class EquipmentChangeContext extends EquipmentContext {
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

  public EquipmentChangeContext(LivingEntity entity, EquipmentSlotType changedSlot, ItemStack original, ItemStack replacement) {
    super(entity);
    this.changedSlot = changedSlot;
    this.original = original;
    this.replacement = replacement;
    this.originalTool = getToolStackIfModifiable(original);
    int replacementIndex = changedSlot.getIndex();
    toolsInSlots[replacementIndex] = getToolStackIfModifiable(replacement);
    fetchedTool[replacementIndex] = true;
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
