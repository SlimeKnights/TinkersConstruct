package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.library.modifiers.Modifier;

public class DiamondModifier extends Modifier {

  public DiamondModifier() {
    super("Diamond");

    addItem(Items.diamond);
    addAspects(new ModifierAspect.SingleAspect(this), new ModifierAspect.DataAspect(this, EnumChatFormatting.AQUA), ModifierAspect.freeModifier);
  }

  @Override
  public void updateNBT(NBTTagCompound modifierTag) {
    // no extra data needed
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    NBTTagCompound toolTag = TagUtil.getToolTag(rootCompound);
    int durability = toolTag.getInteger(Tags.DURABILITY);
    durability += 500;

    toolTag.setInteger(Tags.DURABILITY, durability);
    TagUtil.setToolTag(rootCompound, toolTag);
  }
}
