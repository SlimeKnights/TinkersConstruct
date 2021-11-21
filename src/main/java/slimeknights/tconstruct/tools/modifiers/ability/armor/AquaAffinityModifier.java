package slimeknights.tconstruct.tools.modifiers.ability.armor;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class AquaAffinityModifier extends SingleUseModifier {
  public static final TinkerDataKey<Integer> AQUA_AFFINITY = TConstruct.createKey("aqua_affinity");
  public AquaAffinityModifier() {
    super(0x3FA442);
  }

  @Override
  public void onEquip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    ModifierUtil.addTotalArmorModifierLevel(tool, context, AQUA_AFFINITY, 1);
  }

  @Override
  public void onUnequip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    ModifierUtil.addTotalArmorModifierLevel(tool, context, AQUA_AFFINITY, -1);
  }
}
