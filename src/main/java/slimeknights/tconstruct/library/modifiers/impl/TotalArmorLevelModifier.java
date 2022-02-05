package slimeknights.tconstruct.library.modifiers.impl;

import lombok.RequiredArgsConstructor;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/** Modifier that keeps track of the total armor level in persistent data */
@RequiredArgsConstructor
public class TotalArmorLevelModifier extends Modifier {
  private final TinkerDataKey<Integer> key;
  private final boolean allowBroken;
  private final boolean singleUse;

  public TotalArmorLevelModifier(TinkerDataKey<Integer> key, boolean singleUse) {
    this(key, singleUse, false);
  }

  public TotalArmorLevelModifier(TinkerDataKey<Integer> key) {
    this(key, false, false);
  }

  @Override
  public Component getDisplayName(int level) {
    if (singleUse) {
      return super.getDisplayName();
    }
    return super.getDisplayName(level);
  }

  @Override
  public void onEquip(IToolStackView tool, int level, EquipmentChangeContext context) {
    ModifierUtil.addTotalArmorModifierLevel(tool, context, key, level, allowBroken);
  }

  @Override
  public void onUnequip(IToolStackView tool, int level, EquipmentChangeContext context) {
    ModifierUtil.addTotalArmorModifierLevel(tool, context, key, -level, allowBroken);
  }
}
