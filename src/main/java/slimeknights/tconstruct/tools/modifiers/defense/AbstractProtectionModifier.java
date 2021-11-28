package slimeknights.tconstruct.tools.modifiers.defense;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.logic.ModifierMaxLevel;

/** Base class for protection modifiers that want to keep track of the largest level for a bonus */
public abstract class AbstractProtectionModifier<T extends ModifierMaxLevel> extends IncrementalModifier {
  private final TinkerDataKey<T> key;
  public AbstractProtectionModifier(int color, TinkerDataKey<T> key) {
    super(color);
    this.key = key;
  }

  /** Creates a new data instance */
  protected abstract T createData();

  /** Called when the last piece of equipment is removed to reset the data */
  protected void reset(T data) {}

  @Override
  public void onUnequip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    LivingEntity entity = context.getEntity();
    EquipmentSlotType slot = context.getChangedSlot();
    if (slot.getSlotType() == Group.ARMOR && !entity.getEntityWorld().isRemote) {
      entity.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
        T modData = data.get(key);
        if (modData != null) {
          modData.set(slot, 0);
          if (modData.getMax() == 0) {
            reset(modData);
          }
        }
      });
    }
  }

  @Override
  public void onEquip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    LivingEntity entity = context.getEntity();
    EquipmentSlotType slot = context.getChangedSlot();
    if (!entity.getEntityWorld().isRemote && slot.getSlotType() == Group.ARMOR && !tool.isBroken()) {
      float scaledLevel = getScaledLevel(tool, level);
      entity.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
        T modData = data.get(key);
        if (modData == null) {
          // not calculated yet? add all vanilla values to the tracker
          modData = createData();
          data.put(key, modData);
        }
        // add ourself to the data
        modData.set(slot, scaledLevel);
      });
    }
  }
}
