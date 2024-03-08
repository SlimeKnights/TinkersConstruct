package slimeknights.tconstruct.library.modifiers.impl;

import lombok.RequiredArgsConstructor;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Cross of {@link IncrementalModifier} and {@link TotalArmorLevelModifier}
 * TODO: move to a module, maybe a registered one?
 */
@RequiredArgsConstructor
public class IncrementalArmorLevelModifier extends IncrementalModifier implements EquipmentChangeModifierHook {
  private final TinkerDataKey<Float> key;

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.EQUIPMENT_CHANGE);
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    ModifierUtil.addTotalArmorModifierFloat(tool, context, key, modifier.getEffectiveLevel(tool));
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    ModifierUtil.addTotalArmorModifierFloat(tool, context, key, -modifier.getEffectiveLevel(tool));
  }
}
