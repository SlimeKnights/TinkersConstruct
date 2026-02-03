package slimeknights.tconstruct.tools.modifiers.slotless;

import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.tools.modules.FovModule;
import slimeknights.tconstruct.tools.modules.FovModule.FovAction;

/** @deprecated use {@link FovModule} */
@Deprecated(forRemoval = true)
public class NearsightedModifier extends Modifier implements EquipmentChangeModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addModule(new FovModule(LevelingValue.eachLevel(0.05f), FovAction.INCREASE));
  }
}
