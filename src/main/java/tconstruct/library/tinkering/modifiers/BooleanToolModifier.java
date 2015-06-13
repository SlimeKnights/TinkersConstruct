package tconstruct.library.tinkering.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import tconstruct.library.utils.TagUtil;

public abstract class BooleanToolModifier extends ToolModifier {

  public BooleanToolModifier(String identifier) {
    super(identifier);
  }

  @Override
  public boolean canApply(ItemStack stack) {
    // can only apply once
    

    return super.canApply(stack);
  }

  @Override
  public void updateNBT(NBTTagCompound modifierTag) {
    (new ModifierNBT(this)).write(modifierTag);
  }
}
