package slimeknights.tconstruct.library.client.material.deserializers;

import slimeknights.tconstruct.library.client.material.IMaterialRenderInfoDeserializer;

public abstract class AbstractRenderInfoDeserializer implements IMaterialRenderInfoDeserializer {

  private String suffix;

  protected int fromHex(String hex) {
    return Integer.parseInt(hex, 16);
  }

  @Override
  public String getSuffix() {
    return suffix;
  }

  @Override
  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }
}
