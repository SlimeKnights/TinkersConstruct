package slimeknights.tconstruct.library.utils;

/** Options for which tooltip is being used on an item */
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

  /** Common operation is wanting to cancel when shift or unknown */
  public boolean isShiftOrUnknown() {
    return this == SHIFT || this == UNKNOWN;
  }
}
