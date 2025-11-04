package slimeknights.tconstruct.tools.modules;

import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.CraftCountModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/** Module for changing how much ammo is crafted in an ammo modifier. Note this cannot increase the result above the tool stack size. */
public record CraftCountModule(LevelingValue multiplier, ModifierCondition<IToolStackView> condition) implements ModifierModule, CraftCountModifierHook, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<CraftCountModule>defaultHooks(ModifierHooks.CRAFT_COUNT);
  public static final RecordLoadable<CraftCountModule> LOADER = RecordLoadable.create(LevelingValue.LOADABLE.requiredField("multiplier", CraftCountModule::multiplier), ModifierCondition.TOOL_FIELD, CraftCountModule::new);

  public CraftCountModule(LevelingValue multiplier) {
    this(multiplier, ModifierCondition.ANY_TOOL);
  }

  @Override
  public RecordLoadable<CraftCountModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public float modifyCraftCount(IToolStackView tool, ModifierEntry entry, float amount) {
    if (condition.matches(tool, entry)) {
      amount *= multiplier.compute(entry.getEffectiveLevel());
    }
    return amount;
  }
}
