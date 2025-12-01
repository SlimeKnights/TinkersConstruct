package slimeknights.tconstruct.library.modifiers.modules;

import net.minecraftforge.common.crafting.conditions.ICondition;
import slimeknights.mantle.data.loadable.mapping.ConditionalLoadable.ConditionalObject;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.module.ModuleHook;

import java.util.List;

/**
 * Datagen helper for making conditional {@link ModifierModule}.
 * @param ifTrue      Module to use if all conditions are true.
 * @param ifFalse     Module to use if any condition is false. Defaults to {@link ModifierModule#EMPTY}
 * @param conditions  Conditions to evaluate.
 */
@SuppressWarnings("unused") // API
public record ConditionalModifierModule(ModifierModule ifTrue, ModifierModule ifFalse, ICondition... conditions) implements ModifierModule, ConditionalObject<ModifierModule> {
  public ConditionalModifierModule(ModifierModule ifTrue, ICondition... conditions) {
    this(ifTrue, ModifierModule.EMPTY, conditions);
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return List.of();
  }

  @Override
  public RecordLoadable<? extends ModifierModule> getLoader() {
    return ModifierModule.LOADER.getConditionalLoader();
  }
}
