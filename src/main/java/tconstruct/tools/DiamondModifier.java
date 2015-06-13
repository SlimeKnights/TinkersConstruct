package tconstruct.tools;

import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;

import tconstruct.library.tinkering.modifiers.BooleanToolModifier;
import tconstruct.library.tinkering.modifiers.ModifierNBT;
import tconstruct.library.tinkering.modifiers.ToolModifier;
import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.Tags;

public class DiamondModifier extends BooleanToolModifier {

  public DiamondModifier() {
    super("Diamond");

    addItem(Items.diamond);
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
