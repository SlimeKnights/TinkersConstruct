package slimeknights.tconstruct.library.tinkering;

import com.google.common.collect.Maps;

import java.util.Locale;
import java.util.Map;

public class Category {

  public static Map<String, Category> categories = Maps.newHashMap();

  // everything item built is a tool
  public static final Category TOOL = new Category("tool");
  // everything that has weapon as intended use
  public static final Category WEAPON = new Category("weapon");
  // everything that can harvest blocks
  public static final Category HARVEST = new Category("harvest");


  public final String name;

  public Category(String name) {
    this.name = name.toLowerCase(Locale.US);
    categories.put(name, this);
  }
}
