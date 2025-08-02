package slimeknights.tconstruct.tools.modifiers.traits.ranged;

import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.tools.modules.ranged.HolyArrowModule;

/** @deprecated use {@link slimeknights.tconstruct.tools.modules.ranged.HolyArrowModule} */
@Deprecated(forRemoval = true)
public class HolyModifier extends Modifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(new HolyArrowModule(LevelingValue.flat(0.75f)));
  }
}
