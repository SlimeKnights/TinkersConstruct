package slimeknights.tconstruct.library.modifiers.impl;

import lombok.RequiredArgsConstructor;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/** Cross of {@link IncrementalModifier} and {@link TotalArmorLevelModifier} */
@RequiredArgsConstructor
public class IncrementalArmorLevelModifier extends IncrementalModifier {
  private final TinkerDataKey<Float> key;

  @Override
  public void onEquip(IToolStackView tool, int level, EquipmentChangeContext context) {
    ModifierUtil.addTotalArmorModifierFloat(tool, context, key, getScaledLevel(tool, level));
  }

  @Override
  public void onUnequip(IToolStackView tool, int level, EquipmentChangeContext context) {
    ModifierUtil.addTotalArmorModifierFloat(tool, context, key, -getScaledLevel(tool, level));
  }
}
