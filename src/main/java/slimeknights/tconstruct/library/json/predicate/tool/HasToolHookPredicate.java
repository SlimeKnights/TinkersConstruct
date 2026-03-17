package slimeknights.tconstruct.library.json.predicate.tool;

import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;

/** Predicate matching tools with the given hook */
public record HasToolHookPredicate(ModuleHook<?> hook) implements ToolContextPredicate {
  public static final RecordLoadable<HasToolHookPredicate> LOADER = RecordLoadable.create(ToolHooks.LOADER.requiredField("hook", HasToolHookPredicate::hook), HasToolHookPredicate::new);

  @Override
  public boolean matches(IToolContext tool) {
    return tool.getDefinition().getData().getHooks().hasHook(hook);
  }

  @Override
  public RecordLoadable<? extends IJsonPredicate<IToolContext>> getLoader() {
    return LOADER;
  }
}
