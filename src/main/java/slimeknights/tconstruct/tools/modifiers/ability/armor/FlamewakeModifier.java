package slimeknights.tconstruct.tools.modifiers.ability.armor;

import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.tools.modules.armor.FireWalkerModule;

/** @deprecated use {@link FireWalkerModule} */
@Deprecated(forRemoval = true)
public class FlamewakeModifier extends NoLevelsModifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addModule(new FireWalkerModule(new LevelingValue(1.5f, 1)));
  }
}
