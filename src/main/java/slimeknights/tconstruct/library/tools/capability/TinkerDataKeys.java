package slimeknights.tconstruct.library.tools.capability;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.data.FloatMultiplier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;

/** All tinker data keys intended to be used by multiple modifiers */
public interface TinkerDataKeys {
  /** If this key is greater than 0, the offhand will be rendered even if empty */
  TinkerDataKey<Integer> SHOW_EMPTY_OFFHAND = TConstruct.createKey("show_empty_offhand");

  /** If this key is greater than 0, the entity has aqua affinity */
	TinkerDataKey<Integer> AQUA_AFFINITY = TConstruct.createKey("aqua_affinity");

	/** Float value for the FOV modifier, will be 1.0 if no change */
	ComputableDataKey<FloatMultiplier> FOV_MODIFIER = TConstruct.createKey("zoom_multiplier", FloatMultiplier::new);
}
