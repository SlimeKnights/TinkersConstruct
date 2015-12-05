package slimeknights.tconstruct.library.tinkering;

import java.util.Locale;

public class Category {

  // everything item built is a tool
  public static final Category TOOL = new Category("tool");
  // everything that has weapon as intended use
  public static final Category WEAPON = new Category("weapon");
  // everything that can harvest blocks
  public static final Category HARVEST = new Category("harvest");


  private final String name;

  public Category(String name) {
    this.name = name.toLowerCase(Locale.US);
  }
}
