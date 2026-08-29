package slimeknights.tconstruct.library.modifiers.util;

import net.minecraft.network.chat.Component;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.mantle.data.registry.DefaultingLoaderRegistry;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.utils.RomanNumeralHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Handles formatting the modifier name with levels.
 * TODO 1.21: move to its own package and extract subclasses.
 */
public interface ModifierLevelDisplay extends IHaveLoader {
  /** Default display, listing name followed by a roman numeral for level */
  ModifierLevelDisplay DEFAULT = simple((modifier, level) ->
    modifier.applyStyle(Component.translatable(modifier.getTranslationKey())
      .append(" ")
      .append(RomanNumeralHelper.getNumeral(level))));

  /** Loader instance */
  DefaultingLoaderRegistry<ModifierLevelDisplay> LOADER = new DefaultingLoaderRegistry<>("Modifier Level Display", DEFAULT, true);

  /** Gets the name for a modifier for the given level */
  Component nameForLevel(Modifier modifier, int level);

  @Override
  RecordLoadable<? extends ModifierLevelDisplay> getLoader();


  /* Non-default implementations */

  /** Displays just the name, for modifiers where multiple levels has no effect */
  ModifierLevelDisplay NO_LEVELS = simple((modifier, level) -> modifier.getDisplayName());

  /** Displays just the name for the first level, for modifiers that can have multiple levels but don't by design */
  ModifierLevelDisplay SINGLE_LEVEL = simple((modifier, level) ->  {
    if (level == 1) {
      return modifier.getDisplayName();
    }
    return DEFAULT.nameForLevel(modifier, level);
  });

  /** Displays level with pluses instead of numbers */
  ModifierLevelDisplay PLUSES = simple((modifier, level) -> {
    if (level > 1) {
      return modifier.applyStyle(Component.translatable(modifier.getTranslationKey()).append("+".repeat(level - 1)));
    }
    return modifier.getDisplayName();
  });

  /** Creates a simple level display singleton */
  static ModifierLevelDisplay simple(BiFunction<Modifier,Integer,Component> name) {
    return SingletonLoader.singleton(loader -> new ModifierLevelDisplay() {
      @Override
      public Component nameForLevel(Modifier modifier, int level) {
        return name.apply(modifier, level);
      }

      @Override
      public RecordLoadable<? extends ModifierLevelDisplay> getLoader() {
        return loader;
      }
    });
  }

  /**
   * Name that is unique for the first several levels
   * @param unique       Number of levels with unique names
   * @param firstUnique  If true, first level has a unique name. If false, first level uses the levelless modifier name
   */
  record UniqueForLevels(int unique, boolean firstUnique) implements ModifierLevelDisplay {
    public static final RecordLoadable<UniqueForLevels> LOADER = RecordLoadable.create(
      IntLoadable.FROM_ONE.requiredField("unique_until", UniqueForLevels::unique),
      BooleanLoadable.INSTANCE.defaultField("first_unique", false, UniqueForLevels::firstUnique),
      UniqueForLevels::new);

    public UniqueForLevels(int unique) {
      this(unique, false);
    }

    @Override
    public Component nameForLevel(Modifier modifier, int level) {
      if (!firstUnique && level == 1) {
        return modifier.getDisplayName();
      }
      if (level <= unique) {
        return modifier.applyStyle(Component.translatable(modifier.getTranslationKey() + "." + level));
      }
      return DEFAULT.nameForLevel(modifier, level);
    }

    @Override
    public RecordLoadable<UniqueForLevels> getLoader() {
      return LOADER;
    }
  }

  /** Caps the displayed modifier level to at most the given value. Used for effects that stop scaling after a point. */
  record LevelCap(int cap, ModifierLevelDisplay apply) implements ModifierLevelDisplay {
    public static final RecordLoadable<LevelCap> LOADER = RecordLoadable.create(
      IntLoadable.FROM_ONE.requiredField("cap", LevelCap::cap),
      ModifierLevelDisplay.LOADER.defaultField("apply", LevelCap::apply),
      LevelCap::new);

    public LevelCap(int cap) {
      this(cap, DEFAULT);
    }

    @Override
    public RecordLoadable<? extends ModifierLevelDisplay> getLoader() {
      return LOADER;
    }

    @Override
    public Component nameForLevel(Modifier modifier, int level) {
      return apply.nameForLevel(modifier, Math.min(level, cap));
    }
  }

  /** Displays the modifier name using another modifier's name */
  record ModifierName(LazyModifier modifier, LevelingInt level) implements ModifierLevelDisplay {
    public static final RecordLoadable<ModifierName> LOADER = RecordLoadable.create(
      ModifierId.PARSER.requiredField("modifier", r -> r.modifier.getId()),
      LevelingInt.LOADABLE.requiredField("level", ModifierName::level),
      ModifierName::new);
    /** Stack of modifiers currently being processed, to avoid cycles. */
    private static final List<ModifierId> PROCESSING = new ArrayList<>(4);

    public ModifierName(ModifierId modifier, LevelingInt level) {
      this(new LazyModifier(modifier), level);
    }

    @Override
    public RecordLoadable<ModifierName> getLoader() {
      return LOADER;
    }

    @Override
    public Component nameForLevel(Modifier modifier, int level) {
      ModifierId override = this.modifier.id;
      if (PROCESSING.contains(override)) {
        TConstruct.LOG.error("Modifier name override cycle detected: attempting to query {} but its being processed: {}", override, PROCESSING);
        // levelness display is good enough
        return this.modifier.get().getDisplayName();
      }
      PROCESSING.add(override);
      Component name = this.modifier.get().getDisplayName(this.level.computeForLevel(level));
      PROCESSING.remove(override);
      return name;
    }
  }
}
