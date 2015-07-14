package tconstruct.library.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import tconstruct.library.tinkering.Category;
import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.Tags;
import tconstruct.library.utils.TinkerUtil;
import tconstruct.library.utils.ToolHelper;
import tconstruct.library.utils.ToolTagUtil;

/**
 * Have you ever wanted to create a simple modifier that is only allowed on tools
 * but needed something else too so you couldn't derive from a single base-modifier-class?
 * Well now you can!
 */
public abstract class ModifierAspect {

  public static final ModifierAspect freeModifier = new FreeModifierAspect(1);

  public static final ModifierAspect toolOnly = new ToolAspect();

  protected final IModifier parent;

  protected ModifierAspect() {
    this.parent = null;
  }

  public ModifierAspect(IModifier parent) {
    this.parent = parent;
  }

  public abstract boolean canApply(ItemStack stack) throws ModifyException;

  public abstract void updateNBT(NBTTagCompound root, NBTTagCompound modifierTag);

  /**
   * The modifier requires sufficient free modifier sto be present.
   */
  public static class FreeModifierAspect extends ModifierAspect {

    private final int requiredModifiers;

    public FreeModifierAspect(int requiredModifiers) {
      this.requiredModifiers = requiredModifiers;
    }

    @Override
    public boolean canApply(ItemStack stack) throws ModifyException {
      NBTTagCompound toolTag = TagUtil.getToolTag(stack);
      if(ToolTagUtil.getFreeModifiers(toolTag) < requiredModifiers) {
        String error = StatCollector.translateToLocalFormatted("gui.error.notEnoughModifiers", requiredModifiers);
        // also returns false if the tooltag is missing
        throw new ModifyException(error);
      }

      return true;
    }

    @Override
    public void updateNBT(NBTTagCompound root, NBTTagCompound modifierTag) {
      // substract the modifiers
      NBTTagCompound toolTag = TagUtil.getToolTag(root);
      int modifiers = ToolTagUtil.getFreeModifiers(toolTag) - requiredModifiers;
      toolTag.setInteger(Tags.FREE_MODIFIERS, Math.max(0, modifiers));

      // and increase the count of used modifiers
      int usedModifiers = TagUtil.getBaseModifiersUsed(root);
      usedModifiers += requiredModifiers;
      TagUtil.setBaseModifiersUsed(root, usedModifiers);
    }
  }

  /**
   * Saves the base data of the modifier onto the tool.
   * Any modifier not having this has to take care of it itself.
   */
  public static class DataAspect extends ModifierAspect {

    private final EnumChatFormatting color;

    public DataAspect(IModifier parent, EnumChatFormatting color) {
      super(parent);
      this.color = color;
    }

    @Override
    public boolean canApply(ItemStack stack) {
      // can always apply
      return true;
    }

    @Override
    public void updateNBT(NBTTagCompound root, NBTTagCompound modifierTag) {
      ModifierNBT data = ModifierNBT.readTag(modifierTag);
      data.identifier = parent.getIdentifier();
      data.color = color;
      data.write(modifierTag);
    }
  }

  /**
   * The modifier can be applied several times per modifier used.
   */
  public static class MultiAspect extends ModifierAspect {

    private final int countPerLevel;

    private final DataAspect dataAspect;
    private final LevelAspect levelAspect;
    private final FreeModifierAspect freeModifierAspect;

    // multiple levels, once every time the maximum is reached
    public MultiAspect(IModifier parent, EnumChatFormatting color, int maxLevel, int countPerLevel, int modifiersNeeded) {
      super(parent);
      this.countPerLevel = countPerLevel;

      dataAspect = new DataAspect(parent, color);
      freeModifierAspect = new FreeModifierAspect(modifiersNeeded);
      levelAspect = new LevelAspect(parent, maxLevel);
    }

    // single-level
    public MultiAspect(IModifier parent, EnumChatFormatting color, int count) {
      this(parent, color, 1, count, 1);
    }

    protected int getMaxForLevel(int level) {
      return countPerLevel * level;
    }

    @Override
    public boolean canApply(ItemStack stack) throws ModifyException {
      // check if the threshold has been reached
      NBTTagCompound modifierTag = TinkerUtil.getModifierTag(stack, parent.getIdentifier());
      ModifierNBT.IntegerNBT data = getData(modifierTag);

      // the current level is full / level is 0
      if(data.current >= getMaxForLevel(data.level)) {
        // enough modifiers for another level?
        if(!freeModifierAspect.canApply(stack)) {
          return false;
        }

        // can we even apply a new level?
        if(!levelAspect.canApply(stack)) {
          return false;
        }
      }

      // we have not maxed out this level OR we have enough modifiers and can add a new level
      return true;
    }

    @Override
    public void updateNBT(NBTTagCompound root, NBTTagCompound modifierTag) {
      // simple data
      dataAspect.updateNBT(root, modifierTag);

      // increase the current level progress
      ModifierNBT.IntegerNBT data = getData(modifierTag);

      // new level?
      if(data.current >= getMaxForLevel(data.level)) {
        // remove modifiers
        freeModifierAspect.updateNBT(root, modifierTag);
        // add a level
        levelAspect.updateNBT(root, modifierTag);

        // update max. but to do so, we have to re-read the changed data again
        data = getData(modifierTag);
        data.max = getMaxForLevel(data.level);
      }

      // increase the level progress
      data.current++;
      data.write(modifierTag);
    }

    private ModifierNBT.IntegerNBT getData(NBTTagCompound tag) {
      ModifierNBT.IntegerNBT data = ModifierNBT.readInteger(tag);

      if(data.max == 0) {
        data.max = getMaxForLevel(data.level);
      }

      return data;
    }
  }

  /**
   * Only applicable on tools.
   */
  public static class ToolAspect extends ModifierAspect {

    @Override
    public boolean canApply(ItemStack stack) {
      return ToolHelper.hasCategory(stack, Category.TOOL);
    }

    @Override
    public void updateNBT(NBTTagCompound root, NBTTagCompound modifierTag) {
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
    public boolean canApply(ItemStack stack) throws ModifyException {
      // check if the modifier is present in the base info.
      // this is not the same as checking if the modifier has data. But should be sufficient
      NBTTagList modifiers = TagUtil.getBaseModifiersTagList(stack);
      int index = TinkerUtil.getIndexInList(modifiers, parent.getIdentifier());

      if(index >= 0) {
        throw new ModifyException(StatCollector.translateToLocalFormatted("gui.error.SingleModifier",
                                                                          parent.getLocalizedName()));
      }

      // applicable if not found
      return true;
    }

    @Override
    public void updateNBT(NBTTagCompound root, NBTTagCompound modifierTag) {
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
    public boolean canApply(ItemStack stack) throws ModifyException {
      NBTTagList modifiers = TagUtil.getModifiersTagList(stack);
      int index = TinkerUtil.getIndexInList(modifiers, parent.getIdentifier());

      if(index >= 0) {
        if(ModifierNBT.readTag(modifiers.getCompoundTagAt(index)).level >= maxLevel) {
          throw new ModifyException(StatCollector.translateToLocalFormatted("gui.error.MaxLevelModifier", parent.getLocalizedName()));
        }
      }

      return true;
    }

    @Override
    public void updateNBT(NBTTagCompound root, NBTTagCompound modifierTag) {
      ModifierNBT data = ModifierNBT.readTag(modifierTag);
      data.level++;
      data.write(modifierTag);
    }
  }
}
