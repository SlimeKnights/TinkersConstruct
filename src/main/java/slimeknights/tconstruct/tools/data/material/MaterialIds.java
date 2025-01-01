package slimeknights.tconstruct.tools.data.material;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import static slimeknights.tconstruct.library.materials.definition.MaterialVariantId.create;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MaterialIds {
  // tier 1
  public static final MaterialId wood = id("wood");
  public static final MaterialId flint = id("flint");
  public static final MaterialId rock = id("rock");
  public static final MaterialId copper = id("copper");
  public static final MaterialId bone = id("bone");
  public static final MaterialId bamboo = id("bamboo");
  // tier 1 - end
  public static final MaterialId chorus = id("chorus");
  // tier 1 - bindings
  public static final MaterialId string = id("string");
  public static final MaterialId leather = id("leather");
  public static final MaterialId vine = id("vine");
  // tier 2
  public static final MaterialId iron = id("iron");
  public static final MaterialId searedStone = id("seared_stone");
  public static final MaterialId slimewood = id("slimewood");
  public static final MaterialId venombone = id("venombone");
  // tier 2 - nether
  public static final MaterialId scorchedStone = id("scorched_stone");
  public static final MaterialId necroticBone = id("necrotic_bone");
  // tier 2 - end
  public static final MaterialId whitestone = id("whitestone");
  // tier 2 - bindings
  public static final MaterialId skyslimeVine = id("skyslime_vine");
  // tier 3
  public static final MaterialId slimesteel = id("slimesteel");
  public static final MaterialId amethystBronze = id("amethyst_bronze");
  public static final MaterialId nahuatl = id("nahuatl");
  public static final MaterialId pigIron = id("pig_iron");
  public static final MaterialId roseGold = id("rose_gold");
  // tier 3 (nether)
  public static final MaterialId cobalt = id("cobalt");
  // tier 3 - bindings
  public static final MaterialId darkthread = id("darkthread");
  // tier 4
  public static final MaterialId manyullyn = id("manyullyn");
  public static final MaterialId hepatizon = id("hepatizon");
  public static final MaterialId queensSlime = id("queens_slime");
  public static final MaterialId blazingBone = id("blazing_bone");
  public static final MaterialId blazewood = id("blazewood");
  //public static final MaterialId soulsteel = id("soulsteel");
  // tier 4 - bindings
  public static final MaterialId ancientHide = id("ancient_hide");
  // tier 5 - bindings
  public static final MaterialId enderslimeVine = id("enderslime_vine");

  // tier 2 (mod integration)
  public static final MaterialId osmium = id("osmium");
  public static final MaterialId tungsten = id("tungsten");
  public static final MaterialId platinum = id("platinum");
  public static final MaterialId silver = id("silver");
  public static final MaterialId lead = id("lead");
  public static final MaterialId aluminum = id("aluminum");
  // tier 3 (mod integration)
  public static final MaterialId steel = id("steel");
  public static final MaterialId bronze = id("bronze");
  public static final MaterialId constantan = id("constantan");
  public static final MaterialId invar = id("invar");
  public static final MaterialId necronium = id("necronium");
  public static final MaterialId electrum = id("electrum");
  public static final MaterialId platedSlimewood = id("plated_slimewood");

  // plate
  public static final MaterialId gold = id("gold");
  public static final MaterialId obsidian = id("obsidian");
  // slimeskull
  public static final MaterialId glass = id("glass");
  public static final MaterialId enderPearl = id("ender_pearl");
  public static final MaterialId rottenFlesh = id("rotten_flesh");
  // slimesuit
  public static final MaterialId enderslime = id("enderslime");
  public static final MaterialId phantom = id("phantom");
  // slimesuit - textures
  public static final MaterialId earthslime = id("earthslime");
  public static final MaterialId skyslime = id("skyslime");
  public static final MaterialId ichor = id("ichor");
  public static final MaterialId blood = id("blood");
  public static final MaterialId magma = id("magma");
  public static final MaterialId clay = id("clay");
  public static final MaterialId honey = id("honey");

  /*
   * Variants
   */
  public static final MaterialVariantId basalt  = create(flint, "basalt");
  // wood
  public static final MaterialVariantId oak      = create(wood, "oak");
  public static final MaterialVariantId spruce   = create(wood, "spruce");
  public static final MaterialVariantId birch    = create(wood, "birch");
  public static final MaterialVariantId jungle   = create(wood, "jungle");
  public static final MaterialVariantId acacia   = create(wood, "acacia");
  public static final MaterialVariantId darkOak  = create(wood, "dark_oak");
  public static final MaterialVariantId mangrove = create(wood, "mangrove");
  public static final MaterialVariantId cherry   = create(wood, "cherry");
  public static final MaterialVariantId crimson  = create(wood, "crimson");
  public static final MaterialVariantId warped   = create(wood, "warped");
  // stone
  public static final MaterialVariantId stone      = create(rock, "stone");
  public static final MaterialVariantId andesite   = create(rock, "andesite");
  public static final MaterialVariantId diorite    = create(rock, "diorite");
  public static final MaterialVariantId granite    = create(rock, "granite");
  public static final MaterialVariantId blackstone = create(rock, "blackstone");
  public static final MaterialVariantId deepslate  = create(rock, "deepslate");
  public static final MaterialVariantId endstone   = create(whitestone, "end");
  // slimewood
  public static final MaterialVariantId slimewoodComposite = create(slimewood, "composite");
  public static final MaterialVariantId greenheart         = create(slimewood, "greenheart");
  public static final MaterialVariantId skyroot            = create(slimewood, "skyroot");
  public static final MaterialVariantId bloodshroom        = create(slimewood, "bloodshroom");
  public static final MaterialVariantId enderbark          = create(slimewood, "enderbark");
  // oxidized
  public static final MaterialVariantId wroughtIron = create(iron, "wrought");
  public static final MaterialVariantId oxidizedIron = create(iron, "oxidized");
  public static final MaterialVariantId oxidizedCopper = create(copper, "oxidized");

  /**
   * Creates a new material ID
   * @param name  ID name
   * @return  Material ID object
   */
  private static MaterialId id(String name) {
    return new MaterialId(TConstruct.MOD_ID, name);
  }
}
