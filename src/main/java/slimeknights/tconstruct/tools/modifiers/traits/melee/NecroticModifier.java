package slimeknights.tconstruct.tools.modifiers.traits.melee;

import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.tools.modules.armor.RestoreLostHealthModule;
import slimeknights.tconstruct.tools.modules.combat.LifestealModule;

/** @deprecated use {@link LifestealModule} and {@link RestoreLostHealthModule} */
@Deprecated(forRemoval = true)
public class NecroticModifier extends Modifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addModule(LifestealModule.builder().eachLevel(0.05f));
    hookBuilder.addModule(RestoreLostHealthModule.builder().toolTag(TinkerTags.Items.ARMOR).eachLevel(0.25f));
  }
}
