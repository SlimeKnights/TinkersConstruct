package slimeknights.tconstruct.common;

public class Config {
  public static boolean forceRegisterAll = false;

  public static boolean reuseStencil = true;
  public static boolean craftCastableMaterials = true;
  public static boolean chestsKeepInventory = true;

  // Worldgen
  public static boolean genIslandsInSuperflat = true;
  public static int slimeIslandsRate = 300; // Every x-th chunk will have a slime island. so 1 = every chunk, 100 = every 100th
  public static boolean genCobalt = true;
  public static int cobaltRate = 8; // max. cobalt per chunk/2
  public static boolean genArdite = true;
  public static int arditeRate = 8; // max. ardite per chunk/2

  // Clientside configs
  public static boolean renderTableItems = true;
  public static boolean extraTooltips = true;
}
