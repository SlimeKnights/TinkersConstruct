package slimeknights.tconstruct.library.tools.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
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
  protected final IToolStackView[] toolsInSlots = new IToolStackView[6];
  /** Cached tinker data capability, saves capability lookup times slightly */
  private LazyOptional<TinkerDataCapability.Holder> tinkerData = null;

  /** Gets a tool stack if the stack is modifiable, null otherwise */
  @Nullable
  protected static IToolStackView getToolStackIfModifiable(ItemStack stack) {
    if (!stack.isEmpty() && stack.is(MODIFIABLE)) {
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
  public IToolStackView getToolInSlot(EquipmentSlot slotType) {
    int index = slotType.getFilterFlag();
    if (!fetchedTool[index]) {
      toolsInSlots[index] = getToolStackIfModifiable(entity.getItemBySlot(slotType));
      fetchedTool[index] = true;
    }
    return toolsInSlots[index];
  }

  /** Checks if any of the armor items are modifiable */
  public boolean hasModifiableArmor() {
    for (EquipmentSlot slotType : EquipmentSlot.values()) {
      if (ModifierUtil.validArmorSlot(entity, slotType) && getToolInSlot(slotType) != null) {
        return true;
      }
    }
    return false;
  }

  /** Gets the tinker data capability */
  public LazyOptional<TinkerDataCapability.Holder> getTinkerData() {
    if (tinkerData == null) {
      tinkerData = entity.getCapability(TinkerDataCapability.CAPABILITY);
    }
    return tinkerData;
  }
}
