package slimeknights.tconstruct.library.tools.context;

import lombok.Getter;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/** Context for equipment change modifier hooks */
public class EquipmentChangeContext extends EquipmentContext {
  /** Slot that changed */
  @Getter
  private final EquipmentSlot changedSlot;
  /** Original stack in the slot */
  @Getter
  private final ItemStack original;
  /** Replacement stack in the slot */
  @Getter
  private final ItemStack replacement;
  /** Original tool in the slot, null if the slot does not contain a modifiable item */
  @Nullable @Getter
  private final IToolStackView originalTool;

  public EquipmentChangeContext(LivingEntity entity, EquipmentSlot changedSlot, ItemStack original, ItemStack replacement) {
    super(entity);
    this.changedSlot = changedSlot;
    this.original = original;
    this.replacement = replacement;
    this.originalTool = getToolStackIfModifiable(original);
    int replacementIndex = changedSlot.getFilterFlag();
    toolsInSlots[replacementIndex] = getToolStackIfModifiable(replacement);
    fetchedTool[replacementIndex] = true;
  }

  /**
   * Gets the tool stack for the stack replacing the original
   * @return  Tool stack replacing, or null if the slot is not modifable
   */
  @Nullable
  public IToolStackView getReplacementTool() {
    return getToolInSlot(changedSlot);
  }
}
