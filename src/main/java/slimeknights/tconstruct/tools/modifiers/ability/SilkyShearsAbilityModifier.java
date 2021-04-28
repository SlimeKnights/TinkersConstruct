package slimeknights.tconstruct.tools.modifiers.ability;

import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class SilkyShearsAbilityModifier extends ShearsAbilityModifier {

  public SilkyShearsAbilityModifier(int color, int priority) {
    super(color, priority);
  }
  
  @Override
  protected boolean isShears(ToolStack tool) {
    return tool.getModifierLevel(TinkerModifiers.silky.get()) > 0;
  }

}
