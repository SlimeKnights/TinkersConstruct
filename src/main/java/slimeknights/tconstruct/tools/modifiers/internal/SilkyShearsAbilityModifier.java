package slimeknights.tconstruct.tools.modifiers.internal;

import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class SilkyShearsAbilityModifier extends ShearsAbilityModifier {

  public SilkyShearsAbilityModifier(int color, int range, int priority) {
    super(color, range, priority);
  }
  
  @Override
  protected boolean isShears(IModifierToolStack tool) {
    return tool.getModifierLevel(TinkerModifiers.silky.get()) > 0;
  }

}
