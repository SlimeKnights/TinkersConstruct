package tconstruct.library.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.TinkerUtil;

public abstract class BooleanToolModifier extends ToolModifier {

  public BooleanToolModifier(String identifier) {
    super(identifier);
  }

  @Override
  public boolean canApply(ItemStack stack) {
    // can only apply once
    NBTTagList modifiers = TagUtil.getBaseModifiersTagList(stack);
    int index = TinkerUtil.getIndexInList(modifiers, identifier);

    // modifier already present
    if (index >= 0) {
      return false;
    }

    return super.canApply(stack);
  }

  @Override
  public void updateNBT(NBTTagCompound modifierTag) {
    (new ModifierNBT(this)).write(modifierTag);
  }
}
