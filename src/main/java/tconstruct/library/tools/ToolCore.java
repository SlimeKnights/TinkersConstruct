package tconstruct.library.tools;


import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import tconstruct.library.ITinkerable;
import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.Tags;

/**
 * The base for each Tinker tool.
 */
public abstract class ToolCore implements ITinkerable {

  public final PartMaterialWrapper[] requiredComponents;

  public ToolCore(PartMaterialWrapper[] requiredComponents) {
    this.requiredComponents = requiredComponents;
  }

  public boolean validComponent(int slot, ItemStack stack) {
    if(slot > requiredComponents.length || slot < 0)
      return false;

    return requiredComponents[slot].isValid(stack);
  }

  @Override
  public String getTagName() {
    return Tags.TOOL_BASE;
  }
}
