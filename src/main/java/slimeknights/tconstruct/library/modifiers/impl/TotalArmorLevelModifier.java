package slimeknights.tconstruct.library.modifiers.impl;

import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class TotalArmorLevelModifier extends Modifier {
  private final TinkerDataKey<Integer> key;
  private final boolean allowBroken;
  private final boolean singleUse;
  public TotalArmorLevelModifier(int color, TinkerDataKey<Integer> key, boolean singleUse, boolean allowBroken) {
    super(color);
    this.key = key;
    this.singleUse = singleUse;
    this.allowBroken = allowBroken;
  }

  public TotalArmorLevelModifier(int color, TinkerDataKey<Integer> key, boolean singleUse) {
    this(color, key, singleUse, false);
  }

  public TotalArmorLevelModifier(int color, TinkerDataKey<Integer> key) {
    this(color, key, false, false);
  }

  @Override
  public ITextComponent getDisplayName(int level) {
    if (singleUse) {
      return super.getDisplayName();
    }
    return super.getDisplayName(level);
  }

  @Override
  public void onEquip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    ModifierUtil.addTotalArmorModifierLevel(tool, context, key, level, allowBroken);
  }

  @Override
  public void onUnequip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    ModifierUtil.addTotalArmorModifierLevel(tool, context, key, -level, allowBroken);
  }
}
