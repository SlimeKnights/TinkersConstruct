package slimeknights.tconstruct.tools.data.material;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.definition.MaterialId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MaterialIds {
  // tier 1
  public static final MaterialId wood = id("wood");
  public static final MaterialId flint = id("flint");
  public static final MaterialId stone = id("stone");
  public static final MaterialId bone = id("bone");
  // tier 1 - nether
  public static final MaterialId necroticBone = id("necrotic_bone");
  // tier 1 - bindings
  public static final MaterialId string = id("string");
  public static final MaterialId leather = id("leather");
  public static final MaterialId vine = id("vine");
  // tier 2
  public static final MaterialId iron = id("iron");
  public static final MaterialId searedStone = id("seared_stone");
  public static final MaterialId scorchedStone = id("scorched_stone");
  public static final MaterialId copper = id("copper");
  public static final MaterialId slimewood = id("slimewood");
  public static final MaterialId bloodbone = id("bloodbone");
  // tier 2 - bindings
  public static final MaterialId chain = id("chain");
  public static final MaterialId skyslimeVine = id("skyslime_vine");
  // tier 3
  public static final MaterialId slimesteel = id("slimesteel");
  public static final MaterialId nahuatl = id("nahuatl");
  public static final MaterialId tinkersBronze = id("tinkers_bronze");
  public static final MaterialId pigIron = id("pig_iron");
  public static final MaterialId roseGold = id("rose_gold");
  // tier 3 (nether)
  public static final MaterialId cobalt = id("cobalt");
  // tier 4
  public static final MaterialId manyullyn = id("manyullyn");
  public static final MaterialId hepatizon = id("hepatizon");
  public static final MaterialId queensSlime = id("queens_slime");
  public static final MaterialId blazingBone = id("blazing_bone");
  //public static final MaterialId soulsteel = id("soulsteel");
  // tier 5 - bindings
  public static final MaterialId enderslimeVine = id("enderslime_vine");

  // tier 2 (mod integration)
  public static final MaterialId osmium = id("osmium");
  public static final MaterialId tungsten = id("tungsten");
  public static final MaterialId platinum = id("platinum");
  public static final MaterialId silver = id("silver");
  public static final MaterialId lead = id("lead");
  public static final MaterialId whitestone = id("whitestone");
  // tier 3 (mod integration)
  public static final MaterialId steel = id("steel");
  public static final MaterialId bronze = id("bronze");
  public static final MaterialId constantan = id("constantan");
  public static final MaterialId invar = id("invar");
  public static final MaterialId necronium = id("necronium");
  public static final MaterialId electrum = id("electrum");
  public static final MaterialId platedSlimewood = id("plated_slimewood");

  // slimeskull
  public static final MaterialId gunpowder = id("gunpowder");
  public static final MaterialId enderPearl = id("ender_pearl");
  public static final MaterialId spider = id("spider");
  public static final MaterialId venom = id("venom");
  public static final MaterialId rottenFlesh = id("rotten_flesh");
  // slimesuit
  public static final MaterialId enderslime = id("enderslime");
  public static final MaterialId phantom = id("phantom");
  public static final MaterialId chorus = id("chorus");
  public static final MaterialId rabbit = id("rabbit");

  /**
   * Creates a new material ID
   * @param name  ID name
   * @return  Material ID object
   */
  private static MaterialId id(String name) {
    return new MaterialId(TConstruct.MOD_ID, name);
  }
}
