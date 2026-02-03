package slimeknights.tconstruct.tools.modifiers.upgrades.melee;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.tools.modules.combat.SeveringModule;

/** @deprecated use {@link SeveringModule} */
@Deprecated(forRemoval = true)
public class SeveringModifier extends Modifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addModule(SeveringModule.INSTANCE);
  }
}
