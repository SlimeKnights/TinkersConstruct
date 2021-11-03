package slimeknights.tconstruct.library.tools.layout;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;

public class Patterns {
  /** Pickaxe pattern used in center of the repair GUI */
  public static final Pattern PICKAXE = pattern("pickaxe");
  /* Icons used for the outer slots in repair UIs */
  public static final Pattern QUARTZ = pattern("quartz");
  public static final Pattern DUST = pattern("dust");
  public static final Pattern LAPIS = pattern("lapis");
  public static final Pattern INGOT = pattern("ingot");
  public static final Pattern GEM = pattern("gem");

  /** Repair icon, not an outline but a button icon */
  public static final Pattern REPAIR = pattern("button_repair");

  private static Pattern pattern(String name) {
    return new Pattern(TConstruct.MOD_ID, name);
  }

  private Patterns() {}
}
