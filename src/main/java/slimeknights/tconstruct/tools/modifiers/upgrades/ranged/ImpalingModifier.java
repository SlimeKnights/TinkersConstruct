package slimeknights.tconstruct.tools.modifiers.upgrades.ranged;

import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.tools.modules.ranged.common.ArrowPierceModule;

/** @deprecated use {@link ArrowPierceModule} */
@Deprecated(forRemoval = true)
public class ImpalingModifier extends Modifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addModule(new ArrowPierceModule(LevelingInt.eachLevel(1), ModifierCondition.ANY_TOOL));
  }
}
