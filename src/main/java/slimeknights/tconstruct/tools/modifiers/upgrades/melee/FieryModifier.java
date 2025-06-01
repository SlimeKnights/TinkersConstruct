package slimeknights.tconstruct.tools.modifiers.upgrades.melee;

import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.tools.modules.armor.FieryCounterModule;
import slimeknights.tconstruct.tools.modules.combat.FieryAttackModule;

/** @deprecated use {@link FieryAttackModule} and {@link FieryCounterModule} */
@Deprecated(forRemoval = true)
public class FieryModifier extends Modifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addModule(new FieryAttackModule(LevelingValue.eachLevel(5)));
    hookBuilder.addModule(FieryCounterModule.builder().constant(LevelingValue.eachLevel(5)).toolTag(TinkerTags.Items.ARMOR).build());
  }
}
