package slimeknights.tconstruct.tools.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.MaterialId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MaterialIds {
  // tier 1
  public static final MaterialId wood = id("wood");
  public static final MaterialId flint = id("flint");
  public static final MaterialId stone = id("stone");
  public static final MaterialId bone = id("bone");
  // tier 2
  public static final MaterialId iron = id("iron");
  public static final MaterialId searedStone = id("seared_stone");
  public static final MaterialId copper = id("copper");
  public static final MaterialId slimewood = id("slimewood");
  // tier 2.5 - its an alloy but its not a higher mining level
  public static final MaterialId roseGold = id("rose_gold");
  // tier 3
  public static final MaterialId slimesteel = id("slimesteel");
  public static final MaterialId nahuatl = id("nahuatl");
  public static final MaterialId tinkersBronze = id("tinkers_bronze");
  public static final MaterialId pigIron = id("pig_iron");

  // tier 2 (nether)
  // nether wood
  // public static final MaterialId witherBone = id("wither_bone");
  // tier 3 (nether)
  // nether stone, scorched stone?
  public static final MaterialId cobalt = id("cobalt");
  // tier 4
  public static final MaterialId manyullyn = id("manyullyn");
  public static final MaterialId hepatizon = id("hepatizon");
  public static final MaterialId queensSlime = id("queens_slime");
  //public static final MaterialId soulsteel = id("soulsteel");

  // tier 2 (end)
  //public static final MaterialId endstone = id("endstone");
  // chorus

  // tier 2 (mod integration)
  public static final MaterialId silver = id("silver");
  public static final MaterialId lead = id("lead");
  // tier 3 (mod integration)
  public static final MaterialId electrum = id("electrum");
  public static final MaterialId bronze = id("bronze");
  public static final MaterialId steel = id("steel");
  public static final MaterialId constantan = id("constantan");

  // bowstring materials
  //public static final MaterialId string = id("string");
  //public static final MaterialId vine = id("vine");
  //public static final MaterialId slimevine_blue = id("slimevine_blue");
  //public static final MaterialId slimevine_purple = id("slimevine_purple");

  /**
   * Creates a new material ID
   * @param name  ID name
   * @return  Material ID object
   */
  private static MaterialId id(String name) {
    return new MaterialId(Util.getResource(name));
  }
}
