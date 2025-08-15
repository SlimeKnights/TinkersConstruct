package slimeknights.tconstruct.library.modifiers.hook.build;

import lombok.RequiredArgsConstructor;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.helper.ModifierBuilder;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/** Hook for a modifier to add in other modifiers */
public interface ModifierTraitHook {
  /**
   * Add all traits from this modifier to the builder.
   * This hook may be called multiple times during the building process if multiple modifiers have the same trait, use {@code firstEncounter} to distinguish.
   * Do not call this method directly, call it through {@link TraitBuilder}.
   * @param context         Tool building context, note that volatile data has not yet been filled and modifiers does not include traits
   * @param modifier        Modifier entry
   * @param builder         Builder handling traits, use methods on this object to add traits
   * @param firstEncounter  If true, this is the first time this modifier has been seen while rebuilding the stats
   */
  void addTraits(IToolContext context, ModifierEntry modifier, TraitBuilder builder, boolean firstEncounter);

  /** Builder that handles adding traits that can themselves contain traits */
  @RequiredArgsConstructor
  class TraitBuilder implements ModifierBuilder {
    /** Set of all modifiers that have been encountered during this rebuild */
    private final Set<Modifier> seenModifiers = new HashSet<>();
    /** Modifiers that are currently adding their traits, prevents adding traits for a modifier inside itself, which will recurse infinitely */
    private final Set<Modifier> currentStack = new LinkedHashSet<>();
    /** Context for tool building */
    private final IToolContext context;
    /** Builder instance */
    private final ModifierNBT.Builder builder;

    /** Adds the given modifier to the builder and adds all its traits */
    @Override
    public TraitBuilder add(ModifierEntry entry) {
      if (entry.isBound()) {
        builder.add(entry);
        addTraits(entry);
      }
      return this;
    }

    /** Adds all traits for the given modifier entry */
    private void addTraits(ModifierEntry entry) {
      Modifier modifier = entry.getModifier();
      // if the modifier lacks the trait hook, then we can skip tracking it, no need to add it to any data structures
      ModifierTraitHook hook = modifier.getHooks().getOrNull(ModifierHooks.MODIFIER_TRAITS);
      if (hook != null) {
        // if this modifier is already on the stack, ignore it to avoid infinite recursion
        if (currentStack.contains(modifier)) {
          TConstruct.LOG.error("Encountered {} as a child of itself, previous stack {}", modifier.getId(), currentStack);
        } else {
          // not on the stack? add it, then recursively add traits
          currentStack.add(modifier);
          hook.addTraits(context, entry, this, !hasSeenModifier(modifier));
          seenModifiers.add(modifier);
          currentStack.remove(modifier);
        }
      }
    }

    /** Checks if the given modifier has been seen before */
    public boolean hasSeenModifier(Modifier modifier) {
      return seenModifiers.contains(modifier);
    }

    @Override
    public ModifierNBT build() {
      return builder.build();
    }
  }

  /** Merger that runs all hooks */
  record AllMerger(Collection<ModifierTraitHook> modules) implements ModifierTraitHook {
    @Override
    public void addTraits(IToolContext context, ModifierEntry modifier, TraitBuilder builder, boolean firstEncounter) {
      for (ModifierTraitHook module : modules) {
        module.addTraits(context, modifier, builder, firstEncounter);
      }
    }
  }
}
