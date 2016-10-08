package slimeknights.tconstruct.library.client.material;

import slimeknights.tconstruct.library.client.MaterialRenderInfo;

/** Instantiate this as your  */
public interface IMaterialRenderInfoDeserializer {

  MaterialRenderInfo getMaterialRenderInfo();

  /**
   * Suffix is the suffix after the textures to be used. Stuff like Flint etc.
   * This data is "parked" in the deserializer, for the sake of a single output from deserialization
   */
  String getSuffix();
  void setSuffix(String suffix);
}
