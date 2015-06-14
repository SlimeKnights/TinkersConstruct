package tconstruct.tools;

import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import tconstruct.library.modifiers.Modifier;
import tconstruct.library.modifiers.ModifierAspect;
import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.Tags;

public class DiamondModifier extends Modifier {

  public DiamondModifier() {
    super("Diamond");

    addItem(Items.diamond);
    addAspects(new ModifierAspect.SingleAspect(this), new ModifierAspect.DataAspect(this, EnumChatFormatting.AQUA));
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
