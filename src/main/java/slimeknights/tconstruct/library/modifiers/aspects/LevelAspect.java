package slimeknights.tconstruct.library.modifiers.aspects;

import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.nbt.ModifierNBT;

public class LevelAspect extends ModifierAspect {

  private final int maxLevel;

  public LevelAspect(IModifier parent, int maxLevel) {
    super(parent);
    this.maxLevel = maxLevel;
  }

  @Override
  public boolean canApply(ItemStack stack, ItemStack original) {
    return true;
  }

  @Override
  public ModifierNBT editNBT(ModifierNBT modifierNBT) {
    return modifierNBT.withLevel(modifierNBT.level + 1);
  }
}
