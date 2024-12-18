package slimeknights.tconstruct.tools.modifiers.traits.harvest;

import slimeknights.tconstruct.library.json.predicate.TinkerPredicate;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.modules.mining.ConditionalMiningSpeedModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;

/** @deprecated use {@link slimeknights.tconstruct.library.modifiers.modules.mining.ConditionalMiningSpeedModule} */
@Deprecated(forRemoval = true)
public class SearingModifier extends Modifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(ConditionalMiningSpeedModule.builder().blocks(TinkerPredicate.CAN_MELT_BLOCK).eachLevel(6f));
  }
}
