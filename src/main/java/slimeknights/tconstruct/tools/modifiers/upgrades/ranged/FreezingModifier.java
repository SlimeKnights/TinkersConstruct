package slimeknights.tconstruct.tools.modifiers.upgrades.ranged;

import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.tools.modules.armor.FreezingCounterModule;
import slimeknights.tconstruct.tools.modules.combat.FreezingAttackModule;

/** @deprecated use {@link FreezingAttackModule} and {@link FreezingCounterModule} */
@Deprecated(forRemoval = true)
public class FreezingModifier extends Modifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addModule(new FreezingAttackModule(new LevelingValue(4, 4)));
    hookBuilder.addModule(FreezingCounterModule.builder().constant(new LevelingValue(4, 4)).toolTag(TinkerTags.Items.ARMOR).build());
  }
}
