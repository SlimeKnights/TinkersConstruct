package slimeknights.tconstruct.library.client.material.deserializers;

import slimeknights.tconstruct.library.client.MaterialRenderInfo;

public class BlockRenderInfoDeserializer extends AbstractRenderInfoDeserializer {

  protected String textureLocation;

  @Override
  public MaterialRenderInfo getMaterialRenderInfo() {
    return new MaterialRenderInfo.BlockTexture(textureLocation);
  }
}
