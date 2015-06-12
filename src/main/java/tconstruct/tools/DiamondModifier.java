package tconstruct.tools;

import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;

import tconstruct.library.tinkering.modifiers.ModifierNBT;
import tconstruct.library.tinkering.modifiers.ToolModifier;
import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.Tags;

public class DiamondModifier extends ToolModifier {

  public DiamondModifier() {
    super("Diamond");

    addItem(Items.diamond);
  }

  @Override
  public void updateNBT(NBTTagCompound modifierTag) {
    ModifierNBT.Boolean data = ModifierNBT.Boolean.read(modifierTag, identifier);
    data.status = true; // we don't actually use the old value, if it exists. but meh.

    data.write(modifierTag);
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    NBTTagCompound toolTag = TagUtil.getTagSafe(rootCompound, Tags.TOOL_DATA);
    int durability = toolTag.getInteger(Tags.DURABILITY);
    durability += 500;

    toolTag.setInteger(Tags.DURABILITY, durability);
    rootCompound.setTag(Tags.TOOL_DATA, toolTag);
  }
}
