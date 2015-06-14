package tconstruct.library.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import tconstruct.library.tinkering.Category;
import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.TinkerUtil;
import tconstruct.library.utils.ToolHelper;

/**
 * Have you ever wanted to create a simple modifier that is only allowed on tools
 * but needed something else too so you couldn't derive from a single base-modifier-class?
 * Well now you can!
 */
public abstract class ModifierAspect {

  protected final IModifier parent;

  public ModifierAspect(IModifier parent) {
    this.parent = parent;
  }

  public abstract boolean canApply(ItemStack stack);

  public abstract void updateNBT(NBTTagCompound modifierTag);

  /**
   * Only applicable on tools.
   */
  public static class ToolAspect extends ModifierAspect {

    public ToolAspect(IModifier parent) {
      super(parent);
    }

    @Override
    public boolean canApply(ItemStack stack) {
      return ToolHelper.hasCategory(stack, Category.TOOL);
    }

    @Override
    public void updateNBT(NBTTagCompound modifierTag) {
      // no extra information needed
    }
  }

  /**
   * Can only be applied once
   */
  public static class SingleAspect extends ModifierAspect {

    public SingleAspect(IModifier parent) {
      super(parent);
    }

    @Override
    public boolean canApply(ItemStack stack) {
      // check if the modifier is present in the base info.
      // this is not the same as checking if the modifier has data. But should be sufficient
      NBTTagList modifiers = TagUtil.getBaseModifiersTagList(stack);
      int index = TinkerUtil.getIndexInList(modifiers, parent.getIdentifier());

      // applicable if ont found
      return index < 0;
    }

    @Override
    public void updateNBT(NBTTagCompound modifierTag) {
      // no extra information needed, taken care of by base modifier
    }
  }

  /**
   * Allows the modifier to be applied multiple times up to a max level
   */
  public static class LevelAspect extends ModifierAspect {

    private final int maxLevel;

    public LevelAspect(IModifier parent, int maxLevel) {
      super(parent);
      this.maxLevel = maxLevel;
    }


    @Override
    public boolean canApply(ItemStack stack) {
      NBTTagList modifiers = TagUtil.getModifiersTagList(stack);
      int index = TinkerUtil.getIndexInList(modifiers, parent.getIdentifier());

      if (index >= 0) {
        if (ModifierNBT.readTag(modifiers.getCompoundTagAt(index)).level >= maxLevel) {
          return false;
        }
      }

      return true;
    }

    @Override
    public void updateNBT(NBTTagCompound modifierTag) {
      ModifierNBT data = ModifierNBT.readTag(modifierTag);
      data.level++;
      data.write(modifierTag);
    }
  }
}
