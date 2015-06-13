package tconstruct.tools;

import net.minecraft.util.EnumChatFormatting;

import tconstruct.library.modifiers.TraitModifier;

// Only for test purposes
public class StoneboundModifier extends TraitModifier {

  public StoneboundModifier() {
    super(TinkerMaterials.stonebound, EnumChatFormatting.DARK_GRAY);

    addItem("cobblestone");
  }
}
