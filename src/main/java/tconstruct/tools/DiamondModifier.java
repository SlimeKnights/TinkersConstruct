package tconstruct.tools;

import net.minecraft.nbt.NBTTagCompound;

import tconstruct.library.tinkering.modifiers.ModifierNBT;
import tconstruct.library.tinkering.modifiers.ToolModifier;

public class DiamondModifier extends ToolModifier {

  public DiamondModifier() {
    super("Diamond");
  }

  @Override
  public void apply(NBTTagCompound modifierTag) {
    ModifierNBT.Boolean data = ModifierNBT.Boolean.read(modifierTag, identifier);
    data.status = true; // we don't actually use the old value, if it exists. but meh.

    data.write(modifierTag);
  }
}
