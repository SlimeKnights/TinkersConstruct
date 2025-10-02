package slimeknights.tconstruct.tools.modules;

import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.json.LevelingInt;
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
public record CraftCountModule(LevelingInt multiplier, ModifierCondition<IToolStackView> condition) implements ModifierModule, CraftCountModifierHook, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<CraftCountModule>defaultHooks(ModifierHooks.CRAFT_COUNT);
  public static final RecordLoadable<CraftCountModule> LOADER = RecordLoadable.create(LevelingInt.LOADABLE.requiredField("multiplier", CraftCountModule::multiplier), ModifierCondition.TOOL_FIELD, CraftCountModule::new);

  @Override
  public RecordLoadable<? extends IHaveLoader> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public int modifyCraftCount(IToolStackView tool, ModifierEntry entry, int amount) {
    if (condition.matches(tool, entry)) {
      amount *= multiplier.compute(entry.getEffectiveLevel());
    }
    return amount;
  }
}
