package slimeknights.tconstruct.library.modifiers.modules.build;

import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;

import java.util.List;

/**
 * Module that adds extra modifier slots to a tool.
 */
public record ModifierSlotModule(SlotType type, int count, ModifierCondition<IToolContext> condition) implements VolatileDataModifierHook, ModifierModule, ConditionalModule<IToolContext> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ModifierSlotModule>defaultHooks(ModifierHooks.VOLATILE_DATA);
  public static final RecordLoadable<ModifierSlotModule> LOADER = RecordLoadable.create(
    SlotType.LOADABLE.requiredField("name", ModifierSlotModule::type),
    IntLoadable.ANY_SHORT.defaultField("count", 1, true, ModifierSlotModule::count),
    ModifierCondition.CONTEXT_FIELD,
    ModifierSlotModule::new);

  public ModifierSlotModule(SlotType type, int count) {
    this(type, count, ModifierCondition.ANY_CONTEXT);
  }

  public ModifierSlotModule(SlotType type) {
    this(type, 1);
  }

  @Override
  public Integer getPriority() {
    // show lower priority so they group together
    return 50;
  }

  @Override
  public void addVolatileData(IToolContext context, ModifierEntry modifier, ToolDataNBT volatileData) {
    if (condition.matches(context, modifier)) {
      volatileData.addSlots(type, (int)(count * modifier.getEffectiveLevel()));
    }
  }

  @Override
  public RecordLoadable<ModifierSlotModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }
}
