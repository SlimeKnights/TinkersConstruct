package slimeknights.tconstruct.tools.modifiers.defense;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.data.ModifierMaxLevel;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/** Base class for protection modifiers that want to keep track of the largest level for a bonus */
@RequiredArgsConstructor
public abstract class AbstractProtectionModifier<T extends ModifierMaxLevel> extends IncrementalModifier implements EquipmentChangeModifierHook {
  private final TinkerDataKey<T> key;

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.EQUIPMENT_CHANGE);
  }

  /** Creates a new data instance */
  protected abstract T createData(EquipmentChangeContext context);

  /** Called when the last piece of equipment is removed to reset the data */
  protected void reset(T data, EquipmentChangeContext context) {}

  /** Called to apply updates to the piece */
  protected void set(T data, EquipmentSlot slot, float scaledLevel, EquipmentChangeContext context) {
    data.set(slot, scaledLevel);
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    LivingEntity entity = context.getEntity();
    EquipmentSlot slot = context.getChangedSlot();
    if (ModifierUtil.validArmorSlot(tool, slot) && !entity.level.isClientSide) {
      context.getTinkerData().ifPresent(data -> {
        T modData = data.get(key);
        if (modData != null) {
          set(modData, slot, 0, context);
          if (modData.getMax() == 0) {
            reset(modData, context);
          }
        }
      });
    }
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    LivingEntity entity = context.getEntity();
    EquipmentSlot slot = context.getChangedSlot();
    if (!entity.level.isClientSide && ModifierUtil.validArmorSlot(tool, slot) && !tool.isBroken()) {
      float scaledLevel = modifier.getEffectiveLevel(tool);
      context.getTinkerData().ifPresent(data -> {
        T modData = data.get(key);
        if (modData == null) {
          // not calculated yet? add all vanilla values to the tracker
          modData = createData(context);
          data.put(key, modData);
        }
        // add ourself to the data
        set(modData, slot, scaledLevel, context);
      });
    }
  }
}
