package slimeknights.tconstruct.library.traits;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

/**
 * A Trait that has multiple levels and can be applied multiple times
 * Effectively it's multiple traits that all use one single modifier.
 */
public abstract class AbstractTraitLeveled extends AbstractTrait {

  protected final String name;
  protected final int levels;

  public AbstractTraitLeveled(String identifier, int color, int maxLevels, int levels) {
    this(identifier, String.valueOf(levels), color, maxLevels, levels);
  }

  public AbstractTraitLeveled(String identifier, String suffix, int color, int maxLevels, int levels) {
    super(identifier + suffix, color);
    this.name = identifier;

    this.levels = levels;

    // don't overwrite the modifier alias if one with less levels already is present
    // we basically always want the level1 one to be associated with the modifier used
    IModifier modifier = TinkerRegistry.getModifier(name);
    if(modifier != null) {
      if(modifier instanceof AbstractTraitLeveled && ((AbstractTraitLeveled) modifier).levels > this.levels) {
        TinkerRegistry.registerModifierAlias(this, name);
      }
    }
    else {
      TinkerRegistry.registerModifierAlias(this, name);
    }

    aspects.clear();
    this.addAspects(new ModifierAspect.LevelAspect(this, maxLevels), new ModifierAspect.DataAspect(this, color));
  }

  @Override
  public void updateNBTforTrait(NBTTagCompound modifierTag, int newColor) {
    super.updateNBTforTrait(modifierTag, newColor);

    ModifierNBT data = ModifierNBT.readTag(modifierTag);
    data.level = 0; // handled by applyEffect in this case
    data.write(modifierTag);
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    super.applyEffect(rootCompound, modifierTag);

    // traits are the only things that can modify here safely, since they're only ever called on tool creation
    NBTTagList tagList = TagUtil.getModifiersTagList(rootCompound);
    int index = TinkerUtil.getIndexInCompoundList(tagList, getModifierIdentifier());

    NBTTagCompound tag = new NBTTagCompound();
    if(index > -1) {
      tag = tagList.getCompoundTagAt(index);
    }
    else {
      index = tagList.tagCount();
      tagList.appendTag(tag);
    }
    if(!tag.getBoolean(identifier)) {
      ModifierNBT data = ModifierNBT.readTag(tag);
      data.level += levels;
      data.write(tag);
      tag.setBoolean(identifier, true);
      tagList.set(index, tag);
      TagUtil.setModifiersTagList(rootCompound, tagList);

      applyModifierEffect(rootCompound);
    }
  }

  /**
   * Called when the trait gets applied. Called for each application/level of the trait.
   * Only called once per specific trait (e.g. Writable1 and Writable2) but multiple times overall (per specific trait present)
   *
   * Unlike Modifiers that get applied with the total result, you can do things incrementally here.
   */
  public void applyModifierEffect(NBTTagCompound rootCompound) {

  }

  @Override
  public String getModifierIdentifier() {
    return name;
  }

  @Override
  public String getLocalizedName() {
    String locName = Util.translate(LOC_Name, name);
    if(levels > 1) {
      locName += " " + TinkerUtil.getRomanNumeral(levels);
    }
    return locName;
  }

  @Override
  public String getLocalizedDesc() {
    return Util.translate(LOC_Desc, name);
  }

  @Override
  public String getTooltip(NBTTagCompound modifierTag, boolean detailed) {
    return getLeveledTooltip(modifierTag, detailed);
  }
}
