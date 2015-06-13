package tconstruct.tools;

import tconstruct.library.tinkering.modifiers.TraitModifier;

// Only for test purposes
public class StoneboundModifier extends TraitModifier {

  public StoneboundModifier() {
    super("Stonebound", TinkerMaterials.stonebound);

    addItem("cobblestone");
  }
}
