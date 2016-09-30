package slimeknights.tconstruct.library.client.material.deserializers;

import slimeknights.tconstruct.library.client.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.material.IMaterialRenderInfoDeserializer;

public class ColoredRenderInfoDeserializer implements IMaterialRenderInfoDeserializer {

  public String color;

  @Override
  public MaterialRenderInfo getMaterialRenderInfo() {
    int intColor = Integer.parseInt(color, 16);
    return new MaterialRenderInfo.Default(intColor);
  }
}
