package slimeknights.tconstruct.library.json.predicate.tool;

import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.json.predicate.modifier.ModifierPredicate;
import slimeknights.tconstruct.library.json.predicate.modifier.SingleModifierPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

/**
 * Predicate that checks a tool for the given modifier.
 * @param modifier  Modifier to check for
 * @param level     Range of levels to check for, use {@link ModifierEntry#VALID_LEVEL} for simply checking for any level on the tool, 0 means not on the tool.
 * @param check     Whether to check upgrades or all modifiers
 */
public record HasModifierPredicate(IJsonPredicate<ModifierId> modifier, IntRange level, ModifierCheck check) implements ToolContextPredicate {
  public static final RecordLoadable<HasModifierPredicate> LOADER = RecordLoadable.create(
    ModifierPredicate.LOADER.requiredField("modifier", HasModifierPredicate::modifier),
    ModifierEntry.ANY_LEVEL.defaultField("level", ModifierEntry.VALID_LEVEL, HasModifierPredicate::level),
    new EnumLoadable<>(ModifierCheck.class).requiredField("check", HasModifierPredicate::check),
    HasModifierPredicate::new);

  public HasModifierPredicate(ModifierId modifier, IntRange level, ModifierCheck check) {
    this(new SingleModifierPredicate(modifier), level, check);
  }

  @Override
  public boolean matches(IToolContext tool) {
    for (ModifierEntry entry : check.getModifiers(tool).getModifiers()) {
      // TODO: what if multiple modifiers match?
      if (modifier.matches(entry.getId())) {
        return level.test(entry.intEffectiveLevel());
      }
    }
    return level.test(0);
  }

  @Override
  public IJsonPredicate<IToolContext> inverted() {
    // if our range touches the maximum bound, then inverted just goes from min to our min-1
    if (level.max() == ModifierEntry.ANY_LEVEL.max()) {
      return new HasModifierPredicate(modifier, new IntRange(ModifierEntry.ANY_LEVEL.min(), level.min() - 1), check);
    }
    // if our range touches the minimum bound, then inverted just goes from our max+1 to max possible
    if (level.min() == ModifierEntry.ANY_LEVEL.min()) {
      return new HasModifierPredicate(modifier, new IntRange(level.max() + 1, ModifierEntry.ANY_LEVEL.max()), check);
    }
    // if we are not touching either edge, no possible range exists so use the regular inverted logic
    return ToolContextPredicate.super.inverted();
  }

  @Override
  public RecordLoadable<? extends ToolContextPredicate> getLoader() {
    return LOADER;
  }

  /** Enum of modifier type */
  public enum ModifierCheck {
    UPGRADES {
      @Override
      public ModifierNBT getModifiers(IToolContext tool) {
        return tool.getUpgrades();
      }
    },
    ALL {
      @Override
      public ModifierNBT getModifiers(IToolContext tool) {
        return tool.getModifiers();
      }
    };

    public abstract ModifierNBT getModifiers(IToolContext tool);
  }


  /* Constructors */

  /** Creates a predicate for a tool having the given upgrade (recipe modifier) */
  public static HasModifierPredicate hasUpgrade(IJsonPredicate<ModifierId> modifier, int min) {
    return new HasModifierPredicate(modifier, ModifierEntry.VALID_LEVEL.min(min), ModifierCheck.UPGRADES);
  }

  /** Creates a predicate for a tool having the given modifier (recipe or trait) */
  public static HasModifierPredicate hasModifier(IJsonPredicate<ModifierId> modifier, int min) {
    return new HasModifierPredicate(modifier, ModifierEntry.VALID_LEVEL.min(min), ModifierCheck.ALL);
  }

  /** Creates a predicate for a tool having the given upgrade (recipe modifier) */
  public static HasModifierPredicate hasUpgrade(ModifierId modifier, int min) {
    return new HasModifierPredicate(modifier, ModifierEntry.ANY_LEVEL.min(min), ModifierCheck.UPGRADES);
  }

  /** Creates a predicate for a tool having the given modifier (recipe or trait) */
  public static HasModifierPredicate hasModifier(ModifierId modifier, int min) {
    return new HasModifierPredicate(modifier, ModifierEntry.ANY_LEVEL.min(min), ModifierCheck.ALL);
  }
}
