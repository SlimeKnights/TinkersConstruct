package slimeknights.tconstruct.library.materials;

/** A material variant used only for GUI things. Its textures will always be present */
public class MaterialGUI extends Material {

  public MaterialGUI(String identifier) {
    super(identifier, 0xffffff, false);
  }
}
