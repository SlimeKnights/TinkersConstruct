package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.modules.combat.KnockbackModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.tools.modules.armor.KnockbackCounterModule;

/** @deprecated use {@link KnockbackModule} and {@link KnockbackCounterModule} */
@Deprecated(forRemoval = true)
public class SpringyModifier extends Modifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(KnockbackModule.builder().eachLevel(0.5f));
    hookBuilder.addModule(KnockbackCounterModule.builder().durabilityUsage(0).random(LevelingValue.eachLevel(0.5f)).chanceLeveling(0.25f).build());
  }
}
