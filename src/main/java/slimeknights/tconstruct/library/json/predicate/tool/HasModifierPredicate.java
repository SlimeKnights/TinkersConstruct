package slimeknights.tconstruct.library.json.predicate.tool;

import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

/**
 * Predicate that checks a tool for the given modifier.
 * @param modifier  Modifier to check for
 * @param level     Range of levels to check for, use {@link #DEFAULT_RANGE} for simply checking for any level on the tool, 0 means not on the tool.
 * @param check     Whether to check upgrades or all modifiers
 */
public record HasModifierPredicate(ModifierId modifier, IntRange level, ModifierCheck check) implements ToolContextPredicate {
  /** Valid bounds of the modifier level, 0 being allowed means you can condition on a modifier not being present if you wish */
  public static final IntRange MAX_RANGE = new IntRange(0, Short.MAX_VALUE);
  /** Default bounds of the modifier level */
  public static final IntRange DEFAULT_RANGE = MAX_RANGE.min(1);

  public static final RecordLoadable<HasModifierPredicate> LOADER = RecordLoadable.create(
    ModifierId.PARSER.field("modifier", HasModifierPredicate::modifier),
    MAX_RANGE.defaultField("level", DEFAULT_RANGE, true, HasModifierPredicate::level),
    new EnumLoadable<>(ModifierCheck.class).field("check", HasModifierPredicate::check),
    HasModifierPredicate::new);

  public HasModifierPredicate(ModifierId modifier, ModifierCheck check) {
    this(modifier, DEFAULT_RANGE, check);
  }

  @Override
  public boolean matches(IToolContext tool) {
    return level.test(check.getModifiers(tool).getLevel(modifier));
  }

  @Override
  public IJsonPredicate<IToolContext> inverted() {
    // if our range touches the maximum bound, then inverted just goes from min to our min-1
    if (level.max() == MAX_RANGE.max()) {
      return new HasModifierPredicate(modifier, new IntRange(MAX_RANGE.min(), level.min() - 1), check);
    }
    // if our range touches the minimum bound, then inverted just goes from our max+1 to max possible
    if (level.min() == MAX_RANGE.min()) {
      return new HasModifierPredicate(modifier, new IntRange(level.max() + 1, MAX_RANGE.max()), check);
    }
    // if we are not touching either edge, no possible range exists so use the regular inverted logic
    return ToolContextPredicate.super.inverted();
  }

  @Override
  public IGenericLoader<? extends ToolContextPredicate> getLoader() {
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
}
