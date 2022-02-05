package slimeknights.tconstruct.plugin.jei.partbuilder;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.plugin.jei.JEIPlugin;

import javax.annotation.Nullable;

/**
 * Ingredient helper for Tinkers patterns
 */
public class PatternIngredientHelper implements IIngredientHelper<Pattern> {
  @Override
  public IIngredientType<Pattern> getIngredientType() {
    return JEIPlugin.PATTERN_TYPE;
  }

  @Nullable
  @Override
  public Pattern getMatch(Iterable<Pattern> iterable, Pattern match, UidContext context) {
    for (Pattern check : iterable) {
      if (match.equals(check)) {
        return check;
      }
    }
    return null;
  }

  @Override
  public String getDisplayName(Pattern pattern) {
    return pattern.getDisplayName().getString();
  }

  @Override
  public String getUniqueId(Pattern pattern, UidContext context) {
    return pattern.toString();
  }

  @Override
  public String getModId(Pattern pattern) {
    return pattern.getNamespace();
  }

  @Override
  public String getResourceId(Pattern pattern) {
    return pattern.getPath();
  }

  @Override
  public Pattern copyIngredient(Pattern pattern) {
    return pattern;
  }

  @Override
  public String getErrorInfo(@Nullable Pattern pattern) {
    if (pattern == null) {
      return "null";
    }
    return pattern.toString();
  }
}
