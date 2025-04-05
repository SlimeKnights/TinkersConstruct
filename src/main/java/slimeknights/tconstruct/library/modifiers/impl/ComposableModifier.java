package slimeknights.tconstruct.library.modifiers.impl;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import slimeknights.mantle.data.loadable.ErrorFactory;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.module.WithHooks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/** Modifier consisting of many composed hooks, used in datagen as a serialized modifier. */
public class ComposableModifier extends BasicModifier {
  public static final RecordLoadable<ComposableModifier> LOADER = RecordLoadable.create(
    ModifierLevelDisplay.LOADER.defaultField("level_display", true, m -> m.levelDisplay),
    new EnumLoadable<>(TooltipDisplay.class).defaultField("tooltip_display", TooltipDisplay.ALWAYS, true, m -> m.tooltipDisplay),
    IntLoadable.ANY_FULL.defaultField("priority", Integer.MIN_VALUE, m -> m.priority),
    ModifierModule.WITH_HOOKS.list(0).defaultField("modules", List.of(), m -> m.modules),
    ErrorFactory.FIELD,
    (level, tooltip, priority, modules, error) -> new ComposableModifier(level, tooltip, priority == Integer.MIN_VALUE ? computePriority(modules) : priority, modules, error));

  private final List<WithHooks<ModifierModule>> modules;

  /**
   * Creates a new instance
   * @param levelDisplay     Level display
   * @param tooltipDisplay   Tooltip display
   * @param priority         If the value is {@link Integer#MIN_VALUE}, assumed unset for datagen
   * @param modules          Modules for this modifier
   */
  protected ComposableModifier(ModifierLevelDisplay levelDisplay, TooltipDisplay tooltipDisplay, int priority, List<WithHooks<ModifierModule>> modules, ErrorFactory error) {
    super(ModuleHookMap.createMap(modules, error), levelDisplay, tooltipDisplay, priority);
    this.modules = modules;
  }

  /** Creates a builder instance for datagen */
  public static Builder builder() {
    return new Builder();
  }

  /** Computes the recommended priority for a set of modifier modules */
  private static int computePriority(List<WithHooks<ModifierModule>> modules) {
    // poll all modules to find who has a priority preference
    List<ModifierModule> priorityModules = new ArrayList<>();
    for (WithHooks<ModifierModule> module : modules) {
      if (module.module().getPriority() != null) {
        priorityModules.add(module.module());
      }
    }
    if (!priorityModules.isEmpty()) {
      //noinspection ConstantConditions  validated nonnull above
      int firstPriority = priorityModules.get(0).getPriority();

      // check if any module disagrees with the first priority, if so we need a warning (but not more than one warning)
      for (int i = 1; i < priorityModules.size(); i++) {
        //noinspection ConstantConditions  validated nonnull above
        if (priorityModules.get(i).getPriority() != firstPriority) {
          TConstruct.LOG.warn("Multiple modules disagree on the preferred priority for composable modifier, choosing priority {}. Set the priority manually to silence this warning. All opinions: \n{}", firstPriority,
                              priorityModules.stream()
                                             .map(module -> "* " + module + ": " + module.getPriority())
                                             .collect(Collectors.joining("\n")));
          break;
        }
      }
      return firstPriority;
    }
    return Modifier.DEFAULT_PRIORITY;
  }

  /** Builder for a composable modifier instance */
  @SuppressWarnings("UnusedReturnValue")  // it's a builder
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  @Accessors(fluent = true)
  public static class Builder {
    @Setter
    private ModifierLevelDisplay levelDisplay = ModifierLevelDisplay.DEFAULT;
    @Setter
    private TooltipDisplay tooltipDisplay = TooltipDisplay.ALWAYS;
    /** {@link Integer#MIN_VALUE} is an internal value used to represent unset for datagen, to distinguish unset from {@link Modifier#DEFAULT_PRIORITY} */
    @Setter
    private int priority = Integer.MIN_VALUE;
    private final ImmutableList.Builder<WithHooks<ModifierModule>> modules = ImmutableList.builder();

    /** Adds a module to the builder */
    public final Builder addModule(ModifierModule module) {
      modules.add(new WithHooks<>(module, Collections.emptyList()));
      return this;
    }

    /** Adds a module to the builder */
    public final Builder addModules(ModifierModule... modules) {
      for (ModifierModule module : modules) {
        addModule(module);
      }
      return this;
    }

    /** Adds a module to the builder */
    @SuppressWarnings("UnusedReturnValue")
    @SafeVarargs
    public final <T extends ModifierModule> Builder addModule(T object, ModuleHook<? super T>... hooks) {
      modules.add(new WithHooks<>(object, List.of(hooks)));
      return this;
    }

    /** Builds the final instance */
    public ComposableModifier build() {
      List<WithHooks<ModifierModule>> modules = this.modules.build();
      if (priority == Integer.MIN_VALUE) {
        // call computePriority if we did not set one so we get the warning if multiple modules wish to set the priority
        computePriority(modules);
      }
      return new ComposableModifier(levelDisplay, tooltipDisplay, priority, modules, ErrorFactory.RUNTIME);
    }
  }
}
