package slimeknights.tconstruct.library.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TinkerUtil;

/**
 * Represents a modifier that has trait-logic
 * Modifier can have multiple levels.
 * Since this is intended for modifiers it uses a modifier
 */
public class ModifierTrait extends AbstractTrait {

  protected final int maxLevel;

  public ModifierTrait(String identifier, int color) {
    this(identifier, color, 0, 0);
  }

  public ModifierTrait(String identifier, int color, int maxLevel, int countPerLevel) {
    super(identifier, color);

    // register the modifier trait
    TinkerRegistry.addTrait(this);

    this.maxLevel = maxLevel;
    this.aspects.clear();

    if(maxLevel > 0 && countPerLevel > 0) {
      addAspects(new ModifierAspect.MultiAspect(this, color, maxLevel, countPerLevel, 1));
    }
    else {
      addAspects(new ModifierAspect.DataAspect(this, color), ModifierAspect.freeModifier);
      if(maxLevel > 0) {
        addAspects(new ModifierAspect.LevelAspect(this, maxLevel));
      }
    }
  }

  @Override
  public boolean canApplyCustom(ItemStack stack) {
    // not present yet, ok
    if(super.canApplyCustom(stack)) {
      return true;
    }
    // no max level
    else if(maxLevel == 0) {
      return false;
    }

    // already present, limit by level
    NBTTagCompound tag = TinkerUtil.getModifierTag(stack, identifier);

    return ModifierNBT.readTag(tag).level <= maxLevel;
  }
}
