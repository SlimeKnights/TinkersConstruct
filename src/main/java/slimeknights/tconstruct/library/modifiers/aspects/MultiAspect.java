package slimeknights.tconstruct.library.modifiers.aspects;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.nbt.ModifierAndExtraPair;
import slimeknights.tconstruct.library.modifiers.nbt.ModifierNBT;

public class MultiAspect extends ModifierAspect {

  protected final int countPerLevel;

  protected LevelAspect levelAspect;

  public MultiAspect(IModifier parent, int maxLevel, int countPerLevel) {
    super(parent);

    this.countPerLevel = countPerLevel;

    levelAspect = new LevelAspect(parent, maxLevel);
  }

  @Override
  public boolean canApply(ItemStack stack, ItemStack original) {
    return false;
  }

  protected int getMaxForLevel(int level) {
    return countPerLevel * level;
  }

  @Override
  public ModifierAndExtraPair editNbt(ModifierNBT modifierNBT, CompoundNBT extraNBT) {
    System.out.println("hi: " + modifierNBT + " bye: " + extraNBT);
    return super.editNbt(modifierNBT, extraNBT);
  }
}
