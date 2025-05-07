package slimeknights.tconstruct.library.tools.capability;

import slimeknights.mantle.data.registry.IdAwareComponentRegistry;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.data.FloatMultiplier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.shared.TinkerAttributes;

/** All tinker data keys intended to be used by multiple modifiers */
public interface TinkerDataKeys {
  IdAwareComponentRegistry<TinkerDataKey<Integer>> INTEGER_REGISTRY = new IdAwareComponentRegistry<>("Unknown data key");
  /** @deprecated use {@link net.minecraft.core.registries.BuiltInRegistries#ATTRIBUTE} */
  @Deprecated(forRemoval = true)
  IdAwareComponentRegistry<TinkerDataKey<Float>> FLOAT_REGISTRY = new IdAwareComponentRegistry<>("Unknown data key");

  static void init() {}

  /** If this key is greater than 0, the offhand will be rendered even if empty */
  TinkerDataKey<Integer> SHOW_EMPTY_OFFHAND = TConstruct.createKey("show_empty_offhand"); // unregistered as ShowOffhandModule exists

  /** Float value for the FOV modifier, will be 1.0 if no change */
  ComputableDataKey<FloatMultiplier> FOV_MODIFIER = TConstruct.createKey("zoom_multiplier", FloatMultiplier::new);

  /** FOV modifier that only applies when not disabled in the settings menu */
  ComputableDataKey<FloatMultiplier> SCALED_FOV_MODIFIER = TConstruct.createKey("scaled_fov_multiplier", FloatMultiplier::new);

  /** @deprecated use {@link TinkerAttributes#PROTECTION_CAP} */
  @Deprecated(forRemoval = true)
  TinkerDataKey<Float> PROTECTION_CAP = floatKey("protection_cap");

  /** @deprecated use {@link TinkerAttributes#USE_ITEM_SPEED} */
  @Deprecated(forRemoval = true)
  TinkerDataKey<Float> USE_ITEM_SPEED = floatKey("use_item_speed");
  /** @deprecated use {@link TinkerAttributes#KNOCKBACK_MULTIPLIER} */
  @Deprecated(forRemoval = true)
  TinkerDataKey<Float> KNOCKBACK = floatKey("knockback");
  /** @deprecated use {@link TinkerAttributes#JUMP_BOOST} */
  @Deprecated(forRemoval = true)
  TinkerDataKey<Float> JUMP_BOOST = floatKey("jump_boost");
  /** @deprecated use {@link TinkerAttributes#MINING_SPEED_MULTIPLIER} */
  @Deprecated(forRemoval = true)
  TinkerDataKey<Float> MINING_SPEED = floatKey("mining_speed");
  /** @deprecated use {@link TinkerAttributes#EXPERIENCE_MULTIPLIER} */
  @Deprecated(forRemoval = true)
  TinkerDataKey<Float> EXPERIENCE = floatKey("experience");
  /** @deprecated use {@link TinkerAttributes#CRITICAL_DAMAGE} */
  @Deprecated(forRemoval = true)
  TinkerDataKey<Float> CRITICAL_DAMAGE = floatKey("critical_damage");
  /** @deprecated use {@link TinkerAttributes#BAD_EFFECT_DURATION} */
  @Deprecated(forRemoval = true)
  TinkerDataKey<Float> BAD_EFFECT_DURATION = floatKey("bad_effect_duration");
  /** @deprecated use {@link TinkerAttributes#GOOD_EFFECT_DURATION} */
  @Deprecated(forRemoval = true)
  TinkerDataKey<Float> GOOD_EFFECT_DURATION = floatKey("good_effect_duration");
  /** @deprecated use {@link TinkerAttributes#CROUCH_DAMAGE_MULTIPLIER} */
  @Deprecated(forRemoval = true)
  TinkerDataKey<Float> CROUCH_DAMAGE = floatKey("crouch_damage");

  /** Crystalstrike level for knockback restriction */
  TinkerDataKey<Integer> CRYSTALSTRIKE = intKey("crystalstrike_knockback");

  /** Soul belt level for hotbar preservation */
  TinkerDataKey<Integer> SOUL_BELT = intKey("soul_belt");


  /** Creates and registers an integer key */
  private static TinkerDataKey<Integer> intKey(String name) {
    return INTEGER_REGISTRY.register(TConstruct.createKey(name));
  }

  /** Creates and registers a float key */
  @Deprecated(forRemoval = true)
  private static TinkerDataKey<Float> floatKey(String name) {
    return FLOAT_REGISTRY.register(TConstruct.createKey(name));
  }
}
