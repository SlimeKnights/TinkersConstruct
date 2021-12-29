package slimeknights.tconstruct.library.tools.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
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
  /** Cached tinker data capability, saves capability lookup times slightly */
  private LazyOptional<TinkerDataCapability.Holder> tinkerData = null;

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
  public IModifierToolStack getToolInSlot(EquipmentSlot slotType) {
    int index = slotType.getFilterFlag();
    if (!fetchedTool[index]) {
      toolsInSlots[index] = getToolStackIfModifiable(entity.getItemBySlot(slotType));
      fetchedTool[index] = true;
    }
    return toolsInSlots[index];
  }

  /** Checks if any of the armor items are modifiable */
  public boolean hasModifiableArmor() {
    for (EquipmentSlot slotType : ModifiableArmorMaterial.ARMOR_SLOTS) {
      if (getToolInSlot(slotType) != null) {
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
