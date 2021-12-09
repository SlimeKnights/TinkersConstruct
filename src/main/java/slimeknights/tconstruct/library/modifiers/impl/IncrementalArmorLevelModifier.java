package slimeknights.tconstruct.library.modifiers.impl;

import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class IncrementalArmorLevelModifier extends IncrementalModifier {
  private final TinkerDataKey<Float> key;
  public IncrementalArmorLevelModifier(int color, TinkerDataKey<Float> key) {
    super(color);
    this.key = key;
  }

  @Override
  public void onEquip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    ModifierUtil.addTotalArmorModifierFloat(tool, context, key, getScaledLevel(tool, level));
  }

  @Override
  public void onUnequip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    ModifierUtil.addTotalArmorModifierFloat(tool, context, key, -getScaledLevel(tool, level));
  }
}
