package slimeknights.tconstruct.library.tools.capability;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;

/** All tinker data keys intended to be used by multiple modifiers */
public interface TinkerDataKeys {
  /** If this key is greater than 0, the offhand will be rendered even if empty */
  TinkerDataKey<Integer> SHOW_EMPTY_OFFHAND = TConstruct.createKey("show_empty_offhand");
}
