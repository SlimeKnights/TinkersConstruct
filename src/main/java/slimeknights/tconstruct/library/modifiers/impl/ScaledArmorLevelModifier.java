package slimeknights.tconstruct.library.modifiers.impl;

import lombok.RequiredArgsConstructor;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Modifier that keeps track of the total level as a scale
 * TODO: move to a module, maybe a registered one?
 */
@RequiredArgsConstructor
public class ScaledArmorLevelModifier extends Modifier implements EquipmentChangeModifierHook {
  private final TinkerDataKey<Float> key;
  private final float scale;
  private final boolean singleUse;

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.EQUIPMENT_CHANGE);
  }

  @Override
  public Component getDisplayName(int level) {
    if (singleUse) {
      return super.getDisplayName();
    }
    return super.getDisplayName(level);
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    ModifierUtil.addTotalArmorModifierFloat(tool, context, key, modifier.getLevel() * scale);
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    ModifierUtil.addTotalArmorModifierFloat(tool, context, key, -modifier.getLevel() * scale);
  }
}
