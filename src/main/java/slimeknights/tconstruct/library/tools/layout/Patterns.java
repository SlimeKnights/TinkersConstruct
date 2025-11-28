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
  /** Shield for tool offhand slot */
  public static final Pattern SHIELD = pattern("shield");

  /** Repair icon, not an outline but a button icon */
  public static final Pattern REPAIR = pattern("button_repair");
  /** Icon with multiple plate armor pieces */
  public static final Pattern PLATE_ARMOR = pattern("plate_armor");
  /** Icon with multiple ammo items */
  public static final Pattern THROWN_AMMO = pattern("thrown_ammo");
  /** Pattern for generic plating */
  public static final Pattern PLATING = pattern("plating");
  /** Pattern for generic arrow parts */
  public static final Pattern ARROW_PART = pattern("arrow_part");
  /** Feather for arrows */
  public static final Pattern FEATHER = pattern("feather");

  private static Pattern pattern(String name) {
    return new Pattern(TConstruct.MOD_ID, name);
  }

  private Patterns() {}
}
