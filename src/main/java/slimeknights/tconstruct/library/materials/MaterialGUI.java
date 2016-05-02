package slimeknights.tconstruct.library.materials;

import net.minecraft.util.text.TextFormatting;

public class MaterialGUI extends Material {

  public MaterialGUI(String identifier) {
    super(identifier, 0xffffff);
  }

  @Override
  public boolean isHidden() {
     return true;
  }
}
