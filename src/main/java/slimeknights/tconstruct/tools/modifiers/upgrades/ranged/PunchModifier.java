package slimeknights.tconstruct.tools.modifiers.upgrades.ranged;

import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.tools.modules.ranged.common.PunchModule;

/** @deprecated use {@link PunchModule} */
@Deprecated(forRemoval = true)
public class PunchModifier extends Modifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addModule(new PunchModule(LevelingValue.eachLevel(1), ModifierCondition.ANY_TOOL));
  }
}
