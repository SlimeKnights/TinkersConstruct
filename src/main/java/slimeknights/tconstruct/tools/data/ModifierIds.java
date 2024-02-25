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
  public static final ModifierId rebalanced  = id("rebalanced");
  public static final ModifierId gilded      = id("gilded");
  public static final ModifierId draconic    = id("draconic");

  // tier upgrades
  public static final ModifierId emerald   = id("emerald");
  public static final ModifierId diamond   = id("diamond");
  public static final ModifierId netherite = id("netherite");

  // general
  public static final ModifierId reinforced = id("reinforced");
  public static final ModifierId worldbound = id("worldbound");
  public static final ModifierId shiny      = id("shiny");
  public static final ModifierId sticky     = id("sticky");
  public static final ModifierId theOneProbe = id("the_one_probe");
  // general abilities
  public static final ModifierId reach = id("reach");

  // harvest
  public static final ModifierId blasting = id("blasting");
  public static final ModifierId hydraulic = id("hydraulic");
  public static final ModifierId lightspeed = id("lightspeed");

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
  public static final ModifierId pierce      = id("pierce");

  // ranged
  public static final ModifierId power = id("power");
  public static final ModifierId quickCharge = id("quick_charge");
  public static final ModifierId trueshot = id("trueshot");
  public static final ModifierId blindshot = id("blindshot");

  // armor
  public static final ModifierId protection = id("protection");
  public static final ModifierId fireProtection = id("fire_protection");
  public static final ModifierId turtleShell = id("turtle_shell");
  public static final ModifierId wings = id("wings");
  public static final ModifierId knockbackResistance = id("knockback_resistance");
  // defense
  public static final ModifierId revitalizing = id("revitalizing");
  // helmet
  public static final ModifierId respiration = id("respiration");
  public static final ModifierId aquaAffinity = id("aqua_affinity");
  // chestplate
  public static final ModifierId strength = id("strength");
  // leggings
  public static final ModifierId pockets = id("pockets");
  public static final ModifierId stepUp = id("step_up");
  public static final ModifierId speedy = id("speedy");
  public static final ModifierId toolBelt = id("tool_belt");
  // boots
  public static final ModifierId depthStrider = id("depth_strider");
  public static final ModifierId featherFalling = id("feather_falling");
  public static final ModifierId longFall = id("long_fall");
  public static final ModifierId frostWalker = id("frost_walker");
  public static final ModifierId pathMaker = id("path_maker");
  public static final ModifierId plowing = id("plowing");
  public static final ModifierId snowdrift = id("snowdrift");

  // interaction
  public static final ModifierId pathing = id("pathing");
  public static final ModifierId stripping = id("stripping");
  public static final ModifierId tilling = id("tilling");

  // internal
  public static final ModifierId overslimeFriend = id("overslime_friend");
  public static final ModifierId snowBoots = id("snow_boots");


  // traits - tier 1
  public static final ModifierId cultivated = id("cultivated");
  public static final ModifierId stringy = id("stringy");
  public static final ModifierId flexible = id("flexible");
  // traits - tier 2
  public static final ModifierId sturdy = id("sturdy");
  public static final ModifierId scorching = id("scorching");
  public static final ModifierId raging = id("raging");
  public static final ModifierId airborne = id("airborne");
  // traits - tier 2 compat
  public static final ModifierId dense = id("dense");
  public static final ModifierId lustrous = id("lustrous");
  public static final ModifierId sharpweight = id("sharpweight");
  public static final ModifierId heavy = id("heavy");
  public static final ModifierId featherweight = id("featherweight");
  // traits - tier 3
  public static final ModifierId crumbling = id("crumbling");
  public static final ModifierId enhanced = id("enhanced");
  public static final ModifierId lightweight = id("lightweight");
  // traits - tier 3 compat
  public static final ModifierId maintained = id("maintained");
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
