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
  public static final ModifierId forecast = id("forecast");
  /** @deprecated use {@link #forecast} */
  @Deprecated
  public static final ModifierId resurrected = id("resurrected");
  public static final ModifierId embossed    = id("embossed");
  public static final ModifierId rebalanced  = id("rebalanced");
  public static final ModifierId redirected  = id("redirected");
  public static final ModifierId gilded      = id("gilded");
  public static final ModifierId draconic    = id("draconic");

  // tier upgrades
  public static final ModifierId emerald   = id("emerald");
  public static final ModifierId diamond   = id("diamond");
  public static final ModifierId netherite = id("netherite");

  // general
  public static final ModifierId reinforced = id("reinforced");
  public static final ModifierId overforced = id("overforced");
  public static final ModifierId worldbound = id("worldbound");
  public static final ModifierId shiny      = id("shiny");
  public static final ModifierId sticky     = id("sticky");
  public static final ModifierId tank       = id("tank");
  public static final ModifierId offhanded  = id("offhanded");
  public static final ModifierId soulbound = id("soulbound");
  public static final ModifierId smelting = id("smelting");
  // combat
  public static final ModifierId fiery = id("fiery");
  public static final ModifierId freezing = id("freezing");
  public static final ModifierId springy = id("springy");
  public static final ModifierId spilling = id("spilling");
  public static final ModifierId spillingRod = id("spilling_rod");
  public static final ModifierId fins = id("fins");
  public static final ModifierId finsAmmo = id("fins_ammo");
  public static final ModifierId channeling = id("channeling");
  public static final ModifierId drillAttack = id("drill_attack");
  // general abilities
  public static final ModifierId reach = id("reach");
  public static final ModifierId glowing = id("glowing");
  // compat
  public static final ModifierId theOneProbe = id("the_one_probe");
  public static final ModifierId headlight = id("headlight");
  // zoom
  public static final ModifierId scope = id("scope");
  public static final ModifierId zoom = id("zoom");

  // harvest
  public static final ModifierId haste = id("haste");
  public static final ModifierId blasting = id("blasting");
  public static final ModifierId hydraulic = id("hydraulic");
  public static final ModifierId lightspeed = id("lightspeed");

  // loot
  public static final ModifierId luck    = id("luck");
  public static final ModifierId looting = id("looting");
  public static final ModifierId fortune = id("fortune");
  public static final ModifierId experienced = id("experienced");

  // damage boost
  public static final ModifierId sharpness   = id("sharpness");
  public static final ModifierId swiftstrike = id("swiftstrike");
  public static final ModifierId smite       = id("smite");
  public static final ModifierId baneOfSssss = id("bane_of_sssss");
  public static final ModifierId antiaquatic = id("antiaquatic");
  public static final ModifierId killager    = id("killager");
  public static final ModifierId cooling     = id("cooling");
  public static final ModifierId pierce      = id("pierce");
  public static final ModifierId chargeAttack = id("charge_attack");

  // ranged
  public static final ModifierId power = id("power");
  public static final ModifierId punch = id("punch");
  public static final ModifierId drawback = id("drawback");
  // TODO 1.20 - change ID to arrow_pierce
  public static final ModifierId arrowPierce = id("impaling");
  public static final ModifierId quickCharge = id("quick_charge");
  public static final ModifierId trueshot = id("trueshot");
  public static final ModifierId blindshot = id("blindshot");
  public static final ModifierId bulkQuiver = id("bulk_quiver");
  public static final ModifierId trickQuiver = id("trick_quiver");
  public static final ModifierId crystalshot = id("crystalshot");
  public static final ModifierId barebow = id("barebow");
  public static final ModifierId warCharge = id("war_charge");

  // armor
  public static final ModifierId protection = id("protection");
  public static final ModifierId meleeProtection = id("melee_protection");
  public static final ModifierId fireProtection = id("fire_protection");
  public static final ModifierId projectileProtection = id("projectile_protection");
  public static final ModifierId blastProtection = id("blast_protection");
  public static final ModifierId magicProtection = id("magic_protection");
  public static final ModifierId turtleShell = id("turtle_shell");
  public static final ModifierId shulking = id("shulking");
  public static final ModifierId dragonborn = id("dragonborn");
  public static final ModifierId wings = id("wings");
  public static final ModifierId knockbackResistance = id("knockback_resistance");
  // counter
  public static final ModifierId thorns = id("thorns");
  public static final ModifierId ricochet = id("ricochet");
  // defense
  public static final ModifierId revitalizing = id("revitalizing");
  // helmet
  public static final ModifierId respiration = id("respiration");
  public static final ModifierId minimap = id("minimap");
  public static final ModifierId aquaAffinity = id("aqua_affinity");
  // chestplate
  public static final ModifierId strength = id("strength");
  // leggings
  public static final ModifierId pockets = id("pockets");
  public static final ModifierId stepUp = id("step_up");
  public static final ModifierId speedy = id("speedy");
  public static final ModifierId swiftSneak = id("swift_sneak");
  public static final ModifierId workbench = id("workbench");
  public static final ModifierId toolBelt = id("tool_belt");
  public static final ModifierId leaping = id("leaping");
  public static final ModifierId soulBelt = id("soul_belt");
  public static final ModifierId craftingTable = id("crafting_table");
  // boots
  public static final ModifierId depthStrider = id("depth_strider");
  public static final ModifierId featherFalling = id("feather_falling");
  public static final ModifierId longFall = id("long_fall");
  public static final ModifierId frostWalker = id("frost_walker");
  public static final ModifierId snowdrift = id("snowdrift");
  public static final ModifierId bouncy = id("bouncy");
  public static final ModifierId doubleJump = id("double_jump");
  // shield
  public static final ModifierId boundless = id("boundless");


  // interaction
  public static final ModifierId pathing = id("pathing");
  public static final ModifierId stripping = id("stripping");
  public static final ModifierId tilling = id("tilling");
  public static final ModifierId brushing = id("brushing");
  public static final ModifierId throwing = id("throwing");
  public static final ModifierId returning = id("returning");
  public static final ModifierId ballista = id("ballista");

  // fishing
  public static final ModifierId fishing = id("fishing");
  public static final ModifierId lure = id("lure");
  public static final ModifierId lureRod = id("lure_rod");
  public static final ModifierId grapple = id("grapple");
  public static final ModifierId collecting = id("collecting");

  // internal
  public static final ModifierId overslimeFriend = id("overslime_friend");
  public static final ModifierId snowBoots = id("snow_boots");

  // traits - tier 1
  public static final ModifierId cultivated = id("cultivated");
  public static final ModifierId economical = id("economical");
  public static final ModifierId stonebound = id("stonebound");
  public static final ModifierId jagged = id("jagged");
  public static final ModifierId tipped = id("tipped");
  public static final ModifierId stringy = id("stringy");
  public static final ModifierId unburdened = id("unburdened");
  public static final ModifierId depthProtection = id("depth_protection");
  public static final ModifierId enderclearance = id("enderclearance");
  public static final ModifierId frostshield = id("frostshield");
  public static final ModifierId woodwind = id("woodwind");
  public static final ModifierId soft = id("soft");
  public static final ModifierId spike = id("spike");
  // traits - tier 2
  public static final ModifierId overgrowth = id("overgrowth");
  public static final ModifierId searing = id("searing");
  public static final ModifierId scorching = id("scorching");
  public static final ModifierId scorchProtection = id("scorch_protection");
  public static final ModifierId antitoxin = id("antitoxin");
  public static final ModifierId airborne = id("airborne");
  public static final ModifierId skyfall = id("skyfall");
  public static final ModifierId flamestance = id("flamestance");
  public static final ModifierId entangled = id("entangled");
  public static final ModifierId stoneshield = id("stoneshield");
  public static final ModifierId amorphous = id("amorphous");
  public static final ModifierId smashing = id("smashing");
  public static final ModifierId smashingAmmo = id("smashing_ammo");
  public static final ModifierId bounce = id("bounce");
  public static final ModifierId venom = id("venom");
  // traits - tier 2 compat
  public static final ModifierId deciduous = id("deciduous");
  public static final ModifierId barkskin = id("barkskin");
  public static final ModifierId dense = id("dense");
  public static final ModifierId lustrous = id("lustrous");
  /** @deprecated use {@link #heavy} */
  @Deprecated(forRemoval = true)
  public static final ModifierId sharpweight = id("sharpweight");
  public static final ModifierId heavy = id("heavy");
  public static final ModifierId featherweight = id("featherweight");
  public static final ModifierId consecrated = id("consecrated");
  public static final ModifierId preserved = id("preserved");
  public static final ModifierId holy = id("holy");
  // traits - tier 3
  public static final ModifierId overcast = id("overcast");
  public static final ModifierId overshield = id("overshield");
  public static final ModifierId crumbling = id("crumbling");
  public static final ModifierId enhanced = id("enhanced");
  public static final ModifierId lightweight = id("lightweight");
  public static final ModifierId crystalbound = id("crystalbound");
  public static final ModifierId crystalstrike = id("crystalstrike");
  public static final ModifierId spectral = id("spectral");
  public static final ModifierId keen = id("keen");
  public static final ModifierId rebound = id("rebound");
  public static final ModifierId ductile = id("ductile");
  public static final ModifierId attractive = id("attractive");
  public static final ModifierId explosive = id("explosive");
  // traits - tier 3 compat
  public static final ModifierId maintained = id("maintained");
  public static final ModifierId temperate = id("temperate");
  /** @deprecated no longer used. Make a copy if you need it */
  @Deprecated
  public static final ModifierId invariant = id("invariant");
  public static final ModifierId solid = id("solid");
  public static final ModifierId shock = id("shock");
  public static final ModifierId raging = id("raging");
  public static final ModifierId vitalProtection = id("vital_protection");
  // traits - tier 4
  public static final ModifierId overburn = id("overburn");
  public static final ModifierId overlord = id("overlord");
  public static final ModifierId recurrentProtection = id("recurrent_protection");
  public static final ModifierId fortified = id("fortified");
  public static final ModifierId kinetic = id("kinetic");
  public static final ModifierId conductive = id("conductive");
  public static final ModifierId flameBarrier = id("flame_barrier");
  public static final ModifierId vintage = id("vintage");
  public static final ModifierId valiant = id("valiant");
  public static final ModifierId stalwart = id("stalwart");
  public static final ModifierId dragonshot = id("dragonshot");
  public static final ModifierId reclaim = id("reclaim");
  public static final ModifierId hover = id("hover");
  // traits - tier 4 compat
  public static final ModifierId temperedProtection = id("tempered_protection");
  // traits - fletching
  public static final ModifierId cheap = id("cheap");
  public static final ModifierId weak = id("weak");
  public static final ModifierId erratic = id("erratic");
  public static final ModifierId fuse = id("fuse");

  // traits - slimeskull
  public static final ModifierId mithridatism = id("mithridatism");

  // unused for now, will be reassigned later

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
