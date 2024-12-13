package slimeknights.tconstruct.library.tools.capability;

import slimeknights.mantle.data.registry.IdAwareComponentRegistry;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.data.FloatMultiplier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;

/** All tinker data keys intended to be used by multiple modifiers */
public interface TinkerDataKeys {
  IdAwareComponentRegistry<TinkerDataKey<Integer>> INTEGER_REGISTRY = new IdAwareComponentRegistry<>("Unknown data key");
  IdAwareComponentRegistry<TinkerDataKey<Float>> FLOAT_REGISTRY = new IdAwareComponentRegistry<>("Unknown data key");

  static void init() {}

  /** If this key is greater than 0, the offhand will be rendered even if empty */
  TinkerDataKey<Integer> SHOW_EMPTY_OFFHAND = TConstruct.createKey("show_empty_offhand"); // unregistered as ShowOffhandModule exists

	/** Float value for the FOV modifier, will be 1.0 if no change */
	ComputableDataKey<FloatMultiplier> FOV_MODIFIER = TConstruct.createKey("zoom_multiplier", FloatMultiplier::new);

	/** FOV modifier that only applies when not disabled in the settings menu */
	ComputableDataKey<FloatMultiplier> SCALED_FOV_MODIFIER = TConstruct.createKey("scaled_fov_multiplier", FloatMultiplier::new);

	/** Cap modifier. Cap defaults to 20, but can be increased up to 23.75 and decreased down to 0 */
	TinkerDataKey<Float> PROTECTION_CAP = floatKey("protection_cap");

	/** Bonus to add to movement speed when using items, used in {@link slimeknights.tconstruct.tools.data.ModifierIds#meleeProtection} */
	TinkerDataKey<Float> USE_ITEM_SPEED = floatKey("use_item_speed");
	/** Knockback bonus */
	TinkerDataKey<Float> KNOCKBACK = floatKey("knockback");
  /** Jump height bonus in blocks */
  TinkerDataKey<Float> JUMP_BOOST = floatKey("jump_boost");
  /** Player modifier data key for mining speed multiplier as an additive percentage boost on mining speed. Used for armor haste. */
  TinkerDataKey<Float> MINING_SPEED = floatKey("mining_speed");
  /** Multiplier for experience drops from events */
  TinkerDataKey<Float> EXPERIENCE = floatKey("experience");
  /** Percentage boost to critial hits for any airborne attacker, used for {@link slimeknights.tconstruct.tools.data.ModifierIds#dragonborn} */
  TinkerDataKey<Float> CRITICAL_DAMAGE = floatKey("critical_damage");
  /** Percentage boost to negative potion effects, used for {@link slimeknights.tconstruct.tools.data.ModifierIds#magicProtection} */
  TinkerDataKey<Float> BAD_EFFECT_DURATION = floatKey("bad_effect_duration");
  /** Percentage boost to positive potion effects */
  TinkerDataKey<Float> GOOD_EFFECT_DURATION = floatKey("good_effect_duration");
  /** Percentage boost to damage while crouching, used by {@link slimeknights.tconstruct.tools.data.ModifierIds#shulking} */
  TinkerDataKey<Float> CROUCH_DAMAGE = floatKey("crouch_damage");
  /** Percentage boost to explosion knockback, used by {@link slimeknights.tconstruct.tools.data.ModifierIds#blastProtection} */
  TinkerDataKey<Float> EXPLOSION_KNOCKBACK = floatKey("explosion_knockback");

  /** Crystalstrike level for knockback restriction */
  TinkerDataKey<Integer> CRYSTALSTRIKE = intKey("crystalstrike_knockback");

  /** Soul belt level for hotbar preservation */
  TinkerDataKey<Integer> SOUL_BELT = intKey("soul_belt");


  /** Creates and registers an integer key */
  private static TinkerDataKey<Integer> intKey(String name) {
    return INTEGER_REGISTRY.register(TConstruct.createKey(name));
  }

  /** Creates and registers a float key */
  private static TinkerDataKey<Float> floatKey(String name) {
    return FLOAT_REGISTRY.register(TConstruct.createKey(name));
  }
}
