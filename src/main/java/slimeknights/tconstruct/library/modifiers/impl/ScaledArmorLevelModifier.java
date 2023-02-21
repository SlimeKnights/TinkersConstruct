package slimeknights.tconstruct.library.modifiers.impl;

import lombok.RequiredArgsConstructor;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/** Modifier that keeps track of the total level as a scale */
@RequiredArgsConstructor
public class ScaledArmorLevelModifier extends Modifier {
  private final TinkerDataKey<Float> key;
  private final float scale;
  private final boolean singleUse;

  @Override
  public Component getDisplayName(int level) {
    if (singleUse) {
      return super.getDisplayName();
    }
    return super.getDisplayName(level);
  }

  @Override
  public void onEquip(IToolStackView tool, int level, EquipmentChangeContext context) {
    ModifierUtil.addTotalArmorModifierFloat(tool, context, key, level * scale);
  }

  @Override
  public void onUnequip(IToolStackView tool, int level, EquipmentChangeContext context) {
    ModifierUtil.addTotalArmorModifierFloat(tool, context, key, -level * scale);
  }
}
