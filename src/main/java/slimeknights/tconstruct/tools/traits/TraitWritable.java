package slimeknights.tconstruct.tools.traits;

import net.minecraft.nbt.NBTTagCompound;

import slimeknights.tconstruct.library.traits.AbstractTraitLeveled;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;

public class TraitWritable extends AbstractTraitLeveled {

  public TraitWritable(int levels) {
    super("writable", String.valueOf(levels), 0xffffff, 3, 1);
  }

  @Override
  public void applyModifierEffect(NBTTagCompound rootCompound) {
    // yaaay, modifiers
    NBTTagCompound toolTag = TagUtil.getToolTag(rootCompound);
    int modifiers = toolTag.getInteger(Tags.FREE_MODIFIERS) + levels;
    toolTag.setInteger(Tags.FREE_MODIFIERS, Math.max(0, modifiers));
    TagUtil.setToolTag(rootCompound, toolTag);
  }
}
