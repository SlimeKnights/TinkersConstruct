package slimeknights.tconstruct.library.utils;

/** @deprecated use {@link slimeknights.mantle.client.TooltipKey} */
@Deprecated
public enum TooltipKey {
  /** Tooltip with neither shift nor control */
  NORMAL,
  /** Tooltip with shift held */
  SHIFT,
  /** Tooltip with control held */
  CONTROL,
  /** Tooltip with alt held */
  ALT,
  /** Tooltip key cannot be determined, typically caused by being on a server */
  UNKNOWN;

  /** Converts this to the mantle tooltip key */
  public slimeknights.mantle.client.TooltipKey asMantle() {
    return switch (this) {
      case NORMAL -> slimeknights.mantle.client.TooltipKey.NORMAL;
      case SHIFT -> slimeknights.mantle.client.TooltipKey.SHIFT;
      case CONTROL -> slimeknights.mantle.client.TooltipKey.CONTROL;
      case ALT -> slimeknights.mantle.client.TooltipKey.ALT;
      case UNKNOWN -> slimeknights.mantle.client.TooltipKey.UNKNOWN;
    };
  }

  /** Gets the tooltip key from the mantle enum */
  public static TooltipKey fromMantle(slimeknights.mantle.client.TooltipKey key) {
    return switch (key) {
      case NORMAL -> NORMAL;
      case SHIFT -> SHIFT;
      case CONTROL -> CONTROL;
      case ALT -> ALT;
      case UNKNOWN -> UNKNOWN;
    };
  }

  /** Common operation is wanting to cancel when shift or unknown */
  public boolean isShiftOrUnknown() {
    return this == SHIFT || this == UNKNOWN;
  }
}
