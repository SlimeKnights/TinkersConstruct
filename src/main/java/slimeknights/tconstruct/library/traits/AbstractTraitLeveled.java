package slimeknights.tconstruct.library.traits;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
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

    TinkerRegistry.registerModifierAlias(this, name);

    aspects.clear();
    this.addAspects(new ModifierAspect.LevelAspect(this, maxLevels), new ModifierAspect.DataAspect(this, color));
  }

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
    }
  }

  @Override
  public String getModifierIdentifier() {
    return name;
  }

  @Override
  public String getLocalizedName() {
    return Util.translate(LOC_Name, name);
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
