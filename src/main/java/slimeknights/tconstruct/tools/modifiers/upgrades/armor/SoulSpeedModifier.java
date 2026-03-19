package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.tools.modules.armor.SoulSpeedModule;

/** @deprecated use {@link SoulSpeedModule} */
@Deprecated(forRemoval = true)
public class SoulSpeedModifier extends Modifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addModule(new SoulSpeedModule(LevelingInt.flat(1), ModifierCondition.ANY_TOOL));
  }
}
