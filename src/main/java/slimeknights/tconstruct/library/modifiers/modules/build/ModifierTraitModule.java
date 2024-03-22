package slimeknights.tconstruct.library.modifiers.modules.build;

import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierTraitHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;

import java.util.List;

/**
 * Module for a modifier to have a nested modifier as a trait.
 */
public record ModifierTraitModule(ModifierEntry modifier, boolean fixedLevel) implements ModifierTraitHook, ModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.MODIFIER_TRAITS);
  public static final RecordLoadable<ModifierTraitModule> LOADER = RecordLoadable.create(
    ModifierEntry.LOADABLE.directField(ModifierTraitModule::modifier),
    BooleanLoadable.INSTANCE.field("fixed_level", ModifierTraitModule::fixedLevel),
    ModifierTraitModule::new);

  public ModifierTraitModule(ModifierId id, int level, boolean fixedLevel) {
    this(new ModifierEntry(id, level), fixedLevel);
  }

  @Override
  public void addTraits(ToolRebuildContext context, ModifierEntry self, TraitBuilder builder, boolean firstEncounter) {
    if (fixedLevel) {
      // fixed levels do not need to add again if already added
      if (firstEncounter) {
        builder.addEntry(this.modifier);
      }
    } else {
      // level of the trait is based on the level of the modifier, just multiply the two
      builder.addEntry(this.modifier.withLevel(this.modifier.getLevel() * self.getLevel()));
    }
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public IGenericLoader<? extends ModifierTraitModule> getLoader() {
    return LOADER;
  }
}
