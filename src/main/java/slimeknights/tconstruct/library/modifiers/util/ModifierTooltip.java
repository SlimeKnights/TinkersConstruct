package slimeknights.tconstruct.library.modifiers.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.util.typed.TypedMap;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;

/** Contexts for showing a modifier in tooltips */
@Getter
@RequiredArgsConstructor
public enum ModifierTooltip {
  /** Shows in tooltips on tool items. */
  TOOL(false),
  /** Shows in advanced details on a tool in the tinker station or modifier worktable. */
  TINKER_STATION(false),
  /** Shows in tooltips on tool part items. */
  TOOL_PART(true),
  /** Shows in advanced details for the material in the part builder. */
  PART_BUILDER(true),
  /** Shows on book pages covering tool materials. */
  BOOK(true);

  /** Loadable instance for JSON contexts */
  public static final EnumLoadable<ModifierTooltip> LOADABLE = new EnumLoadable<>(ModifierTooltip.class);

  /** If true, this is a new flag type that did not exist with the old hook. */
  private final boolean isNew;

  /** Predicate testing for a modifier tooltip. */
  public record ShowInTooltips(Set<ModifierTooltip> tooltips) implements Predicate<ModifierTooltip> {
    /** Matches any tooltip context */
    public static final ShowInTooltips ALWAYS = new ShowInTooltips(EnumSet.allOf(ModifierTooltip.class));
    /** Never shows in any tooltip context */
    public static final ShowInTooltips NEVER = new ShowInTooltips(EnumSet.noneOf(ModifierTooltip.class));

    /** Common set for "modifier bundles", which show everywhere that is not on a tool. */
    public static final ShowInTooltips PARTS_ONLY = match(TOOL_PART, PART_BUILDER, BOOK);
    /** Common set for modifiers adding slots, shows everywhere except tools. */
    public static final ShowInTooltips BONUS_SLOT = match(TINKER_STATION, TOOL_PART, PART_BUILDER, BOOK);
    /** Shows on tables and books, but not on items. */
    public static final ShowInTooltips ADVANCED = match(TINKER_STATION, PART_BUILDER, BOOK);
    
    /** Loader instance. See {@link TooltipsLoadable} for the proper field. */
    private static final Loadable<ShowInTooltips> LOADABLE = ModifierTooltip.LOADABLE.set(0).flatXmap(ShowInTooltips::match, ShowInTooltips::tooltips);

    /** @apiNote use {@link #match(Set)} */
    @Internal
    public ShowInTooltips {}

    /** Creates an instance from the given options */
    public static ShowInTooltips match(Set<ModifierTooltip> tooltips) {
      if (tooltips.isEmpty()) {
        return NEVER;
      }
      if (tooltips.size() == ALWAYS.tooltips.size()) {
        return ALWAYS;
      }
      return new ShowInTooltips(tooltips);
    }

    /** Creates an instance from the given options */
    public static ShowInTooltips match(ModifierTooltip... tooltips) {
      return match(Set.of(tooltips));
    }

    /** Checks if this always matches */
    public boolean isAlways() {
      return this.tooltips.size() == ALWAYS.tooltips.size();
    }

    /** Checks if this never matches */
    public boolean isNever() {
      return this.tooltips.isEmpty();
    }

    @Override
    public boolean test(ModifierTooltip tooltip) {
      return tooltips.contains(tooltip);
    }

    /** Checks if the two instances match the same values */
    private boolean matches(ShowInTooltips other) {
      return this.tooltips.equals(other.tooltips);
    }
  }

  /** Loadable for {@link ShowInTooltips}, supporting both arbitrary sets and a number of presets. */
  public enum TooltipsLoadable implements Loadable<ShowInTooltips> {
    INSTANCE;

    /** Replaces the object instance with one of many common instances */
    private static ShowInTooltips deduplicate(ShowInTooltips tooltip) {
      if (ShowInTooltips.ADVANCED.equals(tooltip))   return ShowInTooltips.ADVANCED;
      if (ShowInTooltips.BONUS_SLOT.equals(tooltip)) return ShowInTooltips.BONUS_SLOT;
      if (ShowInTooltips.PARTS_ONLY.equals(tooltip)) return ShowInTooltips.PARTS_ONLY;
      return tooltip;
    }

    @Override
    public ShowInTooltips convert(JsonElement element, String key, TypedMap context) {
      if (element.isJsonPrimitive()) {
        String value = element.getAsString();
        switch (value) {
          case "always":     return ShowInTooltips.ALWAYS;
          case "never":      return ShowInTooltips.NEVER;
          case "advanced":   return ShowInTooltips.ADVANCED;
          case "bonus_slot": return ShowInTooltips.BONUS_SLOT;
          case "parts_only": return ShowInTooltips.PARTS_ONLY;
        };
      }
      return deduplicate(ShowInTooltips.LOADABLE.convert(element, key, context));
    }

    @Override
    public JsonElement serialize(ShowInTooltips tooltips) {
      if (tooltips.isNever()) return new JsonPrimitive("never");
      if (tooltips.isAlways()) return new JsonPrimitive("always");
      // other presets
      if (ShowInTooltips.ADVANCED.matches(tooltips)) return new JsonPrimitive("advanced");
      if (ShowInTooltips.BONUS_SLOT.matches(tooltips)) return new JsonPrimitive("bonus_slot");
      if (ShowInTooltips.PARTS_ONLY.matches(tooltips)) return new JsonPrimitive("parts_only");
      return ShowInTooltips.LOADABLE.serialize(tooltips);
    }

    @Override
    public ShowInTooltips decode(FriendlyByteBuf buffer, TypedMap context) {
      return deduplicate(ShowInTooltips.LOADABLE.decode(buffer, context));
    }

    @Override
    public void encode(FriendlyByteBuf buffer, ShowInTooltips value) {
      ShowInTooltips.LOADABLE.encode(buffer, value);
    }
  }
}
