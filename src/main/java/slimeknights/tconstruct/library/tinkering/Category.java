package slimeknights.tconstruct.library.tinkering;

import com.google.common.collect.Maps;

import java.util.Locale;
import java.util.Map;

public class Category {

  public static Map<String, Category> categories = Maps.newHashMap();

  /** Everything item built is a tool */
  public static final Category TOOL = new Category("tool");
  /** Everything that has weapon as intended use */
  public static final Category WEAPON = new Category("weapon");
  /** Everything that can harvest blocks */
  public static final Category HARVEST = new Category("harvest");
  /** Anything which has an area of effect, which can be expanded. Can be attack or harvest */
  public static final Category AOE = new Category("aoe");
  /** Everything thrown or launched over a distance */
  public static final Category PROJECTILE = new Category("projectile");
  /** Everything which does nothing on melee */
  public static final Category NO_MELEE = new Category("no_melee");
  /** Everything that shoots other projectiles */
  public static final Category LAUNCHER = new Category("launcher");


  public final String name;

  public Category(String name) {
    this.name = name.toLowerCase(Locale.US);
    categories.put(name, this);
  }
}
