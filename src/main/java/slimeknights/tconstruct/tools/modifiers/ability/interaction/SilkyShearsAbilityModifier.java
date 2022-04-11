package slimeknights.tconstruct.tools.modifiers.ability.interaction;

import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class SilkyShearsAbilityModifier extends ShearsAbilityModifier {
  public SilkyShearsAbilityModifier(int range, int priority) {
    super(range, priority);
  }
  
  @Override
  protected boolean isShears(IToolStackView tool) {
    return tool.getModifierLevel(TinkerModifiers.silky.getId()) > 0;
  }
}
