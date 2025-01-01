package slimeknights.tconstruct.library.modifiers.modules.build;

import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierTraitHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;

import java.util.List;

/**
 * Module for a modifier to have a nested modifier as a trait.
 */
public record ModifierTraitModule(ModifierEntry modifier, boolean fixedLevel) implements ModifierTraitHook, ModifierModule {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ModifierTraitModule>defaultHooks(ModifierHooks.MODIFIER_TRAITS);
  public static final RecordLoadable<ModifierTraitModule> LOADER = RecordLoadable.create(
    ModifierEntry.LOADABLE.directField(ModifierTraitModule::modifier),
    BooleanLoadable.INSTANCE.requiredField("fixed_level", ModifierTraitModule::fixedLevel),
    ModifierTraitModule::new);

  public ModifierTraitModule(ModifierId id, int level, boolean fixedLevel) {
    this(new ModifierEntry(id, level), fixedLevel);
  }

  @Override
  public void addTraits(IToolContext context, ModifierEntry self, TraitBuilder builder, boolean firstEncounter) {
    if (fixedLevel) {
      // fixed levels do not need to add again if already added
      if (firstEncounter) {
        builder.add(this.modifier);
      }
    } else {
      // level of the trait is based on the level of the modifier, just multiply the two
      builder.add(this.modifier.withLevel(this.modifier.getLevel() * self.getLevel()));
    }
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public RecordLoadable<ModifierTraitModule> getLoader() {
    return LOADER;
  }
}
