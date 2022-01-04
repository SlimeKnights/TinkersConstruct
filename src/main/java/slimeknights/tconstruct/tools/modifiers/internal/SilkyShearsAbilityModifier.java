package slimeknights.tconstruct.tools.modifiers.internal;

import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class SilkyShearsAbilityModifier extends ShearsAbilityModifier {

  public SilkyShearsAbilityModifier(int color, int range, int priority) {
    super(color, range, priority);
  }
  
  @Override
  protected boolean isShears(IToolStackView tool) {
    return tool.getModifierLevel(TinkerModifiers.silky.get()) > 0;
  }

}
