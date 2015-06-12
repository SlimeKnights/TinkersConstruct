package tconstruct.library.tinkering.modifiers;

import net.minecraft.item.ItemStack;

import tconstruct.library.tinkering.Category;
import tconstruct.library.utils.ToolHelper;

/**
 * Additionally to the standard conditions, ToolModifiers also require a minimum amount of free modifiers
 */
public abstract class ToolModifier extends Modifier {

  public ToolModifier(String identifier) {
    super(identifier);
  }

  @Override
  public boolean canApply(ItemStack stack) {
    if (!ToolHelper.hasCategory(stack, Category.TOOL)) {
      return false;
    }

    return super.canApply(stack);
  }

  @Override
  public boolean hasTexturePerMaterial() {
    return false;
  }
}
