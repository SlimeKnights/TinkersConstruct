package tconstruct.library.tools;

/*
- Tools are composed out of parts
- Part-Count can be arbitrary and part behaviour is not pre-determined
  > Though first part = head, second part = handle, third part = accessory is standard behaviour
- Each part has requirements/behaviour
  > is used for main stats (head), is used for secondary stats/multipliers (handle), is used for additional stats (accessory,...)
  > Part requires a special Material Stat Type (e.g. Bowstring)

 */

import tconstruct.library.ITinkerItem;
import tconstruct.library.utils.TagUtil;

public abstract class ToolCore implements ITinkerItem {

  @Override
  public String getTagName() {
    return TagUtil.TAG_BASE;
  }
}
