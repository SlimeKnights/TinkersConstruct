package tconstruct.library.tools;


import tconstruct.library.ITinkerable;
import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.Tags;

/**
 * The base for each Tinker tool.
 */
public abstract class ToolCore implements ITinkerable {

  @Override
  public String getTagName() {
    return TagUtil.TAG_BASE;
  }
}
