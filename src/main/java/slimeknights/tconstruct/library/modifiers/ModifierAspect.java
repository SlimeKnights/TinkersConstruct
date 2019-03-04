package slimeknights.tconstruct.library.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;

/**
 * Have you ever wanted to create a simple modifier that is only allowed on tools
 * but needed something else too so you couldn't derive from a single base-modifier-class?
 * Well now you can!
 */
public abstract class ModifierAspect {

  public static final ModifierAspect freeModifier = new FreeModifierAspect(1);

  public static final ModifierAspect toolOnly = new CategoryAspect(Category.TOOL);
  public static final ModifierAspect harvestOnly = new CategoryAspect(Category.HARVEST);
  public static final ModifierAspect weaponOnly = new CategoryAspect(Category.WEAPON);
  public static final ModifierAspect aoeOnly = new CategoryAspect(Category.AOE);
  public static final ModifierAspect projectileOnly = new CategoryAspect(Category.PROJECTILE);

  protected final IModifier parent;

  protected ModifierAspect() {
    this.parent = null;
  }

  public ModifierAspect(IModifier parent) {
    this.parent = parent;
  }

  public abstract boolean canApply(ItemStack stack, ItemStack original) throws TinkerGuiException;

  public abstract void updateNBT(NBTTagCompound root, NBTTagCompound modifierTag);

  /**
   * The modifier requires sufficient free modifier sto be present.
   */
  public static class FreeModifierAspect extends ModifierAspect {

    private final int requiredModifiers;

    public FreeModifierAspect(int requiredModifiers) {
      this.requiredModifiers = requiredModifiers;
    }

    protected FreeModifierAspect(IModifier parent, int requiredModifiers) {
      super(parent);
      this.requiredModifiers = requiredModifiers;
    }

    @Override
    public boolean canApply(ItemStack stack, ItemStack original) throws TinkerGuiException {
      NBTTagCompound toolTag = TagUtil.getToolTag(stack);
      if(ToolHelper.getFreeModifiers(stack) < requiredModifiers) {
        String error = I18n.translateToLocalFormatted("gui.error.not_enough_modifiers", requiredModifiers);
        // also returns false if the tooltag is missing
        throw new TinkerGuiException(error);
      }

      return true;
    }

    @Override
    public void updateNBT(NBTTagCompound root, NBTTagCompound modifierTag) {
      // substract the modifiers
      NBTTagCompound toolTag = TagUtil.getToolTag(root);
      int modifiers = toolTag.getInteger(Tags.FREE_MODIFIERS) - requiredModifiers;
      toolTag.setInteger(Tags.FREE_MODIFIERS, Math.max(0, modifiers));

      // and increase the count of used modifiers
      int usedModifiers = TagUtil.getBaseModifiersUsed(root);
      usedModifiers += requiredModifiers;
      TagUtil.setBaseModifiersUsed(root, usedModifiers);
    }
  }

  public static class FreeFirstModifierAspect extends FreeModifierAspect {

    public FreeFirstModifierAspect(IModifier parent, int requiredModifiers) {
      super(parent, requiredModifiers);
    }

    @Override
    public boolean canApply(ItemStack stack, ItemStack original) throws TinkerGuiException {
      // can always apply if the parent already has the modifier
      if(TinkerUtil.hasModifier(TagUtil.getTagSafe(stack), parent.getIdentifier())) {
        return true;
      }

      // otherwise he requires free modifiers
      return super.canApply(stack, original);
    }

    @Override
    public void updateNBT(NBTTagCompound root, NBTTagCompound modifierTag) {
      // same as above, if already present we don't need to reduce the free modifiers
      if(modifierTag.hasKey("modifierUsed")) {
        return;
      }

      super.updateNBT(root, modifierTag);
      modifierTag.setBoolean("modifierUsed", true);
    }
  }

  /**
   * Saves the base data of the modifier onto the tool.
   * Any modifier not having this has to take care of it itself.
   */
  public static class DataAspect extends ModifierAspect {

    private final int color;

    public DataAspect(IModifier parent, TextFormatting color) {
      this(parent, Util.enumChatFormattingToColor(color));
    }

    public DataAspect(IModifier parent, int color) {
      super(parent);
      this.color = color;
    }

    public <T extends IModifier & IModifierDisplay> DataAspect(T parent) {
      super(parent);
      this.color = parent.getColor();
    }

    @Override
    public boolean canApply(ItemStack stack, ItemStack original) {
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

    protected final int countPerLevel;

    protected DataAspect dataAspect;
    protected LevelAspect levelAspect;
    protected FreeModifierAspect freeModifierAspect;

    public <T extends IModifier & IModifierDisplay> MultiAspect(T parent, int maxLevel, int countPerLevel, int modifiersNeeded) {
      this(parent, parent.getColor(), maxLevel, countPerLevel, modifiersNeeded);
    }

    // multiple levels, once every time the maximum is reached
    public MultiAspect(IModifier parent, int color, int maxLevel, int countPerLevel, int modifiersNeeded) {
      super(parent);
      this.countPerLevel = countPerLevel;

      dataAspect = new DataAspect(parent, color);
      freeModifierAspect = new FreeModifierAspect(modifiersNeeded);
      levelAspect = new LevelAspect(parent, maxLevel);
    }

    // single-level
    public MultiAspect(IModifier parent, int color, int count) {
      this(parent, color, 1, count, 1);
    }

    protected int getMaxForLevel(int level) {
      return countPerLevel * level;
    }

    @Override
    public boolean canApply(ItemStack stack, ItemStack original) throws TinkerGuiException {
      // check if the threshold has been reached
      NBTTagCompound modifierTag = TinkerUtil.getModifierTag(stack, parent.getIdentifier());
      ModifierNBT.IntegerNBT data = getData(modifierTag);

      // the current level is full / level is 0
      if(data.current >= getMaxForLevel(data.level)) {
        // can we even apply a new level?
        if(!levelAspect.canApply(stack, original)) {
          return false;
        }

        // enough modifiers for another level?
        if(!freeModifierAspect.canApply(stack, original)) {
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
      }

      // always update max in case it changed since it got saved
      data.max = getMaxForLevel(data.level);

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
   * Only applicable on tools with the given categories.
   */
  public static class CategoryAspect extends ModifierAspect {

    protected final Category[] category;

    public CategoryAspect(Category... category) {
      this.category = category;
    }

    @Override
    public boolean canApply(ItemStack stack, ItemStack original) {
      for(Category cat : category) {
        if(!ToolHelper.hasCategory(stack, cat)) {
          return false;
        }
      }

      return true;
    }

    @Override
    public void updateNBT(NBTTagCompound root, NBTTagCompound modifierTag) {
      // no extra information needed
    }
  }

  /**
   * Applicable on all tools that have at least one of those categories
   */
  public static class CategoryAnyAspect extends CategoryAspect {

    public CategoryAnyAspect(Category... category) {
      super(category);
    }

    @Override
    public boolean canApply(ItemStack stack, ItemStack original) {
      for(Category cat : category) {
        if(ToolHelper.hasCategory(stack, cat)) {
          return true;
        }
      }

      return false;
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
    public boolean canApply(ItemStack stack, ItemStack original) throws TinkerGuiException {
      // check if the modifier is present in the base info.
      // this is not the same as checking if the modifier has data. But should be sufficient

      if(TinkerUtil.hasModifier(TagUtil.getTagSafe(stack), parent.getIdentifier())) {
        // check if original already had it too
        if(TinkerUtil.hasModifier(TagUtil.getTagSafe(original), parent.getIdentifier())) {
          // error, can't apply if it already had it
          throw new TinkerGuiException(I18n.translateToLocalFormatted("gui.error.single_modifier",
                                                                      parent.getLocalizedName()));
        }
        else {
          // original didn't have it, we can apply it once therefore, no error
          return false;
        }
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
    public boolean canApply(ItemStack stack, ItemStack original) throws TinkerGuiException {
      int levelNew = ModifierNBT.readTag(TinkerUtil.getModifierTag(stack, parent.getIdentifier())).level;
      int levelOld = ModifierNBT.readTag(TinkerUtil.getModifierTag(original, parent.getIdentifier())).level;

      // only 1 level per application
      // original and stack are equal for the first application, any multiple applications therefore yield >0
      if(levelNew - levelOld > 0) {
        return false;
      }

      // new level would be above max level
      if(levelNew >= maxLevel) {
        throw new TinkerGuiException(I18n.translateToLocalFormatted("gui.error.max_level_modifier", parent.getLocalizedName()));
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
