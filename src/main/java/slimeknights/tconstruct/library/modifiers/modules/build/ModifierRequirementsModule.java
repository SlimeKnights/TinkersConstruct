package slimeknights.tconstruct.library.modifiers.modules.build;

import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.json.predicate.modifier.ModifierPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.HasModifierPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.ToolContextPredicate;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.build.ValidateModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.RequirementsModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.util.LazyModifier;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;

import static slimeknights.tconstruct.library.modifiers.ModifierEntry.VALID_LEVEL;

/**
 * Module to validate prerequisites for adding a modifier to a tool.
 */
public class ModifierRequirementsModule implements ValidateModifierHook, ModifierModule, RequirementsModifierHook {
  /** Loader for this module */
  public static final RecordLoadable<ModifierRequirementsModule> LOADER = RecordLoadable.create(
    ToolContextPredicate.LOADER.requiredField("requirement", m -> m.requirement),
    VALID_LEVEL.defaultField("modifier_level", m -> m.level),
    StringLoadable.DEFAULT.requiredField("translation_key", m -> m.translationKey),
    ModifierEntry.LOADABLE.list(0).defaultField("display_modifiers", List.of(), m -> m.display),
    ModifierRequirementsModule::new);

  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ModifierRequirementsModule>defaultHooks(ModifierHooks.VALIDATE_UPGRADE, ModifierHooks.REQUIREMENTS);

  /** Requirements to check, if they fail, the error will be displayed */
  private final IJsonPredicate<IToolContext> requirement;
  /** Level range for the modifier, if outside this range it is considered valid */
  private final IntRange level;
  /** Translation key of the message to display */
  private final String translationKey;
  /** Modifiers for display */
  private final List<ModifierEntry> display;
  /** Message to display */
  private final Component errorMessage;

  private ModifierRequirementsModule(IJsonPredicate<IToolContext> requirement, IntRange level, String translationKey, List<ModifierEntry> display) {
    this.requirement = requirement;
    this.level = level;
    this.translationKey = translationKey;
    this.display = display;
    this.errorMessage = Component.translatable(translationKey);
  }

  @Nullable
  @Override
  public Component validate(IToolStackView tool, ModifierEntry modifier) {
    if (level.test(modifier.getLevel()) && !this.requirement.matches(tool)) {
      return errorMessage;
    }
    return null;
  }

  @Nullable
  @Override
  public Component requirementsError(ModifierEntry entry) {
    return level.test(entry.getLevel()) ? errorMessage : null;
  }

  @Override
  public List<ModifierEntry> displayModifiers(ModifierEntry entry) {
    return level.test(entry.getLevel()) ? display : List.of();
  }

  @Override
  public RecordLoadable<ModifierRequirementsModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  /** Creates a new builder instance */
  public static Builder builder() {
    return new Builder();
  }

  @Setter
  @Accessors(fluent = true)
  public static class Builder {
    private final ImmutableList.Builder<IJsonPredicate<IToolContext>> requirements = ImmutableList.builder();
    private final ImmutableList.Builder<ModifierEntry> displayModifiers = ImmutableList.builder();
    private int minLevel = 1;
    private int maxLevel = VALID_LEVEL.max();
    private String translationKey;

    private Builder() {}

    /** Sets the translation key from a modifier ID */
    public Builder modifierKey(ModifierId id) {
      this.translationKey = Util.makeTranslationKey("modifier", id) + ".requirements";
      return this;
    }

    /** Sets the translation key from a modifier ID */
    public Builder modifierKey(LazyModifier id) {
      this.translationKey = Util.makeTranslationKey("modifier", id.getId()) + ".requirements";
      return this;
    }

    /** Adds a display modifier to the tool */
    @CanIgnoreReturnValue
    public Builder displayModifier(ModifierId id, int level) {
      this.displayModifiers.add(new ModifierEntry(id, level));
      return this;
    }

    /** Adds a requirement to the builder */
    public Builder requirement(IJsonPredicate<IToolContext> requirement) {
      this.requirements.add(requirement);
      return this;
    }

    /** Adds a displayed upgrade requirement to the builder */
    public Builder requireUpgrade(ModifierId id, int level) {
      displayModifier(id, level);
      this.requirements.add(HasModifierPredicate.hasUpgrade(id, level));
      return this;
    }

    /** Adds a displayed modifier requirement to the builder */
    public Builder requireModifier(ModifierId id, int level) {
      displayModifier(id, level);
      this.requirements.add(HasModifierPredicate.hasModifier(id, level));
      return this;
    }

    /** Adds an undisplayed upgrade requirement to the builder */
    public Builder requireUpgrade(TagKey<Modifier> tag, int level) {
      this.requirements.add(HasModifierPredicate.hasUpgrade(ModifierPredicate.tag(tag), level));
      return this;
    }

    /** Adds an undisplayed modifier requirement to the builder */
    public Builder requireModifier(TagKey<Modifier> tag, int level) {
      this.requirements.add(HasModifierPredicate.hasModifier(ModifierPredicate.tag(tag), level));
      return this;
    }

    /** Builds the final module */
    public ModifierRequirementsModule build() {
      if (translationKey == null) {
        throw new IllegalStateException("Must set translation key");
      }
      List<IJsonPredicate<IToolContext>> predicates = this.requirements.build();
      if (predicates.isEmpty()) {
        throw new IllegalStateException("Must have at least one requirement");
      }
      IntRange range = VALID_LEVEL.range(minLevel, maxLevel);
      List<ModifierEntry> display = this.displayModifiers.build();
      if (predicates.size() == 1) {
        return new ModifierRequirementsModule(predicates.get(0), range, translationKey, display);
      }
      return new ModifierRequirementsModule(ToolContextPredicate.LOADER.and(predicates), range, translationKey, display);
    }
  }
}
