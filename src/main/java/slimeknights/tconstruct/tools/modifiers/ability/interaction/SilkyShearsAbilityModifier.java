package slimeknights.tconstruct.tools.modifiers.ability.interaction;

import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modules.interaction.ShearsModule;

/** @deprecated use {@link ShearsModule} with a condition of silky */
@SuppressWarnings("removal")
@Deprecated(forRemoval = true)
public class SilkyShearsAbilityModifier extends ShearsAbilityModifier {
  public SilkyShearsAbilityModifier(int range, int priority) {
    super(range, priority);
  }
  
  @Override
  protected boolean isShears(IToolStackView tool) {
    return tool.getModifierLevel(TinkerModifiers.silky.getId()) > 0;
  }
}
