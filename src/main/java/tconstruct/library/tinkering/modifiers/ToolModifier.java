package tconstruct.library.tinkering.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.ToolTagUtil;

public class ToolModifier extends Modifier {

  public int requiredModifiers = 1;

  public ToolModifier(String identifier) {
    super(identifier);
  }

  @Override
  public boolean canApply(ItemStack stack) {
    NBTTagCompound toolTag = TagUtil.getToolTagSafe(stack);
    if (ToolTagUtil.getFreeModifiers(toolTag) < requiredModifiers) {
      // also returns false if the tooltag is missing
      return false;
    }

    // we assume each modifier can only be applied once
    NBTTagCompound tag = TagUtil.getModifiersTag(stack);

    return !tag.hasKey(getIdentifier());
  }

  @Override
  public void apply(ItemStack stack) {

  }

  @Override
  public boolean hasTexturePerMaterial() {
    return false;
  }
}
