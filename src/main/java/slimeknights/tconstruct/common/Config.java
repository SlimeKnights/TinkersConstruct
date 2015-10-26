package slimeknights.tconstruct.common;

public class Config {
  public static boolean reuseStencil = true;
  public static boolean craftCastableMaterials = true;

  // Worldgen
  public static boolean genIslandsInSuperflat = true;
  public static int slimeIslandsRate = 30; // Every x-th chunk will have a slime island. so 1 = every chunk, 100 = every 100th

  // Clientside configs
  public static boolean renderTableItems = true;
  public static boolean extraTooltips = true;
}
