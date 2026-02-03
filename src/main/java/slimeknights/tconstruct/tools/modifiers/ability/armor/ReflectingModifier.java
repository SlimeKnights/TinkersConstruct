package slimeknights.tconstruct.tools.modifiers.ability.armor;

import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.modules.build.VolatileIntModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.tools.logic.ModifierEvents;

/** @deprecated use {@link VolatileIntModule} with {@link ModifierEvents#REFLECTING} */
@Deprecated(forRemoval = true)
public class ReflectingModifier extends Modifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addModule(new VolatileIntModule(ModifierEvents.REFLECTING, LevelingInt.eachLevel(40)));
  }
}
