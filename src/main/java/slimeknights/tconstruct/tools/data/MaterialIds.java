package slimeknights.tconstruct.tools.data;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.MaterialId;

public final class MaterialIds {

  //Tier 1
  public static final MaterialId wood = id("wood");
  public static final MaterialId bone = id("bone");
  public static final MaterialId stone = id("stone");
  //public static final MaterialId paper = id("paper");
  public static final MaterialId flint = id("flint");
  public static final MaterialId coral = id("coral");

  //Tier 2
  public static final MaterialId copper = id("copper");
  public static final MaterialId searedstone = id("searedstone");
  public static final MaterialId iron = id("iron");
  public static final MaterialId slimewood = id("slimewood");
  public static final MaterialId slimestone = id("slimestone");

  //Tier 2 alternate
  public static final MaterialId mushwood = id("mushwood");
  public static final MaterialId bloodwood = id("bloodwood");
  public static final MaterialId blackstone = id("blackstone");
  public static final MaterialId basalt = id("basalt");
  public static final MaterialId witherbone = id("witherbone");

  //Tier 3
  public static final MaterialId slimesteel = id("slimesteel");
  public static final MaterialId nahuatl = id("nahuatl");
  public static final MaterialId bronze = id("bronze");
  public static final MaterialId pigiron = id("pigiron");
  public static final MaterialId rosegold = id("rosegold");
  public static final MaterialId ravagersteel = id("ravagersteel");

  public static final MaterialId cobalt = id("cobalt");
  public static final MaterialId endstone = id("endstone");
  public static final MaterialId chorus = id("chorus");

  //Tier 4
  public static final MaterialId soulsteel = id("soulsteel");
  public static final MaterialId heptazion = id("heptazion");
  public static final MaterialId slimebronze = id("slimebronze");
  public static final MaterialId blazewood = id("blazewood");

  //Tier 5
  public static final MaterialId manyullyn = id("manyullyn");
  public static final MaterialId knightslime = id("knightslime");
  public static final MaterialId knightmetal = id("knightmetal");
  public static final MaterialId rainbowslime = id("rainbowslime");
  public static final MaterialId alexandrite = id("alexandrite");
  public static final MaterialId gardite = id("gardite");

  private static MaterialId id(String name) {
    return new MaterialId(Util.getResource(name));
  }

  private MaterialIds() {
  }
}
