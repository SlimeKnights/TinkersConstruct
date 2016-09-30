package slimeknights.tconstruct.library.client.material.deserializers;

import slimeknights.tconstruct.library.client.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.material.IMaterialRenderInfoDeserializer;

public class MetalRenderInfoDeserializer extends AbstractRenderInfoDeserializer {

  protected float shinyness;
  protected float brightness;
  protected float hueshift;
  protected String color;

  @Override
  public MaterialRenderInfo getMaterialRenderInfo() {
    return new MaterialRenderInfo.Metal(fromHex(color), shinyness, brightness, hueshift);
  }
}
