package slimeknights.tconstruct.common;

import net.minecraft.util.ResourceLocation;

import slimeknights.tconstruct.library.Util;

public abstract class Sounds {
  private Sounds() {}

  public static final String saw = Util.resource("little_saw");
  public static final String anvil_use = "random.anvil_use";

  public static final String frypan_boing = Util.resource("frypan_hit");
  public static final String toy_squeak = Util.resource("toy_squeak");
}
