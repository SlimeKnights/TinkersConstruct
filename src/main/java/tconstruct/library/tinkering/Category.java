package tconstruct.library.tinkering;

import com.sun.istack.internal.NotNull;

public class Category {

  // everything item built is a tool
  public static final Category TOOL = new Category("tool");
  // everything that has weapon as intended use
  public static final Category WEAPON = new Category("weapon");
  // everything that can harvest blocks
  public static final Category HARVEST = new Category("harvest");


  private final String name;

  public Category(@NotNull String name) {
    this.name = name.toLowerCase();
  }
}
