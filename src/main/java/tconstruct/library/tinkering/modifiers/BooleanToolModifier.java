package tconstruct.library.tinkering.modifiers;

import net.minecraft.nbt.NBTTagCompound;

public abstract class BooleanToolModifier extends ToolModifier {

  public BooleanToolModifier(String identifier) {
    super(identifier);
  }

  @Override
  public void updateNBT(NBTTagCompound modifierTag) {
    ModifierNBT.Boolean.write(true, modifierTag, identifier);
  }
}
