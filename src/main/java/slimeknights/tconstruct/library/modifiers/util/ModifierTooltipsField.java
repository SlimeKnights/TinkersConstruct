package slimeknights.tconstruct.library.modifiers.util;

import com.google.gson.JsonObject;
import slimeknights.mantle.data.loadable.LegacyLoadable;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.field.AlwaysPresentLoadableField;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.impl.BasicModifier.TooltipDisplay;
import slimeknights.tconstruct.library.modifiers.util.ModifierTooltip.ShowInTooltips;
import slimeknights.tconstruct.library.modifiers.util.ModifierTooltip.TooltipsLoadable;

import java.util.function.Function;

/**
 * Field implementing legacy logic of {@link slimeknights.tconstruct.library.modifiers.impl.BasicModifier.TooltipDisplay} in {@link slimeknights.tconstruct.library.modifiers.impl.ComposableModifier} JSON.
 * TODO 1.21: replace with a default field on {@link TooltipsLoadable#INSTANCE}.
 * @param key     Current JSON key.
 * @param legacy  Legacy JSON key.
 * @param getter  Getter for serialization.
 * @param <P>     Parent class.
 */
@SuppressWarnings("removal")
public record ModifierTooltipsField<P>(String key, String legacy, Function<P, ShowInTooltips> getter) implements AlwaysPresentLoadableField<ShowInTooltips, P> {
  private static final Loadable<TooltipDisplay> LEGACY_ENUM = new EnumLoadable<>(TooltipDisplay.class);

  @Override
  public Loadable<ShowInTooltips> loadable() {
    return TooltipsLoadable.INSTANCE;
  }

  @Override
  public ShowInTooltips get(JsonObject json, String key, TypedMap context) {
    // ignore the legacy key if we have the new one, means you already migrated
    if (json.has(legacy) && !json.has(key)) {
      TooltipDisplay legacyEnum = LEGACY_ENUM.getIfPresent(json, legacy);
      String replacement = switch (legacyEnum) {
        case ALWAYS -> "always";
        case TINKER_STATION -> "bonus_slot";
        case NEVER -> "parts_only";
      };
      TConstruct.LOG.warn("Using deprecated JSON key '{}'{}, equivalent replacement is \"{}\": \"{}\"", legacy, LegacyLoadable.whileParsing(context), key, replacement);
      return legacyEnum.getShowInTooltips();
    }
    return TooltipsLoadable.INSTANCE.getOrDefault(json, key, ShowInTooltips.ALWAYS);
  }

  @Override
  public void serialize(P parent, JsonObject json) {
    ShowInTooltips show = getter.apply(parent);
    // skip serializing always
    if (!show.isAlways()) {
      json.add(key, TooltipsLoadable.INSTANCE.serialize(show));
    }
  }
}
