package slimeknights.tconstruct.tools.data;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierId;

/**
 * This class contains the IDs of any dynamic modifiers that are not required.
 * That is, they may be used as traits or in recipes, but nothing in code relies on them existing
 */
public class ModifierIds {
  // bonus modifier slots
  public static final ModifierId writable    = id("writable");
  public static final ModifierId recapitated = id("recapitated");
  public static final ModifierId harmonious  = id("harmonious");
  public static final ModifierId resurrected = id("resurrected");
  public static final ModifierId gilded      = id("gilded");
  public static final ModifierId draconic    = id("draconic");

  // tier upgrades
  public static final ModifierId emerald   = id("emerald");
  public static final ModifierId diamond   = id("diamond");
  public static final ModifierId netherite = id("netherite");

  // general
  public static final ModifierId worldbound = id("worldbound");
  public static final ModifierId shiny      = id("shiny");
  // general abilities
  public static final ModifierId reach = id("reach");

  // loot
  public static final ModifierId luck    = id("luck");
  public static final ModifierId looting = id("looting");
  public static final ModifierId fortune = id("fortune");

  // damage boost
  public static final ModifierId sharpness   = id("sharpness");
  public static final ModifierId swiftstrike = id("swiftstrike");
  public static final ModifierId smite       = id("smite");
  public static final ModifierId baneOfSssss = id("bane_of_sssss");
  public static final ModifierId antiaquatic = id("antiaquatic");
  public static final ModifierId killager    = id("killager");
  public static final ModifierId cooling     = id("cooling");

  // ranged
  public static final ModifierId power = id("power");
  public static final ModifierId quickCharge = id("quick_charge");
  public static final ModifierId trueshot = id("trueshot");
  public static final ModifierId blindshot = id("blindshot");

  // armor
  public static final ModifierId wings = id("wings");
  public static final ModifierId knockbackResistance = id("knockback_resistance");
  // defense
  public static final ModifierId revitalizing = id("revitalizing");
  // chestplate
  public static final ModifierId knockbackArmor = id("knockback_armor");
  public static final ModifierId strength = id("strength");
  // leggings
  public static final ModifierId pockets = id("pockets");
  public static final ModifierId stepUp = id("step_up");
  public static final ModifierId speedy = id("speedy");
  public static final ModifierId toolBelt = id("tool_belt");

  // internal
  public static final ModifierId overslimeFriend = id("overslime_friend");


  // traits - tier 1
  public static final ModifierId stringy = id("stringy");
  public static final ModifierId flexible = id("flexible");
  // traits - tier 2
  public static final ModifierId sturdy = id("sturdy");
  public static final ModifierId scorching = id("scorching");
  // traits - tier 2 compat
  public static final ModifierId lustrous = id("lustrous");
  public static final ModifierId sharpweight = id("sharpweight");
  public static final ModifierId heavy = id("heavy");
  public static final ModifierId featherweight = id("featherweight");
  // traits - tier 3
  public static final ModifierId crumbling = id("crumbling");
  public static final ModifierId enhanced = id("enhanced");
  public static final ModifierId lightweight = id("lightweight");
  // traits - tier 3 compat
  public static final ModifierId ductile = id("ductile");

  // mob disguises
  public static final ModifierId creeperDisguise         = id("creeper_disguise");
  public static final ModifierId endermanDisguise        = id("enderman_disguise");
  public static final ModifierId skeletonDisguise        = id("skeleton_disguise");
  public static final ModifierId strayDisguise           = id("stray_disguise");
  public static final ModifierId witherSkeletonDisguise  = id("wither_skeleton_disguise");
  public static final ModifierId spiderDisguise          = id("spider_disguise");
  public static final ModifierId caveSpiderDisguise      = id("cave_spider_disguise");
  public static final ModifierId zombieDisguise          = id("zombie_disguise");
  public static final ModifierId huskDisguise            = id("husk_disguise");
  public static final ModifierId drownedDisguise         = id("drowned_disguise");
  public static final ModifierId blazeDisguise           = id("blaze_disguise");
  public static final ModifierId piglinDisguise          = id("piglin_disguise");
  public static final ModifierId piglinBruteDisguise     = id("piglin_brute_disguise");
  public static final ModifierId zombifiedPiglinDisguise = id("zombified_piglin_disguise");


  private ModifierIds() {}

  /**
   * Creates a new material ID
   * @param name  ID name
   * @return  Material ID object
   */
  private static ModifierId id(String name) {
    return new ModifierId(TConstruct.MOD_ID, name);
  }
}
